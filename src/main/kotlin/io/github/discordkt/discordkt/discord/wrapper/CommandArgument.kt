package io.github.discordkt.discordkt.discord.wrapper

import io.github.discordkt.discordkt.discord.api.DiscordFlags
import io.github.discordkt.discordkt.discord.collections.DiscordChoiceMap

class CommandArgument(
    private val type: DiscordFlags.CommandArgumentType,
    val required: Boolean = true,
    val choices: DiscordChoiceMap = DiscordChoiceMap(),
    val description: String = "Default Description"
) {
    fun getIntType(): Int = type.intValue
}
