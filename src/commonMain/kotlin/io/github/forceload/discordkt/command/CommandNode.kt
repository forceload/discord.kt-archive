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

    private val argumentMap = HashMap<String, Pair<Argument, ArgumentType<*>>>()

    fun arguments(vararg args: Pair<Any, Any>) {
        for (argument in args) {
            val newArgument = when (argument.first) {
                is String -> Argument(argument.first as String, "")
                is Argument -> argument.first as Argument

                else -> throw InvalidArgumentTypeException(argument.first::class.qualifiedName)
            }

            argumentMap[newArgument.name] = when (argument.second) {
                is ArgumentType<*> -> Pair(newArgument, argument.second as ArgumentType<*>)
                is String.Companion -> Pair(newArgument, DiscordString(false))
                is Int.Companion -> Pair(newArgument, DiscordInteger(false))

                else -> throw InvalidArgumentTypeException(argument.second::class.qualifiedName)
            }
        }

        DebugLogger.log(argumentMap)
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
                entry.key, argument.first.description, argument.second.required
            )

            DebugLogger.log(argument)
            option.nameLocalizations.putAll(argument.first.nameLocalizations)
            option.descriptionLocalizations.putAll(argument.first.descriptionLocalizations)
            option.choices.addAll(argument.first.choice)

            result.options.add(option)
        }

        DebugLogger.log(result)
        return result
    }
}
