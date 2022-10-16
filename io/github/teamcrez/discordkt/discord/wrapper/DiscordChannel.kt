package io.github.teamcrez.discordkt.discord.wrapper
import io.github.teamcrez.discordkt.discord.APIRequester

data class DiscordChannel(val id: String) {
    fun sendMessage(message: String) {
        val messageData = mapOf("content" to message)
        APIRequester.postRequest("channels/$id/messages", messageData)
    }
}