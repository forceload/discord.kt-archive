package io.github.discordkt.discordkt.discord.types

import io.github.discordkt.discordkt.discord.api.DiscordFlags
import io.github.discordkt.discordkt.discord.wrapper.DiscordRole

class DiscordRoleType(override val coveredValue: DiscordRole): DiscordType<DiscordRole> {
    override val type = DiscordFlags.CommandArgumentType.ATTACHMENT
    override fun toString() = this.coveredValue.toString()
}
