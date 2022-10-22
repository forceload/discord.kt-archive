package io.github.teamcrez.discordkt.discord.internal

import io.github.teamcrez.discordkt.discord.internal.command.CommandComponent
import io.github.teamcrez.discordkt.discord.internal.command.context.CommandData
import io.github.teamcrez.discordkt.discord.internal.command.CommandStorage

@Suppress("MemberVisibilityCanBePrivate")
class Commands(private val bot: DiscordBot) {
    val commandNames = ArrayList<String>()

    fun command(
        commandName: String, description: String = "",
        args: Map<String, CommandData> = mapOf(), commandFunc: (CommandData.() -> Unit)
    ) {
        commandNames.add(commandName)
        val component = CommandComponent(this, bot, commandName, description, args)
        CommandStorage.commandProcesses[commandName] = mapOf(component to generateCommand { commandFunc() })
    }

    private fun generateCommand(init: CommandData.() -> Unit): CommandData.() -> Unit = init
}
