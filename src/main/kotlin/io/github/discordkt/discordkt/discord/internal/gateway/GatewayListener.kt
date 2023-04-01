package io.github.discordkt.discordkt.discord.internal.gateway

import io.github.discordkt.discordkt.client.websocket.WebSocketClient
import io.github.discordkt.discordkt.discord.DiscordClient
import io.github.discordkt.discordkt.discord.api.DiscordFlags
import io.github.discordkt.discordkt.discord.internal.gateway.event.GatewayEvent
import io.github.discordkt.discordkt.discord.internal.gateway.manager.CommandManager
import io.github.discordkt.discordkt.discord.internal.gateway.manager.HeartbeatManager
import io.github.discordkt.discordkt.discord.internal.gateway.socket.InternalGatewayListener
import io.github.discordkt.discordkt.serializer.AnySerializer
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlinx.serialization.json.Json.Default.encodeToJsonElement

@Suppress("unused")
class GatewayListener(private val discordClient: DiscordClient) {
    lateinit var client: WebSocketClient

    var isRunning = true
    var isDisabled = false

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun run() {
        client = WebSocketClient("wss://gateway.discord.gg/?v=10&encoding=json", InternalGatewayListener())

        val eventMutex = Mutex(locked = false)
        if (!isDisabled) { isRunning = true }
        GlobalScope.launch(Dispatchers.Default) {
            launch {
                HeartbeatManager.heartbeatLoop(this, this@GatewayListener)
            }

            launch {
                while (isActive && isRunning) {
                    if (GatewayStorage.events.isNotEmpty()) {
                        eventMutex.withLock {}

                        eventMutex.lock()
                        GatewayStorage.events.forEach { processGatewayEvent(it) }
                        eventMutex.unlock()

                        GatewayStorage.events.clear()
                    }
                    delay(1)
                }
            }

            launch {
                while (isActive && isRunning) {
                    if (GatewayStorage.messages.isNotEmpty()) {
                        GatewayStorage.messages.forEach {
                            try {
                                eventMutex.withLock {}

                                eventMutex.lock()
                                GatewayStorage.events.add(Json.decodeFromString(it))
                                eventMutex.unlock()
                            } catch (err: Exception) {
                                println(it)
                                this@GatewayListener.close()
                            }
                        }

                        GatewayStorage.messages.clear()
                    }
                    delay(1)
                }
            }
        }
    }

    private fun close() {
        isRunning = false
        client.disable()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun disable() {
        isRunning = false
        isDisabled = true
        client.disable()
    }

    private fun processGatewayEvent(event: GatewayEvent) {
        GatewayStorage.sequenceNumber = event.s

        if (discordClient.debug) { println(event) }
        when (event.op) {
            DiscordFlags.Opcode.DISPATCH -> {
                if (event.t == "INTERACTION_CREATE") {
                    CommandManager.processCommand(event)
                }
            }

            DiscordFlags.Opcode.RECONNECT -> {
                this.close()
            }

            DiscordFlags.Opcode.HELLO -> {
                GatewayStorage.heartbeatInterval =
                    event.d?.get("heartbeat_interval")!!.jsonPrimitive.int

                val identifier = mapOf(
                    "token" to discordClient.discordBot.token,
                    "intents" to discordClient.discordBot.intentFlag,
                    "properties" to mapOf(
                        "\$os" to "DiscordBot",
                        "\$browser" to "discord.kt",
                        "\$device" to "discord.kt"
                    )
                )

                val identifierData = Json.encodeToString(
                    GatewayEvent(2, Json.encodeToJsonElement(AnySerializer, identifier).jsonObject, null, null)
                )

                client.webSocket.send(identifierData)
            }

            DiscordFlags.Opcode.HEARTBEAT_ACK -> {

            }
        }
    }
}
