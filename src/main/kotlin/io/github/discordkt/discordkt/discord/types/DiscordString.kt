package io.github.discordkt.discordkt.discord.types

import io.github.discordkt.discordkt.discord.api.DiscordFlags

class DiscordString(override val coveredValue: String): DiscordType<String> {
    override val type = DiscordFlags.CommandArgumentType.STRING
    override fun toString() = this.coveredValue
}
