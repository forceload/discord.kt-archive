package io.github.forceload.discordkt

import io.github.forceload.discordkt.command.CommandNode
import io.github.forceload.discordkt.exception.CommandAlreadyExistsException
import io.github.forceload.discordkt.network.RequestUtil
import io.github.forceload.discordkt.util.DebugLogger

fun bot(debug: Boolean = false, application: DiscordBot.() -> Unit): DiscordBot {
    val bot = DiscordBot(debug)
    bot.application()
    return bot
}

class DiscordBot(val debug: Boolean) {
    lateinit var id: String
    lateinit var token: String

    init {
        DebugLogger.enabled = !debug
    }

    private val commandMap = HashMap<String, CommandNode>()
    fun command(name: String, code: CommandNode.() -> Unit) {
        val commandNode = CommandNode()
        commandNode.code()

        if (name in commandMap.keys) {
            throw CommandAlreadyExistsException(name)
        }

        commandMap[name] = commandNode
    }

    fun run() {
        RequestUtil.authorization = token

        val commands = RequestUtil.get("applications/${id}/commands")
        DebugLogger.log(commands)
    }
}