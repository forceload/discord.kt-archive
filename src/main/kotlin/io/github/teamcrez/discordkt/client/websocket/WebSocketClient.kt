package io.github.teamcrez.discordkt.client.websocket

import io.github.teamcrez.discordkt.discord.api.DiscordFlags
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class WebSocketClient(url: String, listener: WebSocketListener) {

    val webSocket: WebSocket
    private val client: OkHttpClient = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .readTimeout(DiscordFlags.GatewayFlag.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(DiscordFlags.GatewayFlag.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .connectTimeout(DiscordFlags.GatewayFlag.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .build()
    private val request: Request

    init {
        request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, listener)
        client.dispatcher.executorService.shutdown()
    }

    fun disable() {
        webSocket.close(0, "Client Disabled")
    }

}
