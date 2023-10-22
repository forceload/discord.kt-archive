package io.github.forceload.discordkt.util

import kotlinx.coroutines.*

object CoroutineUtil {
    private const val maxWebSocket = DiscordConstants.maxConnections

    @OptIn(ExperimentalCoroutinesApi::class)
    val webSocketDispatcher = Dispatchers.IO.limitedParallelism(maxWebSocket)
    val webSocketScope = CoroutineScope(webSocketDispatcher)

    @Suppress("UnusedReceiverParameter")
    suspend fun CoroutineScope.delay(timeMillis: Int) = delay(timeMillis.toLong())
}