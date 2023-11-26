package io.github.forceload.discordkt

import io.github.forceload.discordkt.command.CommandNode
import io.github.forceload.discordkt.command.internal.CommandSerializer
import io.github.forceload.discordkt.command.internal.DiscordCommand
import io.github.forceload.discordkt.exception.CommandAlreadyExistsException
import io.github.forceload.discordkt.exception.gateway.GatewaySerializationFailException
import io.github.forceload.discordkt.internal.GatewayBot
import io.github.forceload.discordkt.network.RequestUtil
import io.github.forceload.discordkt.network.WebSocketClient
import io.github.forceload.discordkt.type.gateway.DiscordPresence
import io.github.forceload.discordkt.type.gateway.GatewayEvent
import io.github.forceload.discordkt.type.gateway.GatewayIntent
import io.github.forceload.discordkt.type.gateway.PresenceStatus
import io.github.forceload.discordkt.type.gateway.event.*
import io.github.forceload.discordkt.type.gateway.event.dispatch.DiscordInteraction
import io.github.forceload.discordkt.type.gateway.event.dispatch.InteractionType
import io.github.forceload.discordkt.type.gateway.event.dispatch.Ready
import io.github.forceload.discordkt.type.gateway.event.dispatch.interaction.ApplicationCommandData
import io.github.forceload.discordkt.util.CoroutineScopes
import io.github.forceload.discordkt.util.CoroutineUtil.delay
import io.github.forceload.discordkt.util.DiscordConstants
import io.github.forceload.discordkt.util.SerializerUtil
import io.github.forceload.discordkt.util.logger.DebugLogger
import io.github.forceload.discordkt.util.logger.WarnLogger
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

fun bot(debug: Boolean = false, application: DiscordBot.() -> Unit) =
    DiscordBot(debug).also(application)

class DiscordBot(debug: Boolean) {
    lateinit var id: String
    lateinit var token: String
    val intent = mutableSetOf<GatewayIntent>()

    companion object {
        val availableInstances = ArrayList<DiscordBot>()
    }

    /**
     * Presence Variables
     *
     * https://discord.com/developers/docs/topics/gateway-events#update-presence-status-types
     */
    var status = PresenceStatus.ONLINE
        set(value) {
            var updateRequired = false
            if (field != value && running) updateRequired = true
            field = value

            if (updateRequired) updatePresence()
        }

    var afk = false
        set (value) {
            var updateRequired = false
            if (field != value && running) updateRequired = true
            field = value

            if (updateRequired) updatePresence()
        }

    var since: Int? = null
        set (value) {
            var updateRequired = false
            if (field != value && running) updateRequired = true
            field = value

            if (updateRequired) updatePresence()
        }

    init {
        DebugLogger.enabled = debug
    }

    private val commandMap = HashMap<String, CommandNode>()
    fun command(name: String, code: CommandNode.() -> Unit) {
        val commandNode = CommandNode(name, token).also(code)
        if (name in commandMap.keys) {
            throw CommandAlreadyExistsException(name)
        }

        commandMap[name] = commandNode
    }

    var running: Boolean = false
    private var authenticated = false
    private var clientInitialized = false
    private lateinit var client: WebSocketClient
    fun run(commandOptionMaxDepth: Int = 16, heartbeatTimeScale: Double = 0.95) {
        val commands = RequestUtil.get("applications/${id}/commands", token, "with_localizations" to true)
        SerializerUtil.commandOptionMaxDepth = commandOptionMaxDepth

        availableInstances.add(this)

        DebugLogger.log(commands.dropLast(1))
        val commandList = SerializerUtil.jsonBuild.decodeFromString<ArrayList<DiscordCommand>>(commands)

        DebugLogger.log(commandList)

        val iterator = commandList.iterator()
        while (iterator.hasNext()) {
            val command = iterator.next()
            if (command.name !in commandMap) {
                command.destroy(token)
                iterator.remove()
            } else {
                val generated = commandMap[command.name]!!.generateCommand()
                if (generated != command) {
                    val serialized = SerializerUtil.jsonBuild.encodeToString(CommandSerializer, generated)
                    val post = RequestUtil.post("applications/$id/commands", token, serialized)

                    DebugLogger.log(serialized)
                    DebugLogger.log(generated)
                    DebugLogger.log(post.dropLast(1))
                }
            }
        }

        val newCommands = commandMap.keys.toMutableList()
        newCommands.removeAll(commandList.map { it.name }.toSet())
        for (command in newCommands) {
            val generated = commandMap[command]!!.generateCommand()
            val serialized = SerializerUtil.jsonBuild.encodeToString(CommandSerializer, generated)
            val post = RequestUtil.post("applications/$id/commands", token, serialized)

            DebugLogger.log(serialized)
            DebugLogger.log(generated)
            DebugLogger.log(post.dropLast(1))
        }

        val gatewayBotData = RequestUtil.get("gateway/bot", token)
        val gatewayBot = SerializerUtil.jsonBuild.decodeFromString<GatewayBot>(gatewayBotData)
        DebugLogger.log(gatewayBot)

        var seqNum = 0

        running = true
        lateinit var sessionID: String
        var webSocketURL = gatewayBot.url
        CoroutineScopes.wsScope.launch {
            while (running) {
                if (gatewayBot.sessionStartLimit.remaining <= 0) {
                    WarnLogger.log("The bot will be launched in ${gatewayBot.sessionStartLimit.resetAfter}ms due to the current unavailability of the Gateway protocol.")
                    delay(gatewayBot.sessionStartLimit.resetAfter)
                }

                var restart = false
                client = WebSocketClient.newInstance(webSocketURL, version = DiscordConstants.apiVersion)
                clientInitialized = true

                client.launch client@{ messages ->
                    val currentTime = Clock.System.now().toEpochMilliseconds()

                    messages.forEach { message ->
                        val event: GatewayEvent
                        try {
                            try {
                                event = SerializerUtil.jsonBuild.decodeFromString<GatewayEvent>(message)
                                event.auth = token
                            } catch (err: GatewaySerializationFailException) {
                                WarnLogger.log(err.stackTraceToString())
                                return@forEach
                            }
                        } catch (err: Throwable) { // INTERNAL_ERROR
                            this.close("${err::class.qualifiedName}: ${err.message ?: "Unknown error"}", 1001)
                            clientInitialized = false

                            WarnLogger.log(err.stackTraceToString())
                            WarnLogger.log("The bot will be shut down in a few seconds...")
                            return@client
                        }

                        if (event.s != null) seqNum = event.s!!
                        DebugLogger.log(event)

                        when (event.op) {
                            DiscordConstants.OpCode.HELLO -> {
                                heartbeatInterval = (event.d as Hello).heartbeatInterval * heartbeatTimeScale
                                latestHeartbeat = currentTime

                                sendHeartbeat(null)
                                if (!restart) {
                                    send(Identify(
                                        token, largeThreshold = 50, intent = intent, presence = DiscordPresence(
                                            null, arrayOf(), status, afk
                                        )
                                    ))
                                } else send(Resume(token, sessionID, seqNum))

                                prepared = true
                            }

                            DiscordConstants.OpCode.RECONNECT -> {
                                this.close(1000)
                                clientInitialized = false
                                restart = true
                            }

                            DiscordConstants.OpCode.HEARTBEAT -> {
                                sendHeartbeat(seqNum)
                                latestHeartbeat = currentTime
                            }

                            DiscordConstants.OpCode.DISPATCH -> when (event.t) {
                                "READY" -> {
                                    val readyEvent = event.d as Ready
                                    webSocketURL = readyEvent.resumeGatewayURL
                                    sessionID = readyEvent.sessionID
                                    authenticated = true
                                }

                                "INTERACTION_CREATE" -> {
                                    val interaction = event.d as DiscordInteraction

                                    when (interaction.type) {
                                        InteractionType.APPLICATION_COMMAND -> {
                                            val commandData = interaction.data as ApplicationCommandData
                                            commandMap[commandData.name]?.run(interaction)
                                        }

                                        else -> {}
                                    }
                                }
                            }
                        }
                    }

                    if (prepared && currentTime - latestHeartbeat >= heartbeatInterval) {
                        sendHeartbeat(seqNum)
                        DebugLogger.log("Heartbeat Sent: Time: ${currentTime}, Delay: ${currentTime - latestHeartbeat}")
                        latestHeartbeat = currentTime
                    }

                    if (closeCode != null) {
                        if (closeCode!!.first != null) this.close(closeCode!!.first!!, closeCode!!.second)
                        else this.close(closeCode!!.second)
                        clientInitialized = false

                        DebugLogger.log("Bot Stop Code: $closeCode")
                        restart = false
                    }
                }

                if (!restart) {
                    availableInstances.remove(this@DiscordBot)
                    clientInitialized = false
                    authenticated = false
                    prepared = false
                    running = false
                }
            }
        }
    }

    fun runBlocking(commandOptionMaxDepth: Int = 16, heartbeatTimeScale: Double = 0.95) =
        kotlinx.coroutines.runBlocking {
            run(commandOptionMaxDepth, heartbeatTimeScale)
            while (running) delay(100)
        }

    private var closeCode: Pair<String?, Short>? = null

    private fun close(code: Short, message: String? = null) { this.closeCode = Pair(message, code) }
    fun stop(message: String? = null) = this.close(1000, message)

    private var prepared = false
    private var heartbeatInterval = 45000.0
    private var latestHeartbeat = -1L

    private fun sendHeartbeat(seqNum: Int?) {
        client.send(GatewayEvent(1, d = Heartbeat(seqNum)), GatewayEvent.Serializer)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun updatePresence() {
        if (running) GlobalScope.launch {
            while (!clientInitialized || !authenticated) delay(50)
            client.send(GatewayEvent(3, d = UpdatePresence(
                since = since, activities = arrayOf(), status = status, afk = afk
            )))
        }
    }
}