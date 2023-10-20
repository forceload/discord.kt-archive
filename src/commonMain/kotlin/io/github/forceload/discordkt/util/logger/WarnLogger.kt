package io.github.forceload.discordkt.util.logger

object WarnLogger {

    fun log(something: Any) {
        val prefixMessage = "[WARN ${LoggerUtil.timestampMessage}]"
        println("$prefixMessage ${something.toString().replace("\n", "\n${prefixMessage} ")}")
    }
}