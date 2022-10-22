package io.github.teamcrez.discordkt.discord.internal.gateway

import com.google.gson.Gson
import io.github.teamcrez.discordkt.client.websocket.WebSocketClient
import io.github.teamcrez.discordkt.discord.DiscordClient
import io.github.teamcrez.discordkt.discord.api.DiscordFlags
import io.github.teamcrez.discordkt.discord.internal.gateway.event.GatewayEvent
import io.github.teamcrez.discordkt.discord.internal.gateway.manager.CommandManager
import io.github.teamcrez.discordkt.discord.internal.gateway.manager.HeartbeatManager
import io.github.teamcrez.discordkt.discord.internal.gateway.socket.InternalGatewayListener
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class GatewayListener(private val discordClient: DiscordClient) {

    private var gson = Gson()
    lateinit var client: WebSocketClient

    var isRunning = true
    var isDisabled = false

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun run() {
        client = WebSocketClient("wss://gateway.discord.gg/?v=10&encoding=json", InternalGatewayListener())

        if (!isDisabled) { isRunning = true }
        GlobalScope.launch(Dispatchers.Default) {
            launch {
                HeartbeatManager.heartbeatLoop(this, this@GatewayListener)
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
                            try {
                                GatewayStorage.events.add(Json.decodeFromString(it))
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
                    GatewayEvent(2, Json.parseToJsonElement(gson.toJson(identifier)).jsonObject, null, null)
                )

                client.webSocket.send(identifierData)
            }

            DiscordFlags.Opcode.HEARTBEAT_ACK -> {

            }
        }
    }
}
