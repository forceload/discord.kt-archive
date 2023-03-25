package io.github.teamcrez.discordkt.discord.types

import io.github.teamcrez.discordkt.discord.api.DiscordFlags

class DiscordInteger(override val coveredValue: Long): DiscordType<Long> {
    override val type = DiscordFlags.CommandArgumentType.INTEGER
    override fun toString() = this.coveredValue.toString()
}
