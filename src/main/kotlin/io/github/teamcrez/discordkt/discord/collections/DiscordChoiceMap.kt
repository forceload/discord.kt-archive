package io.github.teamcrez.discordkt.discord.collections

class DiscordChoiceMap<K>: MutableMap<K, Any> {
    private val hashMap: MutableMap<K, Any> = HashMap()

    override val entries: MutableSet<MutableMap.MutableEntry<K, Any>>
        get() = hashMap.entries
    override val keys: MutableSet<K>
        get() = hashMap.keys
    override val size: Int
        get() = hashMap.size
    override val values: MutableCollection<Any>
        get() = hashMap.values

    override fun clear() = hashMap.clear()
    override fun isEmpty() = hashMap.isEmpty()
    override fun remove(key: K): Any? = hashMap.remove(key)
    override fun putAll(from: Map<out K, Any>) = hashMap.putAll(from)
    override fun put(key: K, value: Any): Any? = hashMap.put(key, value)

    fun applyMap(map: MutableMap<K, Any>): DiscordChoiceMap<K> {
        this.putAll(map)
        return this
    }

    override fun get(key: K): Any {
        var result = hashMap[key]
        if (result == null) {
            result = ""
        }

        return result
    }

    override fun containsValue(value: Any) = hashMap.containsValue(value)
    override fun containsKey(key: K): Boolean = hashMap.containsKey(key)
}
