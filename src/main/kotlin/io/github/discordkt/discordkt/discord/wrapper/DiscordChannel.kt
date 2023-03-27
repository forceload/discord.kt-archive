package io.github.discordkt.discordkt.discord.wrapper
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import io.github.discordkt.discordkt.discord.APIRequester
import io.github.discordkt.discordkt.discord.api.DiscordFlags
import kotlinx.serialization.json.jsonPrimitive

@Suppress("MemberVisibilityCanBePrivate")
data class DiscordChannel(val id: String) {
    var isValid = true
    val channelType: DiscordFlags.ChannelType
    private val internalChannelType: JsonElement? = JsonParser.parseString(
        APIRequester.getRequest("channels/$id")["data"]!!.jsonPrimitive.content
    ).asJsonObject["type"]

    init {
        if (internalChannelType == null) {
            isValid = false
            channelType = DiscordFlags.ChannelType.NULL
        } else {
            channelType = DiscordFlags.matchChannelType(internalChannelType.asJsonPrimitive.asInt)
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
