package io.github.forceload.discordkt.command

import io.github.forceload.discordkt.command.argument.ArgumentType
import io.github.forceload.discordkt.exception.InvalidArgumentTypeException
import io.github.forceload.discordkt.type.DiscordInteger
import io.github.forceload.discordkt.type.DiscordString

class DiscordCommand {
    var description: String = ""
    private var code = ArrayList<CommandContext.() -> Unit>()

    private val argumentMap = HashMap<String, ArgumentType<*>>()
    fun arguments(vararg args: Pair<String, Any>) {
        for (argument in args) {
            argumentMap[argument.first] = when (argument.second) {
                is ArgumentType<*> -> argument.second
                is String.Companion -> DiscordString(false)
                is Int.Companion -> DiscordInteger(false)

                else -> throw InvalidArgumentTypeException(argument.second::class.qualifiedName)
            } as ArgumentType<*>
        }
    }

    fun execute(reaction: CommandContext.() -> Unit) {
        code.add(reaction)
    }
}
