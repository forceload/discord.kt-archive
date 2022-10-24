package io.github.teamcrez.discordkt.discord.api

@Suppress("SpellCheckingInspection", "unused")
object DiscordFlags {
    object GatewayFlag {
        const val DEFAULT_DISABLE_CODE = 1000
        const val HEARTBEAT_TIMESTAMP_SCALE = 0.6
        const val DEFAULT_TIMEOUT: Long = 5
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

    object Opcode {
        const val DISPATCH = 0
        const val HEARTBEAT = 1
        const val IDENTIFY = 2
        const val PRESENCE_UPDATE = 3
        const val VOICE_STATE_UPDATE = 4
        const val RESUME = 6
        const val RECONNECT = 7
        const val REQUEST_GUILD_MEMBERS = 8
        const val INVAILD_SESSION = 9
        const val HELLO = 10
        const val HEARTBEAT_ACK = 11
    }

    enum class CommandArgumentType(val intValue: Int) {
        NULL(-1),
        
        SUB_COMMAND(1),
        SUB_COMMAND_GROUP(2),
        STRING(3),
        INTEGER(4),
        BOOLEAN(5),
        USER(6),
        CHANNEL(7),
        ROLE(8),
        MENTIONABLE(9),
        NUMBER(10),
        ATTACHMENT(11)
    }

    fun matchArgumentType(intValue: Int): CommandArgumentType =
        when (intValue) {
            CommandArgumentType.NULL.intValue -> CommandArgumentType.NULL
            CommandArgumentType.SUB_COMMAND.intValue -> CommandArgumentType.SUB_COMMAND
            CommandArgumentType.SUB_COMMAND_GROUP.intValue -> CommandArgumentType.SUB_COMMAND_GROUP
            CommandArgumentType.STRING.intValue -> CommandArgumentType.STRING
            CommandArgumentType.INTEGER.intValue -> CommandArgumentType.INTEGER
            CommandArgumentType.BOOLEAN.intValue -> CommandArgumentType.BOOLEAN
            CommandArgumentType.USER.intValue -> CommandArgumentType.USER
            CommandArgumentType.CHANNEL.intValue -> CommandArgumentType.CHANNEL
            CommandArgumentType.ROLE.intValue -> CommandArgumentType.ROLE
            CommandArgumentType.MENTIONABLE.intValue -> CommandArgumentType.MENTIONABLE
            CommandArgumentType.NUMBER.intValue -> CommandArgumentType.NUMBER
            CommandArgumentType.ATTACHMENT.intValue -> CommandArgumentType.ATTACHMENT
        else -> {
            CommandArgumentType.NULL
        }
    }

    enum class ChannelType(val intValue: Int) {
        NULL(-1),

        GUILD_TEXT(0),
        DM(1),
        GUILD_VOICE(2),
        GROUP_DM(3),
        GUILD_CATEGORY(4),
        GUILD_ANNOUNCEMENT(5),
        ANNOUNCEMENT_THREAD(10),
        PUBLIC_THREAD(11),
        PRIVATE_THREAD(12),
        GUILD_STAGE_VOICE(13),
        GUILD_DIRECTORY(14),
        GUILD_FORUM(15);

        fun isOnGuild() = (this == GUILD_TEXT || this == GUILD_VOICE || this == GUILD_CATEGORY ||
                    this == GUILD_ANNOUNCEMENT || this == ANNOUNCEMENT_THREAD ||
                    this == PUBLIC_THREAD || this == PRIVATE_THREAD ||
                    this == GUILD_STAGE_VOICE || this == GUILD_FORUM ||
                    this == GUILD_DIRECTORY)
    }

    fun matchChannelType(intValue: Int): ChannelType =
        when (intValue) {
            ChannelType.NULL.intValue -> ChannelType.NULL
            ChannelType.GUILD_TEXT.intValue -> ChannelType.GUILD_TEXT
            ChannelType.DM.intValue -> ChannelType.DM
            ChannelType.GUILD_VOICE.intValue -> ChannelType.GUILD_VOICE
            ChannelType.GROUP_DM.intValue -> ChannelType.GROUP_DM
            ChannelType.GUILD_CATEGORY.intValue -> ChannelType.GUILD_CATEGORY
            ChannelType.GUILD_ANNOUNCEMENT.intValue -> ChannelType.GUILD_ANNOUNCEMENT
            ChannelType.ANNOUNCEMENT_THREAD.intValue -> ChannelType.ANNOUNCEMENT_THREAD
            ChannelType.PUBLIC_THREAD.intValue -> ChannelType.PUBLIC_THREAD
            ChannelType.PRIVATE_THREAD.intValue -> ChannelType.PRIVATE_THREAD
            ChannelType.GUILD_STAGE_VOICE.intValue -> ChannelType.GUILD_STAGE_VOICE
            ChannelType.GUILD_DIRECTORY.intValue -> ChannelType.GUILD_DIRECTORY
            ChannelType.GUILD_FORUM.intValue -> ChannelType.GUILD_FORUM
            else -> {
                ChannelType.NULL
            }
        }

    object SystemChannelFlags {
        const val SUPPRESS_JOIN_NOTIFICATIONS = (1 shl 0).toByte()
        const val SUPPRESS_PREMIUM_SUBSCRIPTIONS = (1 shl 1).toByte()
        const val SUPPRESS_GUILD_REMINDER_NOTIFICATIONS = (1 shl 2).toByte()
        const val SUPPRESS_JOIN_NOTIFICATION_REPLIES = (1 shl 3).toByte()
    }
}
