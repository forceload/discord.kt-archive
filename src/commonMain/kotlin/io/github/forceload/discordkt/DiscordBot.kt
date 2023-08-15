package io.github.forceload.discordkt

import io.github.forceload.discordkt.command.CommandNode
import io.github.forceload.discordkt.exception.CommandAlreadyExistsException
import io.github.forceload.discordkt.util.DebugLogger
import io.ktor.client.*
import io.ktor.client.engine.cio.*

fun bot(
    credentials: DiscordBotCredentials,
    debug: Boolean = false, application: DiscordBot.() -> Unit
) = DiscordBot(credentials, debug).also(application)

class DiscordBot(val credentials: DiscordBotCredentials, debug: Boolean) {
    val client = HttpClient(CIO)
    private val commandMap = HashMap<String, CommandNode>()

    init {
        DebugLogger.enabled = debug
    }

    fun command(name: String, code: CommandNode.() -> Unit) {
        val commandNode = CommandNode()
        commandNode.code()

        if (name in commandMap.keys) {
            throw CommandAlreadyExistsException(name)
        }

        commandMap[name] = commandNode
    }

    suspend fun run() {
        val commands = get("applications/${credentials.id}/commands")
        DebugLogger.log(commands)
    }
}