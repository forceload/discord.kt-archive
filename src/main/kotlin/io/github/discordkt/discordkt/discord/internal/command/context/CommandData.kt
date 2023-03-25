package io.github.discordkt.discordkt.discord.internal.command.context

import io.github.discordkt.discordkt.discord.collections.DiscordArgumentMap

data class CommandData(val context: CommandContext, val args: DiscordArgumentMap<String> = DiscordArgumentMap())
