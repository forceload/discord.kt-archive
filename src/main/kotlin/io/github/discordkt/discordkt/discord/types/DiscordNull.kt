package io.github.discordkt.discordkt.discord.types

import io.github.discordkt.discordkt.discord.api.DiscordFlags

class DiscordNull: DiscordType<Unit?> {
    override val coveredValue = null
    override val type = DiscordFlags.CommandArgumentType.NULL
    override fun toString() = "null"
}
