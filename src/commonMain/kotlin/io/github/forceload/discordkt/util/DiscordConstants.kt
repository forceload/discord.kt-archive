package io.github.forceload.discordkt.util


object DiscordConstants {
    const val defaultDescription = "Default Description"

    const val MAX_COMMAND_THREADS = 4
    const val MAX_HTTP_THREADS = 4
    const val MAX_WS_THREADS = 4

    const val apiVersion = 10

    object Caches {
        const val DM_CACHE_ALIVE = 10 * TimeConstants.MINUTE_IN_SECONDS * 1000
    }

    @Suppress("unused")
    object OpCode {
        const val DISPATCH = 0
        const val HEARTBEAT = 1
        const val IDENTIFY = 2
        const val PRESENCE_UPDATE = 3
        const val VOICE_STATE_UPDATE = 4
        const val RESUME = 6
        const val RECONNECT = 7
        const val REQUEST_GUILD_MEMBERS = 8
        const val INVALID_SESSION = 9
        const val HELLO = 10
        const val HEARTBEAT_ACK = 11
    }
}