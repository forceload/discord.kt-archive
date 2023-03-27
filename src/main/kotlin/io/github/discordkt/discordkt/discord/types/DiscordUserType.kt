package io.github.discordkt.discordkt.discord.types

import io.github.discordkt.discordkt.discord.api.DiscordFlags
import io.github.discordkt.discordkt.discord.wrapper.DiscordUser

class DiscordUserType(override val coveredValue: DiscordUser): DiscordType<DiscordUser> {
    override val type = DiscordFlags.CommandArgumentType.USER
    override fun toString() = "<@${coveredValue.id}>"
}
