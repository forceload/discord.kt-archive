package io.github.forceload.discordkt.type

import io.github.forceload.discordkt.command.argument.ArgumentType

class DiscordInteger(override val required: Boolean = false): ArgumentType<Long>(required)

val Int.Companion.require
    get() = DiscordInteger(true)