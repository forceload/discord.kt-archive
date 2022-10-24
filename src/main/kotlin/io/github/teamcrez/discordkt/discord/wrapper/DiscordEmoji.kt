package io.github.teamcrez.discordkt.discord.wrapper

@Suppress("MemberVisibilityCanBePrivate")
class DiscordEmoji(
    val id: String? = null, val name: String? = null,
    val roles: ArrayList<DiscordRole> = ArrayList(), val user: DiscordUser? = null,
    val require_colons: Boolean? = null, val managed: Boolean? = null,
    val animated: Boolean? = null, val available: Boolean? = null
)
