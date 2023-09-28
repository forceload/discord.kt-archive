package io.github.forceload.discordkt.exception

class InvalidArgumentTypeException(private val name: String?) : Throwable() {
    override val message: String
        get() {
            var fixedName = name
            if (fixedName != null && fixedName.endsWith(".Companion"))
                fixedName = fixedName.substring(0..<fixedName.length - 10)

            return "Argument Type \"$fixedName\" is invalid in Discord API"
        }
}
