package io.github.discordkt.discordkt.discord.internal.gateway.socket

import io.github.discordkt.discordkt.discord.internal.gateway.GatewayStorage
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class InternalGatewayListener: WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        if (GatewayStorage.gatewayDebug) { println("Gateway Listening!") }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        GatewayStorage.messages.add(text)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
        GatewayStorage.messages.add(bytes.utf8())
    }
}
