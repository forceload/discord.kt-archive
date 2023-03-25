package io.github.discordkt.discordkt.discord.types

import io.github.discordkt.discordkt.discord.api.DiscordFlags

class DiscordBoolean(override val coveredValue: Boolean): DiscordType<Boolean> {
    override val type = DiscordFlags.CommandArgumentType.BOOLEAN
    override fun toString() = this.coveredValue.toString()
}
