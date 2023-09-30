package io.github.forceload.discordkt.type

import io.github.forceload.discordkt.command.argument.ArgumentType

class DiscordAttachment(override val required: Boolean): ArgumentType<URLFile>(required) {
}

val URLFile.Companion.require
    get() = DiscordAttachment(true)