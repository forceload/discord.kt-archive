package io.github.teamcrez.discordkt.discord.types

import io.github.teamcrez.discordkt.discord.api.DiscordFlags
import io.github.teamcrez.discordkt.discord.wrapper.DiscordUser

class DiscordUserType(override val coveredValue: DiscordUser): DiscordType<DiscordUser> {
    override val type = DiscordFlags.CommandArgumentType.STRING
    override fun toString() = "<@${coveredValue.id}>"
}
