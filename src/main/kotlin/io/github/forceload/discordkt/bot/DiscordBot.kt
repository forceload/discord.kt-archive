package io.github.forceload.discordkt.bot

import io.github.forceload.discordkt.command.DiscordCommand

fun bot(debug: Boolean = false, application: DiscordBot.() -> Unit): DiscordBot {
    val bot = DiscordBot()
    bot.application()

    return bot
}

class DiscordBot {
    var id:

    private val commandMap = HashMap<String, DiscordCommand>()
    fun command(name: String, code: DiscordCommand.() -> Unit) {
        val discordCommand = DiscordCommand()
        discordCommand.code()

        commandMap[name] = discordCommand
    }

    fun run() {

    }
}