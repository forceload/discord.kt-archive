package io.github.teamcrez.discordkt.discord.types

import io.github.teamcrez.discordkt.discord.api.DiscordFlags

class DiscordString(override val coveredValue: String): DiscordType<String> {
    override val type = DiscordFlags.CommandArgumentType.STRING
    override fun toString() = this.coveredValue
}
