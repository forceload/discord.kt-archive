package io.github.forceload.discordkt.type.commands

import io.github.forceload.discordkt.command.argument.ArgumentType
import io.github.forceload.discordkt.type.URLFile

class DiscordAttachmentType(override val required: Boolean): ArgumentType<URLFile>(required)

val URLFile.Companion.require
    get() = DiscordAttachmentType(true)