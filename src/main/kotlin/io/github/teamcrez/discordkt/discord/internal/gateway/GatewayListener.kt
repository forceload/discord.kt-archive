package io.github.teamcrez.discordkt.discord.internal.gateway

import com.google.gson.Gson
import io.github.teamcrez.discordkt.client.websocket.WebSocketClient
import io.github.teamcrez.discordkt.discord.DiscordClient
import io.github.teamcrez.discordkt.discord.internal.gateway.event.GatewayEvent
import io.github.teamcrez.discordkt.discord.internal.gateway.manager.CommandManager
import io.github.teamcrez.discordkt.discord.internal.gateway.manager.HeartbeatManager
import io.github.teamcrez.discordkt.discord.internal.gateway.socket.InternalGatewayListener
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

import kotlinx.serialization.json.*

class GatewayListener(private val discordClient: DiscordClient) {

    private var gson = Gson()
    lateinit var client: WebSocketClient

    var isRunning = true
    var isDisabled = false

    val identifier = mapOf(
        "token" to discordClient.discordBot.token,
        "intents" to discordClient.discordBot.intentFlag,
        "properties" to mapOf(
            "\$os" to "DiscordBot",
            "\$browser" to "discord.kt",
            "\$device" to "discord.kt"
        )
    )

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
                            GatewayStorage.events.add(Json.decodeFromString(it))
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
            0 -> {
                if (event.t == "INTERACTION_CREATE") {
                    CommandManager.processCommand(event)
                }
            }

            7 -> {
                this.close()
            }

            10 -> {
                GatewayStorage.heartbeatInterval =
                    event.d?.get("heartbeat_interval")!!.jsonPrimitive.int

                val identifierData = Json.encodeToString(
                    GatewayEvent(2, Json.parseToJsonElement(gson.toJson(identifier)).jsonObject, null, null)
                )

                client.webSocket.send(identifierData)
            }

            11 -> {

            }
        }
    }
}
