package io.github.teamcrez.discordkt.client.websocket

import okhttp3.*
import java.util.concurrent.TimeUnit

class WebSocketClient(url: String, listener: WebSocketListener) {

    val webSocket: WebSocket
    private val client: OkHttpClient = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .connectTimeout(5, TimeUnit.SECONDS)
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
