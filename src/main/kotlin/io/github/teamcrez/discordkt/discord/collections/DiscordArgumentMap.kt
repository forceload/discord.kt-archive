package io.github.teamcrez.discordkt.discord.collections

import io.github.teamcrez.discordkt.discord.types.DiscordNull
import io.github.teamcrez.discordkt.discord.types.DiscordType

class DiscordArgumentMap<K>: MutableMap<K, DiscordType<*>> {
    private val hashMap: MutableMap<K, DiscordType<*>> = HashMap()

    override val entries: MutableSet<MutableMap.MutableEntry<K, DiscordType<*>>>
        get() = hashMap.entries
    override val keys: MutableSet<K>
        get() = hashMap.keys
    override val size: Int
        get() = hashMap.size
    override val values: MutableCollection<DiscordType<*>>
        get() = hashMap.values

    override fun clear() = hashMap.clear()
    override fun isEmpty() = hashMap.isEmpty()
    override fun remove(key: K): DiscordType<*>? = hashMap.remove(key)
    override fun putAll(from: Map<out K, DiscordType<*>>) = hashMap.putAll(from)
    override fun put(key: K, value: DiscordType<*>): DiscordType<*>? = hashMap.put(key, value)

    override fun get(key: K): DiscordType<*> {
        var result = hashMap[key]
        if (result == null) {
            result = DiscordNull()
        }

        return result
    }

    override fun containsValue(value: DiscordType<*>) = hashMap.containsValue(value)
    override fun containsKey(key: K): Boolean = hashMap.containsKey(key)
}
