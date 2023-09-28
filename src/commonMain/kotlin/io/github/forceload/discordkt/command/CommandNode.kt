package io.github.forceload.discordkt.command

import io.github.forceload.discordkt.command.argument.Argument
import io.github.forceload.discordkt.command.argument.ArgumentType
import io.github.forceload.discordkt.command.internal.DiscordCommand
import io.github.forceload.discordkt.command.internal.type.ApplicationCommandOptionType
import io.github.forceload.discordkt.exception.InvalidArgumentTypeException
import io.github.forceload.discordkt.type.DiscordInteger
import io.github.forceload.discordkt.type.DiscordString
import io.github.forceload.discordkt.util.DebugLogger

class CommandNode(var name: String) {
    var description: String = ""
    private var code = ArrayList<CommandContext.() -> Unit>()

    private val argumentMap = HashMap<String, Pair<String, ArgumentType<*>>>()

    private fun makePair(description: String, type: Any): Pair<String, ArgumentType<*>> {
        return when (type) {
            is ArgumentType<*> -> Pair(description, type)
            is String.Companion -> Pair(description, DiscordString(false))
            is Int.Companion -> Pair(description, DiscordInteger(false))

            else -> throw InvalidArgumentTypeException(type::class.qualifiedName)
        }
    }

    fun arguments(vararg args: Pair<Any, Any>) {
        for (argument in args) {
            when (argument.first) {
                is String -> argumentMap[argument.first as String] = makePair("", argument.second)
                is Argument -> argumentMap[(argument.first as Argument).name] = makePair((argument.first as Argument).description, argument.second)
            }
        }
    }

    fun execute(reaction: CommandContext.() -> Unit) {
        code.add(reaction)
    }

    fun generateCommand(): DiscordCommand {
        val result = DiscordCommand(null, null, name, description)
        argumentMap.onEachIndexed { index, entry ->
            val argument = entry.value
            val option = DiscordCommand.ApplicationCommandOption(
                when (argument.second) {
                    is DiscordString -> ApplicationCommandOptionType.STRING
                    is DiscordInteger -> ApplicationCommandOptionType.INTEGER
                    else -> throw InvalidArgumentTypeException("Argument Type is Invalid")
                },
                entry.key, argument.first, argument.second.required
            )

            result.options.add(option)
        }

        DebugLogger.log(result)
        return result
    }
}
