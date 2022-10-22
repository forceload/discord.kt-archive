package io.github.teamcrez.discordkt.discord.api

@Suppress("SpellCheckingInspection")
object DiscordFlags {
    object GatewayFlag {
        const val HEARTBEAT_TIMESTAMP_SCALE = 0.6
    }
    object MessageFlag {
        const val CROSSPOSTED = (1 shl 0).toShort()
        const val IS_CROSSPOST = (1 shl 1).toShort()
        const val SUPPRESS_EMBEDS = (1 shl 2).toShort()
        const val SOURCE_MESSAGE_DELETED = (1 shl 3).toShort()
        const val URGENT = (1 shl 4).toShort()
        const val HAS_THREAD = (1 shl 5).toShort()
        const val EPHEMERAL = (1 shl 6).toShort()
        const val LOADING = (1 shl 7).toShort()
        const val FAILED_TO_MENTION_SOME_ROLES_IN_THREAD = (1 shl 8).toShort()
    }
}
