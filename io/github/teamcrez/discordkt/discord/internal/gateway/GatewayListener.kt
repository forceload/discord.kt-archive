package io.github.teamcrez.discordkt.discord.internal.gateway

import com.google.gson.Gson
import io.github.teamcrez.discordkt.client.websocket.WebSocketClient
import io.github.teamcrez.discordkt.discord.DiscordClient
import io.github.teamcrez.discordkt.discord.internal.command.context.CommandData
import io.github.teamcrez.discordkt.discord.internal.command.CommandStorage
import io.github.teamcrez.discordkt.discord.internal.command.context.CommandContext
import io.github.teamcrez.discordkt.discord.internal.gateway.event.GatewayEvent
import io.github.teamcrez.discordkt.discord.internal.gateway.socket.InternalGatewayListener
import io.github.teamcrez.discordkt.discord.wrapper.DiscordChannel
import io.github.teamcrez.discordkt.discord.wrapper.DiscordInteraction
import io.github.teamcrez.discordkt.discord.wrapper.DiscordUser
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

import kotlinx.serialization.json.*

class GatewayListener(private val discordClient: DiscordClient) {

    private var gson = Gson()
    private lateinit var client: WebSocketClient
    private var isRunning = true

    private var heartbeatTimestamp: Long = 0

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun run() {
        client = WebSocketClient("wss://gateway.discord.gg/?v=10&encoding=json", InternalGatewayListener())

        GlobalScope.launch(Dispatchers.Default) {
            launch {
                while (true) {
                    if (GatewayStorage.heartbeatInterval != -1) { break }
                    delay(1)
                }

                while (isActive && isRunning) {
                    client.webSocket.send(Json.encodeToString(
                        GatewayEvent(1, null, GatewayStorage.sequenceNumber, null)
                    ))

                    delay((GatewayStorage.heartbeatInterval * 0.5).toLong())
                }
            }

            launch {
                while (isActive && isRunning) {
                    if (GatewayStorage.events.isNotEmpty()) {
                        GatewayStorage.events.forEach {
                            processGatewayEvent(it)
                        }

                        GatewayStorage.events.clear()
                    }
                    delay(1)
                }
            }

            launch {
                while (isActive && isRunning) {
                    if (GatewayStorage.messages.isNotEmpty()) {
                        GatewayStorage.messages.forEach {
                            GatewayStorage.events.add(Json.decodeFromString(it))
                        }

                        GatewayStorage.messages.clear()
                    }
                    delay(1)
                }
            }
        }
    }

    fun disable() {
        isRunning = false
        client.disable()
    }

    private fun processGatewayEvent(event: GatewayEvent) {
        GatewayStorage.sequenceNumber = event.s

        println(event)
        when (event.op) {
            0 -> {
                if (event.t == "INTERACTION_CREATE") {
                    val commandName = event.d?.get("data")!!.jsonObject["name"]!!.jsonPrimitive.content

                    if (CommandStorage.commandProcesses.keys.contains(commandName)) {
                        val commandComponent = CommandStorage.commandProcesses[commandName]!!
                        commandComponent[commandComponent.keys.first()]?.let {it(
                            CommandData(
                                CommandContext(
                                    DiscordUser(event.d["member"]!!.jsonObject["user"]!!.jsonObject["id"]!!.jsonPrimitive.content),
                                    DiscordChannel(event.d["channel_id"]!!.jsonPrimitive.content),

                                    DiscordInteraction(event.d["id"]!!.jsonPrimitive.content, event.d["token"]!!.jsonPrimitive.content)
                                )
                            )
                        )}
                    }
                }
            }

            10 -> {
                GatewayStorage.heartbeatInterval =
                    event.d?.get("heartbeat_interval")!!.jsonPrimitive.int

                val identifierData = Json.encodeToString(
                    GatewayEvent(2, Json.parseToJsonElement(gson.toJson(
                        mapOf(
                            "token" to discordClient.discordBot.token,
                            "intents" to discordClient.discordBot.intentFlag,
                            "properties" to mapOf(
                                "\$os" to "DiscordBot",
                                "\$browser" to "discord.kt",
                                "\$device" to "discord.kt"
                            )
                        )
                    )).jsonObject, null, null)
                )

                client.webSocket.send(identifierData)
            }

            11 -> {

            }
        }
    }
}