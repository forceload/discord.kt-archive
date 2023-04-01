package io.github.discordkt.discordkt.discord.internal

import io.github.discordkt.discordkt.discord.internal.command.CommandComponent
import io.github.discordkt.discordkt.discord.internal.command.context.CommandData
import io.github.discordkt.discordkt.discord.internal.command.CommandStorage
import io.github.discordkt.discordkt.discord.wrapper.CommandArgument

@Suppress("MemberVisibilityCanBePrivate")
class Commands(private val bot: DiscordBot) {
    val commandNames = ArrayList<String>()

    fun command(
        commandName: String, description: String = "Default Description",
        args: Map<String, CommandArgument> = mapOf(), commandFunc: (CommandData.() -> Unit)
    ) {
        commandNames.add(commandName)
        val component = CommandComponent(this, bot, commandName, description, args)

        CommandStorage.commandProcesses[commandName] = mapOf(component to generateCommand { commandFunc() })
    }

    private fun generateCommand(init: CommandData.() -> Unit): CommandData.() -> Unit = init
}
