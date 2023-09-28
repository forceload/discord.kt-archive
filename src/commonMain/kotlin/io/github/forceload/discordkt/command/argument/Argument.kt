package io.github.forceload.discordkt.command.argument

data class Argument(val name: String, val description: String = "")
inline infix fun String.with(other: String) = Argument(this, other)