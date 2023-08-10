package io.github.forceload.discordkt.exception

class CommandAlreadyExistsException(private val name: String) : Throwable() {
    override val message: String
        get() = "Command named \"$name\" already exists"
}
