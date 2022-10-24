package io.github.teamcrez.discordkt.discord.types

import io.github.teamcrez.discordkt.discord.api.DiscordFlags

interface DiscordType<T> {
    val type: DiscordFlags.CommandArgumentType
    val coveredValue: T
    fun getValue(): T
}
