package io.github.forceload.discordkt.type.collection.cache

import io.github.forceload.discordkt.type.DiscordChannel

object DMCache {
    private val cacheMap = HashMap<String, CacheMap<DiscordChannel>>()

    operator fun get(token: String, id: String) = cacheMap[token]?.get(id)
    operator fun set(token: String, id: String, validUntil: Long = -1, value: DiscordChannel) {
        if (cacheMap[token] == null) cacheMap[token] = CacheMap("DM Channel")
        cacheMap[token]!![id, validUntil] = value
    }

    fun checkCache(token: String, id: String, validUntil: Long = -1) =
        if (cacheMap[token] == null) { cacheMap[token] = CacheMap("DM Channel"); false }
        else cacheMap[token]!!.checkCache(id, validUntil)
}