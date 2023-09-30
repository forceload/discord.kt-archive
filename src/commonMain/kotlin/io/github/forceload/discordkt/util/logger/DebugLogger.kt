package io.github.forceload.discordkt.util.logger

object DebugLogger {

    var enabled = true
    fun log(something: Any) {
        if (enabled) println("[DEBUG ${LoggerUtil.timestampMessage}] $something")
    }
}