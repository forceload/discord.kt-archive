package io.github.teamcrez.discordkt.discord.wrapper

import io.github.teamcrez.discordkt.discord.APIRequester

data class DiscordInteraction(val id: String, val token: String) {
    fun interact(type: Int?, params: Map<*, *>? = HashMap<Any, Any>()) {
        if (type == null) {
            APIRequester.postRequest("interactions/$id/$token/callback", mapOf(
                "type" to 1,
                "data" to params
            ))
        } else {
            APIRequester.postRequest("interactions/$id/$token/callback", mapOf(
                "type" to type,
                "data" to params
            ))
        }
    }

    fun reply(message: String, flags: Int) = interact(
        type = 4, mapOf(
            "content" to message,
            "flags" to flags
        )
    )

    fun reply(message: String, flags: Short) = reply(message, flags.toInt())
    fun reply(message: String) = reply(message, 0)
}