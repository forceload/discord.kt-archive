package io.github.teamcrez.discordkt.client.websocket

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class WebSocketClient(url: String, listener: WebSocketListener) {

    val webSocket: WebSocket
    private val defaultTimeout: Long = 5
    private val client: OkHttpClient = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .readTimeout(defaultTimeout, TimeUnit.SECONDS)
        .writeTimeout(defaultTimeout, TimeUnit.SECONDS)
        .connectTimeout(defaultTimeout, TimeUnit.SECONDS)
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
