package io.github.forceload.discordkt.command

import io.github.forceload.discordkt.command.argument.Argument
import io.github.forceload.discordkt.command.argument.ArgumentType
import io.github.forceload.discordkt.command.internal.DiscordCommand
import io.github.forceload.discordkt.command.internal.type.ApplicationCommandOptionType
import io.github.forceload.discordkt.exception.InvalidArgumentTypeException
import io.github.forceload.discordkt.type.DiscordAttachment
import io.github.forceload.discordkt.type.DiscordInteger
import io.github.forceload.discordkt.type.DiscordString

class CommandNode(var name: String) {
    var description: String = " "
        set(value) { field = value.ifEmpty { " " } }

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
                else -> Pair(newArgument, Argument.identifyType(argument.second) as ArgumentType<*>)
            }
        }

        // DebugLogger.log(argumentMap)
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
                    is DiscordAttachment -> ApplicationCommandOptionType.ATTACHMENT
                    else -> throw InvalidArgumentTypeException("Argument Type is Invalid")
                },
                entry.key, argument.first.description, argument.second.required
            )

            option.nameLocalizations.putAll(argument.first.nameLocalizations)
            option.descriptionLocalizations.putAll(argument.first.descriptionLocalizations)
            option.choices.addAll(argument.first.choice)

            result.options.add(option)
        }

        // DebugLogger.log(result)
        return result
    }
}
