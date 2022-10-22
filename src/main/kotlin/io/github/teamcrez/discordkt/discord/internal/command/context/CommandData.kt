package io.github.teamcrez.discordkt.discord.internal.command.context

import io.github.teamcrez.discordkt.discord.collections.DiscordArgumentMap

data class CommandData(val context: CommandContext, val args: DiscordArgumentMap<String> = DiscordArgumentMap())
