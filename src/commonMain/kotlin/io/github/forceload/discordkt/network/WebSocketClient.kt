package io.github.forceload.discordkt.network

import io.github.forceload.discordkt.type.gateway.GatewayEvent
import io.github.forceload.discordkt.type.gateway.event.GatewayEventType
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.KSerializer

expect class WebSocketClient(host: String, url: String, params: HashMap<String, Any>) {
    val host: String
    val url: String
    val params: HashMap<String, Any>

    companion object {
        internal val client: HttpClient
        internal fun convertURL(url: String): String

        internal fun newInstance(host: String, url: String = "", version: Int): WebSocketClient
    }

    internal val events: ArrayDeque<String>
    internal val messageQueue: ArrayDeque<String>
    internal var isRunning: Boolean

    internal var session: DefaultClientWebSocketSession?
    internal var reason: CloseReason?

    fun send(message: String): Boolean
    fun <T> send(obj: T, serializer: KSerializer<T>): Boolean

    fun send(message: GatewayEvent): Boolean
    fun send(message: GatewayEventType): Boolean

    fun close(reason: CloseReason = CloseReason(CloseReason.Codes.NORMAL, "Closed Normally"))

    fun close(code: Short)
    fun close(reason: String)
    fun close(reason: String, code: CloseReason.Codes)
    fun close(reason: String, code: Short)

    suspend fun launch(code: WebSocketClient.(messages: Array<String>) -> Unit): Unit
}

inline fun <reified E> ArrayDeque<E>.clean(): Array<E> {
    val length = this.size
    val arrayList = ArrayList<E>()
    for (i in 0..<length) arrayList.add(this.removeFirst())
    return arrayList.toTypedArray()
}
