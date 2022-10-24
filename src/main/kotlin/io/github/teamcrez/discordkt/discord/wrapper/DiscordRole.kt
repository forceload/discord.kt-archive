package io.github.teamcrez.discordkt.discord.wrapper

@Suppress("MemberVisibilityCanBePrivate")
class DiscordRole(
    val id: String, val name: String,
    val color: Int, val hoist: Boolean,
    val icon: String? = null, val unicode_emoji: String? = null,
    val position: Int, val permissions: String,
    val managed: Boolean, val mentionable: Boolean, val tags: DiscordRoleTag? = null
)
