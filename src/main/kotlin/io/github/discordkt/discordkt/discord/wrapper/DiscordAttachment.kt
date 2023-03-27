package io.github.discordkt.discordkt.discord.wrapper

class DiscordAttachment(
    val id: String, val filename: String, val description: String = "",
    val contentType: String = "text/plain", val size: Int, val url: String, val proxyUrl: String,
    val width: Int? = null, val height: Int? = null, val ephemeral: Boolean = false
) {
    override fun toString() = url
}