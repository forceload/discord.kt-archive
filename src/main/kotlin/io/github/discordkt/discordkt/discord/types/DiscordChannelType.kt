package io.github.discordkt.discordkt.discord.types

import io.github.discordkt.discordkt.discord.api.DiscordFlags
import io.github.discordkt.discordkt.discord.wrapper.DiscordChannel

class DiscordChannelType(override val coveredValue: DiscordChannel): DiscordType<DiscordChannel> {
    override val type = DiscordFlags.CommandArgumentType.ATTACHMENT
    override fun toString() = this.coveredValue.toString()
}
