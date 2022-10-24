package io.github.teamcrez.discordkt.discord.types

import io.github.teamcrez.discordkt.discord.api.DiscordFlags

class DiscordNull : DiscordType<Unit?> {
    override val coveredValue = null
    override val type = DiscordFlags.CommandArgumentType.NULL
    override fun getValue(): Unit? {
        return null
    }
}
