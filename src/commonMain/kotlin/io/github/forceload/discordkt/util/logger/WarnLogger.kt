package io.github.forceload.discordkt.util.logger

object WarnLogger {

    var enabled = true
    fun log(something: Any) {
        if (enabled) println("[WARN ${LoggerUtil.timestampMessage}] $something")
    }
}