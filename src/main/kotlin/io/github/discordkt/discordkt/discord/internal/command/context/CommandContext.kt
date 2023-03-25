package io.github.discordkt.discordkt.discord.internal.command.context

import io.github.discordkt.discordkt.discord.wrapper.DiscordChannel
import io.github.discordkt.discordkt.discord.wrapper.DiscordInteraction
import io.github.discordkt.discordkt.discord.wrapper.DiscordUser

@Suppress("MemberVisibilityCanBePrivate")
class CommandContext(val user: DiscordUser, val channel: DiscordChannel, val interaction: DiscordInteraction) {
    fun sendMessage(message: String) = channel.sendMessage(message)
    fun interact(type: Int?) = interaction.interact(type)
}
