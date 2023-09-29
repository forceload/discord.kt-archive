package io.github.forceload.discordkt.util

object CollectionUtil {
    fun <K, V> MutableMap<K, V>.add(pair: Pair<K, V>) { this[pair.first] = pair.second }
}