package io.github.teamcrez.discordkt.discord.internal.command

import io.github.teamcrez.discordkt.discord.internal.command.context.CommandData

object CommandStorage {
    val commandProcesses: HashMap<String, Map<CommandComponent, CommandData.() -> Unit>> = HashMap()
}