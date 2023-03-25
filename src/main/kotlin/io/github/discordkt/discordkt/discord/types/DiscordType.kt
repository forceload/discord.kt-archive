package io.github.discordkt.discordkt.discord.types

import io.github.discordkt.discordkt.discord.api.DiscordFlags

interface DiscordType<T> {
    val type: DiscordFlags.CommandArgumentType
    val coveredValue: T
}
