package io.github.forceload.discordkt.exception

class InvalidArgumentTypeException(private val name: String?) : Throwable() {
    override val message: String
        get() {
            var fixed_name = name
            if (fixed_name != null && fixed_name.endsWith(".Companion"))
                fixed_name = fixed_name.substring(0..<fixed_name.length - 10)

            return "Argument Type \"$fixed_name\" is invalid in Discord API"
        }
}
