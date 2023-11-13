package io.github.forceload.discordkt.util

import kotlinx.coroutines.*

object CoroutineUtil {
    @Suppress("UnusedReceiverParameter")
    suspend fun CoroutineScope.delay(timeMillis: Int) = delay(timeMillis.toLong())
}

@OptIn(ExperimentalCoroutinesApi::class)
object CoroutineScopes {
    private const val COMMAND_THREADS = DiscordConstants.MAX_COMMAND_THREADS
    private const val HTTP_THREADS = DiscordConstants.MAX_HTTP_THREADS
    private const val WS_THREADS = DiscordConstants.MAX_WS_THREADS

    val commandDispatcher = Dispatchers.IO.limitedParallelism(COMMAND_THREADS)
    val httpDispatcher = Dispatchers.IO.limitedParallelism(HTTP_THREADS)
    val wsDispatcher = Dispatchers.IO.limitedParallelism(WS_THREADS)

    val commandScope = CoroutineScope(commandDispatcher)
    val httpScope = CoroutineScope(httpDispatcher)
    val wsScope = CoroutineScope(wsDispatcher)
}