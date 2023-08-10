package io.github.forceload.discordkt.type

import io.github.forceload.discordkt.command.argument.ArgumentType

class DiscordString(override val required: Boolean): ArgumentType<String>(required) {

}

val String.Companion.require: DiscordString
    get() = DiscordString(true)