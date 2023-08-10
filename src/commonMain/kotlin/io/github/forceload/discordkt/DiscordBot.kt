package io.github.forceload.discordkt

import io.github.forceload.discordkt.command.DiscordCommand
import io.github.forceload.discordkt.exception.CommandAlreadyExistsException

fun bot(debug: Boolean = true, application: DiscordBot.() -> Unit): DiscordBot {
    val bot = DiscordBot()
    bot.application()
    return bot
}

class DiscordBot {
    var id: Long = 0
    var token: String = ""

    private val commandMap = HashMap<String, DiscordCommand>()
    fun command(name: String, code: DiscordCommand.() -> Unit) {
        val discordCommand = DiscordCommand()
        discordCommand.code()

        if (name in commandMap.keys) {
            throw CommandAlreadyExistsException(name)
        }

        commandMap[name] = discordCommand
    }

    fun run() {

    }
}