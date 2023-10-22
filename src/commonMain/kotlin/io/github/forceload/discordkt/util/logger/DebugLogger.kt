package io.github.forceload.discordkt.util.logger

object DebugLogger {
    var enabled = true
    fun log(something: Any) {
        val prefixMessage = "[DEBUG ${LoggerUtil.timestampMessage}]"
        if (enabled) println("$prefixMessage ${something.toString().replace("\n", "\n${prefixMessage} ")}")
    }
}