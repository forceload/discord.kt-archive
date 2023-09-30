package io.github.forceload.discordkt

import io.github.forceload.discordkt.command.CommandNode
import io.github.forceload.discordkt.command.internal.CommandSerializer
import io.github.forceload.discordkt.command.internal.DiscordCommand
import io.github.forceload.discordkt.exception.CommandAlreadyExistsException
import io.github.forceload.discordkt.network.RequestUtil
import io.github.forceload.discordkt.network.WebSocketUtil
import io.github.forceload.discordkt.util.DebugLogger
import io.github.forceload.discordkt.util.SerializerUtil

fun bot(debug: Boolean = false, application: DiscordBot.() -> Unit) =
    DiscordBot(debug).also(application)

class DiscordBot(debug: Boolean) {
    lateinit var id: String
    lateinit var token: String

    init {
        DebugLogger.enabled = debug
    }

    private val commandMap = HashMap<String, CommandNode>()
    fun command(name: String, code: CommandNode.() -> Unit) {
        val commandNode = CommandNode(name).also(code)
        if (name in commandMap.keys) {
            throw CommandAlreadyExistsException(name)
        }

        commandMap[name] = commandNode
    }

    fun run(commandOptionMaxDepth: Int = 16) {
        val commands = RequestUtil.get("applications/${id}/commands", token, "with_localizations" to true)
        SerializerUtil.commandOptionMaxDepth = commandOptionMaxDepth

        DebugLogger.log(commands.dropLast(1))
        val commandList = SerializerUtil.jsonBuild.decodeFromString<ArrayList<DiscordCommand>>(commands)

        DebugLogger.log(commandList)

        val iterator = commandList.iterator()
        while (iterator.hasNext()) {
            val command = iterator.next()
            if (command.name !in commandMap) {
                command.destroy(token)
                iterator.remove()
            } else {
                val generated = commandMap[command.name]!!.generateCommand()
                if (generated != command) {
                    val serialized = SerializerUtil.jsonBuild.encodeToString(CommandSerializer, generated)
                    val post = RequestUtil.post("applications/$id/commands", token, serialized)

                    DebugLogger.log(serialized)
                    DebugLogger.log(generated)
                    DebugLogger.log(command)
                    DebugLogger.log(post)
                }
            }
        }

        val webSocketClient = WebSocketUtil.newInstance("")
    }
}