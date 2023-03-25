package io.github.discordkt.discordkt.discord.types

import io.github.discordkt.discordkt.discord.api.DiscordFlags

class DiscordNumber(override val coveredValue: Double): DiscordType<Double> {
    override val type = DiscordFlags.CommandArgumentType.NUMBER
    override fun toString() = this.coveredValue.toString()
}
