package io.github.discordkt.discordkt.discord.wrapper
import io.github.discordkt.discordkt.discord.APIRequester
import io.github.discordkt.discordkt.discord.api.DiscordFlags
import kotlinx.serialization.json.*

@Suppress("MemberVisibilityCanBePrivate")
data class DiscordChannel(val id: String, val guildId: String? = null) {
    var isValid = true
    val channelType: DiscordFlags.ChannelType
    private val internalChannelType: JsonElement? = Json.parseToJsonElement(
        APIRequester.getRequest("channels/$id")["data"]!!.jsonPrimitive.content
    ).jsonObject["type"]

    // val

    init {
        if (internalChannelType == null) {
            isValid = false
            channelType = DiscordFlags.ChannelType.NULL
        } else {
            channelType = DiscordFlags.matchChannelType(internalChannelType.jsonPrimitive.int)
        }
    }

    fun sendMessage(message: String) {
        val messageData = mapOf("content" to message)
        APIRequester.postRequest("channels/$id/messages", messageData)
    }

    override fun toString(): String {
        return "<#${id}>"
    }
}
