package io.github.teamcrez.discordkt.discord.wrapper

import io.github.teamcrez.discordkt.discord.api.DiscordFlags
import io.github.teamcrez.discordkt.discord.collections.DiscordChoiceMap

class CommandArgument(
    private val type: DiscordFlags.CommandArgumentType,
    val required: Boolean = true,
    val choices: DiscordChoiceMap = DiscordChoiceMap(),
    val description: String = "Default Description"
) {
    fun getIntType(): Int = type.intValue
}
