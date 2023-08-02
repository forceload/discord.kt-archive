package io.github.forceload.discordkt.command

import io.github.forceload.discordkt.type.argument.ArgumentType

class DiscordCommand {
    var description: String = ""
    private var code = ArrayList<CommandContext.() -> Unit>()

    private val argumentMap = HashMap<String, ArgumentType>()
    fun arguments(vararg args: Pair<String, ArgumentType>) {
        for (argument in args) {
            argumentMap[argument.first] = argument.second
        }
    }

    fun execute(reaction: CommandContext.() -> Unit) {
        code.add(reaction)
    }
}
