package io.github.forceload.discordkt.network

import io.github.forceload.discordkt.type.gateway.GatewayEvent
import io.github.forceload.discordkt.type.gateway.event.GatewayEventType
import io.github.forceload.discordkt.util.SerializerUtil
import io.github.forceload.discordkt.util.logger.DebugLogger
import io.github.forceload.discordkt.util.logger.WarnLogger
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.KSerializer

actual class WebSocketClient actual constructor(
    actual val host: String, actual val url: String, actual val params: HashMap<String, Any>
) {
    init {
        println(url)
    }

    actual companion object {
        actual val client: HttpClient = TODO("HI")

        actual fun convertURL(url: String) =
            if (url.startsWith("ws://")) { url.drop(5) }
            else if (url.startsWith("wss://")) { url.drop(6) }
            else { url }

        actual fun newInstance(host: String, url: String, version: Int) =
            WebSocketClient(convertURL(host), url, hashMapOf("v" to version, "encoding" to "json"))
    }

    actual val events = ArrayDeque<String>()
    actual val messageQueue = ArrayDeque<String>()
    actual var isRunning = false

    actual var session: DefaultClientWebSocketSession? = null
    actual var reason: CloseReason? = null

    actual fun send(message: String) = messageQueue.add(message)
    actual fun <T> send(obj: T, serializer: KSerializer<T>): Boolean =
        send(SerializerUtil.jsonBuild.encodeToString(serializer, obj))

    actual fun send(message: GatewayEvent) = send(message, GatewayEvent.Serializer)
    actual fun send(message: GatewayEventType): Boolean = send(GatewayEvent(message.opCode, message))

    actual fun close(reason: CloseReason) {
        this.reason = reason
        isRunning = false
    }

    actual fun close(code: Short) = close("", code)
    actual fun close(reason: String) = close(reason, CloseReason.Codes.NORMAL)
    actual fun close(reason: String, code: CloseReason.Codes): Unit = close(CloseReason(code, reason))
    actual fun close(reason: String, code: Short): Unit = close(CloseReason(code, reason))

    @OptIn(ExperimentalCoroutinesApi::class)
    actual suspend fun launch(code: WebSocketClient.(messages: Array<String>) -> Unit) {
        if (isRunning) return
        else isRunning = true

        val paramUrl = StringBuilder()
        params.onEachIndexed { index, entry ->
            paramUrl.append(if (index == 0) "?" else "&")
            paramUrl.append("${entry.key}=${entry.value}")
        }

        @Suppress("LocalVariableName")
        val IOScope = CoroutineScope(Dispatchers.IO)

        client.webSocket(
            method = HttpMethod.Get, host = host, path = "/${url}${paramUrl}"
        ) client@ {
            session = this
            while (isRunning) {
                var i = 0
                try {
                    if (!incoming.isEmpty) IOScope.launch {
                        loop@ while (!incoming.isEmpty) {
                            val message = incoming.receive() as? Frame.Text? ?: break@loop
                            val msgString = message.readText()
                            DebugLogger.log("Receive ${i++}: $msgString")
                            events.add(msgString)
                        }
                    }
                } catch (err: ClosedReceiveChannelException) {
                    val reason = this.closeReason.await()!!
                    WarnLogger.log("Close Code: ${reason.code}\nMessage: ${reason.message}")
                    return@client
                }

                this@WebSocketClient.code(events.clean())

                i = 0
                while (messageQueue.isNotEmpty()) {
                    val msgString = messageQueue.removeFirst()
                    DebugLogger.log("Send ${i++}: $msgString")
                    IOScope.launch { send(msgString) }
                }

                if ((!isRunning || !this.isActive) && reason != null) {
                    val knownReason = when (reason!!.knownReason) {
                        CloseReason.Codes.GOING_AWAY -> {
                            if (!isRunning) CloseReason.Codes.INTERNAL_ERROR
                            else reason!!.knownReason
                        }

                        else -> reason!!.knownReason
                    }

                    WarnLogger.log("${knownReason}: ${reason!!.message}")
                } else if (!this.isActive) {
                    val reason = this.closeReason.await()!!
                    WarnLogger.log("Close Code: ${reason.code}\nMessage: ${reason.message}")
                    return@client
                }
            }

            this.close(reason!!)
        }
    }
}