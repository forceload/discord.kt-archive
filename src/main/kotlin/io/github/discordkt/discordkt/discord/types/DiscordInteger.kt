package io.github.discordkt.discordkt.discord.types

import io.github.discordkt.discordkt.discord.api.DiscordFlags

class DiscordInteger(override val coveredValue: Long): DiscordType<Long> {
    override val type = DiscordFlags.CommandArgumentType.INTEGER
    override fun toString() = this.coveredValue.toString()
}
