package io.github.teamcrez.discordkt.discord.types

import io.github.teamcrez.discordkt.discord.api.DiscordFlags
import java.util.*

class DiscordBoolean(override val coveredValue: Boolean): DiscordType<Boolean> {
    override val type = DiscordFlags.CommandArgumentType.BOOLEAN
    override fun toString() = this.coveredValue.toString()
}
