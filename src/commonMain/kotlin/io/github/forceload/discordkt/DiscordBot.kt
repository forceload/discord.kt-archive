package io.github.forceload.discordkt

import io.github.forceload.discordkt.command.CommandNode
import io.github.forceload.discordkt.command.internal.DiscordCommand
import io.github.forceload.discordkt.exception.CommandAlreadyExistsException
import io.github.forceload.discordkt.network.RequestUtil
import io.github.forceload.discordkt.util.DebugLogger
import io.github.forceload.discordkt.util.SerializerUtil

fun bot(debug: Boolean = false, application: DiscordBot.() -> Unit): DiscordBot {
    val bot = DiscordBot(debug)
    bot.application()
    return bot
}

class DiscordBot(debug: Boolean) {
    lateinit var id: String
    lateinit var token: String

    init {
        DebugLogger.enabled = debug
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

    fun run(commandOptionMaxDepth: Int = 16) {
        val commands = RequestUtil.get("applications/${id}/commands", token)
        SerializerUtil.commandOptionMaxDepth = commandOptionMaxDepth

        val commandList = SerializerUtil.jsonBuild.decodeFromString<ArrayList<DiscordCommand>>(commands)
        DebugLogger.log(commandList)
    }
}