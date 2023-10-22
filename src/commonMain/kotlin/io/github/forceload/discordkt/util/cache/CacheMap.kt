package io.github.forceload.discordkt.util.cache

import io.github.forceload.discordkt.util.CoroutineUtil.delay
import io.github.forceload.discordkt.util.logger.DebugLogger
import io.github.forceload.discordkt.util.logger.WarnLogger
import io.ktor.client.utils.*
import io.ktor.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class CacheMap<T>(private val cacheName: String) {
    private val cache = HashMap<String, Pair<T, Long>>()

    companion object {
        private const val maxCacheMaps = 128

        @OptIn(InternalAPI::class)
        val cacheDispatcher = Dispatchers.clientDispatcher(maxCacheMaps / 8, "CacheDispatcher")
        val cacheScope = CoroutineScope(cacheDispatcher)
    }

    init {
        cacheScope.launch {
            while (true) {
                delay(50)

                try {
                    val iterator = cache.iterator()
                    val current = Clock.System.now().toEpochMilliseconds()
                    while (iterator.hasNext()) {
                        val entry = iterator.next()
                        if (entry.value.second != -1L && entry.value.second < current) {
                            DebugLogger.log("Removing $cacheName Cache ${entry.key}")
                            iterator.remove()
                        }
                    }
                } catch (err: Exception) { WarnLogger.log(err.stackTraceToString()); break }
            }
        }
    }

    operator fun get(id: String): T = try { cache[id]!!.first!! }
        catch (err: NullPointerException) { throw InvalidCacheLoadException("Invalid $cacheName Cache ID: $id") }

    operator fun set(id: String, validUntil: Long = -1, value: T) { cache[id] = Pair(
        value, if (validUntil == -1L) validUntil else validUntil + Clock.System.now().toEpochMilliseconds()
    )}

    fun checkCache(id: String, validUntil: Long = -1): Boolean {
        return if (cache[id] != null) { cache[id] = cache[id]!!.copy(
            second = if (validUntil == -1L) validUntil else validUntil + Clock.System.now().toEpochMilliseconds()
        ); true } else false
    }
}

class InvalidCacheLoadException(private val text: String) : Throwable() {
    override val message: String
        get() = text
}
