package io.github.discordkt.discordkt.discord.collections

class DiscordChoiceMap: MutableMap<Any, Any> {
    companion object {
        fun applyMap(vararg pairs: Pair<Any, Any>): DiscordChoiceMap {
            val newMap = DiscordChoiceMap()
            newMap.putAll(pairs)
            return newMap
        }
    }

    private val hashMap: MutableMap<Any, Any> = HashMap()

    override val entries: MutableSet<MutableMap.MutableEntry<Any, Any>>
        get() = hashMap.entries
    override val keys: MutableSet<Any>
        get() = hashMap.keys
    override val size: Int
        get() = hashMap.size
    override val values: MutableCollection<Any>
        get() = hashMap.values

    override fun clear() = hashMap.clear()
    override fun isEmpty() = hashMap.isEmpty()
    override fun remove(key: Any): Any? = hashMap.remove(key)
    override fun putAll(from: Map<out Any, Any>) = hashMap.putAll(from)
    override fun put(key: Any, value: Any): Any? = hashMap.put(key, value)

    override fun get(key: Any): Any {
        var result = hashMap[key]
        if (result == null) {
            result = ""
        }

        return result
    }

    override fun containsValue(value: Any) = hashMap.containsValue(value)
    override fun containsKey(key: Any): Boolean = hashMap.containsKey(key)
}
