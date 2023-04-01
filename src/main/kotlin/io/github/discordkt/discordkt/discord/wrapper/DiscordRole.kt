package io.github.discordkt.discordkt.discord.wrapper

@Suppress("MemberVisibilityCanBePrivate")
class DiscordRole(
    val id: String, val name: String,
    val color: Int, val hoist: Boolean,
    val icon: String? = null, val unicodeEmoji: String? = null,
    val position: Int, val permissions: String,
    val managed: Boolean, val mentionable: Boolean, val tags: DiscordRoleTag? = null
) {
    override fun toString(): String = if (name == "@everyone" && position == 0) { "@everyone" } else { "<@&${id}>" }
}
