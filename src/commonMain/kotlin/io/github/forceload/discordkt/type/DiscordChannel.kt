package io.github.forceload.discordkt.type

import io.github.forceload.discordkt.network.RequestUtil
import io.github.forceload.discordkt.type.channel.DiscordChannelType
import io.github.forceload.discordkt.type.guilds.GuildMember
import io.github.forceload.discordkt.util.CoroutineScopes
import io.github.forceload.discordkt.util.SerializerExtension.arraySerializer
import io.github.forceload.discordkt.util.SerializerExtension.decodeNullableString
import io.github.forceload.discordkt.util.SerializerUtil.makeStructure
import kotlinx.coroutines.async
import kotlinx.datetime.Instant
import kotlinx.datetime.serializers.InstantIso8601Serializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = PermissionOverwriteType.Serializer::class)
enum class PermissionOverwriteType(val id: Int) {
    ROLE(0), MEMBER(1);

    companion object {
        fun fromID(id: Int) = entries.first { it.id == id }
    }

    object Serializer: KSerializer<PermissionOverwriteType> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("ChannelOverwriteType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder) = PermissionOverwriteType.fromID(decoder.decodeInt())
        override fun serialize(encoder: Encoder, value: PermissionOverwriteType) = encoder.encodeInt(value.id)
    }
}

@Serializable(with = PermissionOverwrite.Serializer::class)
class PermissionOverwrite(
    val id: String, val type: PermissionOverwriteType,
    val allow: Set<DiscordPermission>, val deny: Set<DiscordPermission>
) {
    object Serializer: KSerializer<PermissionOverwrite> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("ChannelOverwrite") {
                element<String>("id")
                element<Int>("type")
                element<String>("allow")
                element<String>("deny")
            }

        override fun deserialize(decoder: Decoder): PermissionOverwrite {
            var id: String? = null
            var type: PermissionOverwriteType? = null
            var allow: Set<DiscordPermission>? = null
            var deny: Set<DiscordPermission>? = null

            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> id = decodeStringElement(descriptor, index)
                    1 -> type = decodeSerializableElement(descriptor, index, PermissionOverwriteType.Serializer)
                    2 -> allow = decodeSerializableElement(descriptor, index, DiscordPermission.SetSerializer)
                    3 -> deny = decodeSerializableElement(descriptor, index, DiscordPermission.SetSerializer)
                }
            }

            return PermissionOverwrite(id!!, type!!, allow!!, deny!!)
        }

        override fun serialize(encoder: Encoder, value: PermissionOverwrite) {
            encoder.beginStructure(descriptor).run {
                encodeStringElement(descriptor, 0, value.id)
                encodeSerializableElement(descriptor, 1, PermissionOverwriteType.Serializer, value.type)
                encodeSerializableElement(descriptor, 2, DiscordPermission.SetSerializer, value.allow)
                encodeSerializableElement(descriptor, 3, DiscordPermission.SetSerializer, value.deny)
                endStructure(descriptor)
            }
        }

    }
}

@Serializable(with = VideoQualityMode.Serializer::class)
enum class VideoQualityMode(val id: Int) {
    AUTO(1), FULL(2);

    companion object {
        fun fromID(id: Int) = entries.first { it.id == id }
    }

    object Serializer: KSerializer<VideoQualityMode> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("VideoQualityMode", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder) = VideoQualityMode.fromID(decoder.decodeInt())
        override fun serialize(encoder: Encoder, value: VideoQualityMode) = encoder.encodeInt(value.id)
    }
}

@Serializable(with = ThreadMetadata.Serializer::class)
class ThreadMetadata(
    val archived: Boolean, val autoArchiveDuration: Int, val archiveTimestamp: Instant, val locked: Boolean,
    val invitable: Boolean? = null, val createTimestamp: Instant? = null
) {
    object Serializer: KSerializer<ThreadMetadata> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("ThreadMetadata") {
                element<Boolean>("archived")
                element<Int>("auto_archive_duration")
                element<String>("archive_timestamp")
                element<Boolean>("locked")
                element<Boolean>("invitable", isOptional = true)
                element<String?>("create_timestamp", isOptional = true)
            }

        @OptIn(ExperimentalSerializationApi::class)
        override fun deserialize(decoder: Decoder): ThreadMetadata {
            var archived: Boolean? = null
            var autoArchiveDuration: Int? = null
            var archiveTimestamp: Instant? = null
            var locked: Boolean? = null
            var invitable: Boolean? = null
            var createTimestamp: Instant? = null
            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> archived = decodeBooleanElement(descriptor, index)
                    1 -> autoArchiveDuration = decodeIntElement(descriptor, index)
                    2 -> archiveTimestamp = decodeSerializableElement(descriptor, index, InstantIso8601Serializer)
                    3 -> locked = decodeBooleanElement(descriptor, index)
                    4 -> invitable = decodeBooleanElement(descriptor, index)
                    5 -> createTimestamp =
                        decodeNullableSerializableElement(descriptor, index, InstantIso8601Serializer)
                }
            }

            return ThreadMetadata(
                archived!!, autoArchiveDuration!!, archiveTimestamp!!,
                locked!!, invitable, createTimestamp
            )
        }

        override fun serialize(encoder: Encoder, value: ThreadMetadata) {
            encoder.beginStructure(descriptor).run {
                encodeBooleanElement(descriptor, 0, value.archived)
                encodeIntElement(descriptor, 1, value.autoArchiveDuration)
                encodeSerializableElement(descriptor, 2, InstantIso8601Serializer, value.archiveTimestamp)
                encodeBooleanElement(descriptor, 3, value.locked)
                value.invitable?.let { encodeBooleanElement(descriptor, 4, value.invitable) }
                value.createTimestamp?.let { encodeSerializableElement(descriptor, 5, InstantIso8601Serializer, value.createTimestamp) }

                endStructure(descriptor)
            }
        }

    }
}

/**
 * https://discord.com/developers/docs/resources/channel#thread-member-object
 */
@Serializable(with = ThreadMember.Serializer::class)
class ThreadMember(val id: String? = null, val userID: String? = null, val joinTimestamp: Instant, val flags: Int, val member: GuildMember? = null) {
    object Serializer: KSerializer<ThreadMember> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("ThreadMember") {
                element<String>("id", isOptional = true)
                element<String>("user_id", isOptional = true)
                element<String>("join_timestamp")
                element<Int>("flags") // TODO: Find Usage
                element<GuildMember>("member", isOptional = true)
            }

        override fun deserialize(decoder: Decoder): ThreadMember {
            var id: String? = null
            var userID: String? = null
            var joinTimestamp: Instant? = null
            var flags: Int? = null
            var member: GuildMember? = null

            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> id = decodeStringElement(descriptor, index)
                    1 -> userID = decodeStringElement(descriptor, index)
                    2 -> joinTimestamp = decodeSerializableElement(descriptor, index, InstantIso8601Serializer)
                    3 -> flags = decodeIntElement(descriptor, index)
                    4 -> member = decodeSerializableElement(descriptor, index, GuildMember.Serializer)
                }
            }

            return ThreadMember(id, userID, joinTimestamp!!, flags!!, member)
        }

        override fun serialize(encoder: Encoder, value: ThreadMember) {
            encoder.beginStructure(descriptor).run {
                value.id?.let { encodeStringElement(descriptor, 0, value.id) }
                value.userID?.let { encodeStringElement(descriptor, 1, value.userID) }
                encodeSerializableElement(descriptor, 2, InstantIso8601Serializer, value.joinTimestamp)
                encodeIntElement(descriptor, 3, value.flags)
                value.member?.let { encodeSerializableElement(descriptor, 4, GuildMember.Serializer, value.member) }
            }
        }
    }
}

@Serializable
data class ForumTag(
    val id: String, val name: String, val moderated: Boolean,
    @SerialName("emoji_id") val emojiID: String, @SerialName("emoji_name") val emojiName: String
)

@Serializable
data class DefaultReaction(
    @SerialName("emoji_id") val emojiID: String?, @SerialName("emoji_name") val emojiName: String?
)

@Serializable(with = SortOrderType.Serializer::class)
enum class SortOrderType(val id: Int) {
    LATEST_ACTIVITY(0), CREATION_DATE(1);

    companion object {
        fun fromID(id: Int) = entries.first { it.id == id }
    }

    object Serializer: KSerializer<SortOrderType> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("SortOrderType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder) =
            SortOrderType.fromID(decoder.decodeInt())

        override fun serialize(encoder: Encoder, value: SortOrderType) =
            encoder.encodeInt(value.id)
    }
}

@Serializable(with = ForumLayoutType.Serializer::class)
enum class ForumLayoutType(val id: Int) {
    NOT_SET(0), LIST_VIEW(1), GALLERY_VIEW(2);

    companion object {
        fun fromID(id: Int) = entries.first { it.id == id }
    }

    object Serializer: KSerializer<ForumLayoutType> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("ForumLayoutType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder) =
            ForumLayoutType.fromID(decoder.decodeInt())

        override fun serialize(encoder: Encoder, value: ForumLayoutType) =
            encoder.encodeInt(value.id)
    }
}

enum class ChannelFlags(val id: Int) {
    PINNED(1 shl 1), REQUIRE_TAG(1 shl 4), HIDE_MEDIA_DOWNLOAD_OPTIONS(1 shl 15);

    object SetSerializer: KSerializer<Set<ChannelFlags>> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("ChannelFlags", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): Set<ChannelFlags> {
            val intentFlag = decoder.decodeInt()

            val flagSet = mutableSetOf<ChannelFlags>()
            entries.forEach {
                if (intentFlag and it.id == it.id) flagSet.add(it)
            }

            return flagSet
        }

        override fun serialize(encoder: Encoder, value: Set<ChannelFlags>) {
            var result = 0
            value.forEach { result = result or it.id }

            encoder.encodeInt(result)
        }
    }
}

/**
 * https://discord.com/developers/docs/resources/channel#channel-object
 */
@Serializable(with = DiscordChannel.Serializer::class)
data class DiscordChannel(
    val id: String, val type: DiscordChannelType, val guildID: String? = null,
    val position: Int? = null, val permissionOverwrites: Array<PermissionOverwrite>,
    val name: String? = null, val topic: String? = null, val nsfw: Boolean? = null,
    val lastMessageID: String? = null, val bitrate: Int? = null, val userLimit: Int? = null,
    val rateLimitPerUser: Int? = null, val recipients: Array<DiscordUser>? = null,
    val icon: String? = null, val ownerID: String? = null, val appID: String? = null,
    val managed: Boolean? = null, val parentID: String? = null, val lastPinTimestamp: Instant? = null,
    val rtcRegion: String? = null, val videoQualityMode: VideoQualityMode? = VideoQualityMode.AUTO, val messageCount: Int? = null,
    val memberCount: Int? = null, val threadMetadata: ThreadMetadata? = null, val member: ThreadMember? = null,
    val defaultAutoArchiveDuration: Int? = null, val permissions: Set<DiscordPermission>, val flags: Set<ChannelFlags>? = null,
    val totalMessageSent: Int? = null, val availableTags: Array<ForumTag>? = null, val appliedTags: Array<String>? = null,
    val defaultReactionEmoji: DefaultReaction? = null, val defaultThreadRateLimitPerUser: Int? = null,
    val defaultSortOrder: SortOrderType? = null, val defaultForumLayout: ForumLayoutType? = ForumLayoutType.NOT_SET
) {
    @Suppress("DeferredResultUnused")
    fun sendMessage(text: String, token: String) {
        CoroutineScopes.httpScope.async { RequestUtil.post("channels/$id/messages", token, "{\"content\": \"$text\"}") }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DiscordChannel) return false

        if (id != other.id) return false
        if (type != other.type) return false
        if (guildID != other.guildID) return false
        if (position != other.position) return false
        if (!permissionOverwrites.contentEquals(other.permissionOverwrites)) return false
        if (name != other.name) return false
        if (topic != other.topic) return false
        if (nsfw != other.nsfw) return false
        if (lastMessageID != other.lastMessageID) return false
        if (bitrate != other.bitrate) return false
        if (userLimit != other.userLimit) return false
        if (rateLimitPerUser != other.rateLimitPerUser) return false
        if (recipients != null) {
            if (other.recipients == null) return false
            if (!recipients.contentEquals(other.recipients)) return false
        } else if (other.recipients != null) return false
        if (icon != other.icon) return false
        if (ownerID != other.ownerID) return false
        if (appID != other.appID) return false
        if (managed != other.managed) return false
        if (parentID != other.parentID) return false
        if (lastPinTimestamp != other.lastPinTimestamp) return false
        if (rtcRegion != other.rtcRegion) return false
        if (videoQualityMode != other.videoQualityMode) return false
        if (messageCount != other.messageCount) return false
        if (memberCount != other.memberCount) return false
        if (threadMetadata != other.threadMetadata) return false
        if (member != other.member) return false
        if (defaultAutoArchiveDuration != other.defaultAutoArchiveDuration) return false
        if (permissions != other.permissions) return false
        if (flags != other.flags) return false
        if (totalMessageSent != other.totalMessageSent) return false
        if (availableTags != null) {
            if (other.availableTags == null) return false
            if (!availableTags.contentEquals(other.availableTags)) return false
        } else if (other.availableTags != null) return false
        if (appliedTags != null) {
            if (other.appliedTags == null) return false
            if (!appliedTags.contentEquals(other.appliedTags)) return false
        } else if (other.appliedTags != null) return false
        if (defaultReactionEmoji != other.defaultReactionEmoji) return false
        if (defaultThreadRateLimitPerUser != other.defaultThreadRateLimitPerUser) return false
        if (defaultSortOrder != other.defaultSortOrder) return false
        return defaultForumLayout == other.defaultForumLayout
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + (guildID?.hashCode() ?: 0)
        result = 31 * result + (position ?: 0)
        result = 31 * result + permissionOverwrites.contentHashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (topic?.hashCode() ?: 0)
        result = 31 * result + (nsfw?.hashCode() ?: 0)
        result = 31 * result + (lastMessageID?.hashCode() ?: 0)
        result = 31 * result + (bitrate ?: 0)
        result = 31 * result + (userLimit ?: 0)
        result = 31 * result + (rateLimitPerUser ?: 0)
        result = 31 * result + (recipients?.contentHashCode() ?: 0)
        result = 31 * result + (icon?.hashCode() ?: 0)
        result = 31 * result + (ownerID?.hashCode() ?: 0)
        result = 31 * result + (appID?.hashCode() ?: 0)
        result = 31 * result + (managed?.hashCode() ?: 0)
        result = 31 * result + (parentID?.hashCode() ?: 0)
        result = 31 * result + (lastPinTimestamp?.hashCode() ?: 0)
        result = 31 * result + (rtcRegion?.hashCode() ?: 0)
        result = 31 * result + (videoQualityMode?.hashCode() ?: 0)
        result = 31 * result + (messageCount ?: 0)
        result = 31 * result + (memberCount ?: 0)
        result = 31 * result + (threadMetadata?.hashCode() ?: 0)
        result = 31 * result + (member?.hashCode() ?: 0)
        result = 31 * result + (defaultAutoArchiveDuration ?: 0)
        result = 31 * result + permissions.hashCode()
        result = 31 * result + (flags?.hashCode() ?: 0)
        result = 31 * result + (totalMessageSent ?: 0)
        result = 31 * result + (availableTags?.contentHashCode() ?: 0)
        result = 31 * result + (appliedTags?.contentHashCode() ?: 0)
        result = 31 * result + (defaultReactionEmoji?.hashCode() ?: 0)
        result = 31 * result + (defaultThreadRateLimitPerUser ?: 0)
        result = 31 * result + (defaultSortOrder?.hashCode() ?: 0)
        result = 31 * result + (defaultForumLayout?.hashCode() ?: 0)
        return result
    }

    object Serializer: KSerializer<DiscordChannel> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("DiscordChannel") {
                element<String>("id")
                element<DiscordChannelType>("type") // Experiment
                element<String>("guild_id", isOptional = true)
                element<Int>("position")
                element<Array<PermissionOverwrite>>("permission_overwrites", isOptional = true)
                element<String?>("name", isOptional = true)
                element<String?>("topic", isOptional = true)
                element<Boolean>("nsfw", isOptional = true)
                element<String?>("last_message_id", isOptional = true)
                element<Int>("bitrate", isOptional = true)
                element<Int>("user_limit", isOptional = true)
                element<Int>("rate_limit_per_user", isOptional = true)
                element<Array<DiscordUser>>("recipients", isOptional = true)
                element<String?>("icon", isOptional = true)
                element<String>("owner_id", isOptional = true)
                element<String>("application_id", isOptional = true)
                element<Boolean>("managed", isOptional = true)
                element<String?>("parent_id", isOptional = true)
                element<String?>("last_pin_timestamp", isOptional = true)
                element<String?>("rtc_region", isOptional = true)
                element<Int>("video_quality_mode", isOptional = true)
                element<Int>("message_count", isOptional = true)
                element<Int>("member_count", isOptional = true)
                element<ThreadMetadata>("thread_metadata", isOptional = true)
                element<ThreadMember>("member", isOptional = true)
                element<Int>("default_auto_archive_duration", isOptional = true)
                element<String>("permissions", isOptional = true)
                element<Int>("flags", isOptional = true)
                element<Int>("total_message_sent", isOptional = true)
                element<Array<ForumTag>>("available_tags", isOptional = true)
                element<Array<String>>("applied_tags", isOptional = true)
                element<DefaultReaction?>("default_reaction_emoji", isOptional = true)
                element<Int>("default_thread_rate_limit_per_user", isOptional = true)
                element<SortOrderType?>("default_sort_order", isOptional = true) // Experiment
                element<ForumLayoutType>("default_forum_layout", isOptional = true)
            }

        @OptIn(ExperimentalSerializationApi::class)
        override fun deserialize(decoder: Decoder): DiscordChannel {
            var id: String? = null
            var type: DiscordChannelType? = null
            var guildID: String? = null
            var position: Int? = null
            var permissionOverwrites = arrayOf<PermissionOverwrite>()
            var name: String? = null
            var topic: String? = null
            var nsfw: Boolean? = null
            var lastMessageID: String? = null
            var bitrate: Int? = null
            var userLimit: Int? = null
            var rateLimitPerUser: Int? = null
            var recipients: Array<DiscordUser>? = null
            var icon: String? = null
            var ownerID: String? = null
            var appID: String? = null
            var managed: Boolean? = null
            var parentID: String? = null
            var lastPinTimestamp: Instant? = null
            var rtcRegion: String? = null
            var videoQualityMode: VideoQualityMode? = null
            var messageCount: Int? = null
            var memberCount: Int? = null
            var threadMetadata: ThreadMetadata? = null
            var member: ThreadMember? = null
            var defaultAutoArchiveDuration: Int? = null
            var permissions: Set<DiscordPermission> = setOf()
            var flags: Set<ChannelFlags>? = null
            var totalMessageSent: Int? = null
            var availableTags: Array<ForumTag>? = null
            var appliedTags: Array<String>? = null
            var defaultReactionEmoji: DefaultReaction? = null
            var defaultThreadRateLimitPerUser: Int? = null
            var defaultSortOrder: SortOrderType? = null
            var defaultForumLayout: ForumLayoutType? = null
            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> id = decodeStringElement(descriptor, index)
                    1 -> type = decodeSerializableElement(descriptor, index, DiscordChannelType.Serializer)
                    2 -> guildID = decodeStringElement(descriptor, index)
                    3 -> position = decodeIntElement(descriptor, index)
                    4 -> permissionOverwrites =
                        decodeSerializableElement(descriptor, index, PermissionOverwrite.Serializer.arraySerializer())

                    5 -> name = decodeNullableString(descriptor, index)
                    6 -> topic = decodeNullableString(descriptor, index)
                    7 -> nsfw = decodeBooleanElement(descriptor, index)
                    8 -> lastMessageID = decodeNullableString(descriptor, index)
                    9 -> bitrate = decodeIntElement(descriptor, index)
                    10 -> userLimit = decodeIntElement(descriptor, index)
                    11 -> rateLimitPerUser = decodeIntElement(descriptor, index)
                    12 -> recipients =
                        decodeSerializableElement(descriptor, index, DiscordUser.Serializer.arraySerializer())

                    13 -> icon = decodeNullableString(descriptor, index)
                    14 -> ownerID = decodeStringElement(descriptor, index)
                    15 -> appID = decodeStringElement(descriptor, index)
                    16 -> managed = decodeBooleanElement(descriptor, index)
                    17 -> parentID = decodeNullableString(descriptor, index)
                    18 -> lastPinTimestamp =
                        decodeNullableSerializableElement(descriptor, index, InstantIso8601Serializer)

                    19 -> rtcRegion = decodeNullableString(descriptor, index)
                    20 -> videoQualityMode = decodeSerializableElement(descriptor, index, VideoQualityMode.Serializer)
                    21 -> messageCount = decodeIntElement(descriptor, index)
                    22 -> memberCount = decodeIntElement(descriptor, index)
                    23 -> threadMetadata = decodeSerializableElement(descriptor, index, ThreadMetadata.Serializer)
                    24 -> member = decodeSerializableElement(descriptor, index, ThreadMember.Serializer)
                    25 -> defaultAutoArchiveDuration = decodeIntElement(descriptor, index)
                    26 -> permissions = decodeSerializableElement(descriptor, index, DiscordPermission.SetSerializer)
                    27 -> flags = decodeSerializableElement(descriptor, index, ChannelFlags.SetSerializer)
                    28 -> totalMessageSent = decodeIntElement(descriptor, index)
                    29 -> availableTags =
                        decodeSerializableElement(descriptor, index, ForumTag.serializer().arraySerializer())

                    30 -> appliedTags =
                        decodeSerializableElement(descriptor, index, String.serializer().arraySerializer())

                    31 -> defaultReactionEmoji =
                        decodeNullableSerializableElement(descriptor, index, DefaultReaction.serializer())

                    32 -> defaultThreadRateLimitPerUser = decodeIntElement(descriptor, index)
                    33 -> defaultSortOrder =
                        decodeNullableSerializableElement(descriptor, index, SortOrderType.Serializer)

                    34 -> defaultForumLayout = decodeSerializableElement(descriptor, index, ForumLayoutType.Serializer)
                }
            }

            return DiscordChannel(
                id!!, type!!, guildID, position,
                permissionOverwrites, name, topic, nsfw,
                lastMessageID, bitrate, userLimit, rateLimitPerUser, recipients,
                icon, ownerID, appID, managed, parentID, lastPinTimestamp, rtcRegion,
                videoQualityMode, messageCount, memberCount,
                threadMetadata, member, defaultAutoArchiveDuration, permissions, flags,
                totalMessageSent, availableTags, appliedTags, defaultReactionEmoji,
                defaultThreadRateLimitPerUser, defaultSortOrder, defaultForumLayout
            )
        }

        override fun serialize(encoder: Encoder, value: DiscordChannel) {
            encoder.beginStructure(descriptor).run {
                encodeStringElement(descriptor, 0, value.id)
                encodeSerializableElement(descriptor, 1, DiscordChannelType.Serializer, value.type)
                value.guildID?.let { encodeStringElement(descriptor, 2, value.guildID) }
                value.position?.let { encodeIntElement(descriptor, 3, value.position) }
                if (value.permissionOverwrites.isNotEmpty())
                    encodeSerializableElement(descriptor, 4, PermissionOverwrite.Serializer.arraySerializer(), value.permissionOverwrites)
                value.name?.let { encodeStringElement(descriptor, 5, value.name) }
                value.topic?.let { encodeStringElement(descriptor, 6, value.topic) }
                value.nsfw?.let { encodeBooleanElement(descriptor, 7, value.nsfw) }
                value.lastMessageID?.let { encodeStringElement(descriptor, 8, value.lastMessageID) }
                value.bitrate?.let { encodeIntElement(descriptor, 9, value.bitrate) }
                value.userLimit?.let { encodeIntElement(descriptor, 10, value.userLimit) }
                value.rateLimitPerUser?.let { encodeIntElement(descriptor, 11, value.rateLimitPerUser) }
                if (!value.recipients.isNullOrEmpty())
                    encodeSerializableElement(descriptor, 12, DiscordUser.Serializer.arraySerializer(), value.recipients)
                value.icon?.let { encodeStringElement(descriptor, 13, value.icon) }
                value.ownerID?.let { encodeStringElement(descriptor, 14, value.ownerID) }
                value.appID?.let { encodeStringElement(descriptor, 15, value.appID) }
                value.managed?.let { encodeBooleanElement(descriptor, 16, value.managed) }
                value.parentID?.let { encodeStringElement(descriptor, 17, value.parentID) }
                value.lastPinTimestamp?.let { encodeSerializableElement(descriptor, 18, InstantIso8601Serializer, value.lastPinTimestamp) }
                value.rtcRegion?.let { encodeStringElement(descriptor, 19, value.rtcRegion) }
                value.videoQualityMode?.let { encodeSerializableElement(descriptor, 20, VideoQualityMode.Serializer, value.videoQualityMode) }
                value.messageCount?.let { encodeIntElement(descriptor, 21, value.messageCount) }
                value.memberCount?.let { encodeIntElement(descriptor, 22, value.memberCount) }
                value.threadMetadata?.let { encodeSerializableElement(descriptor, 23, ThreadMetadata.Serializer, value.threadMetadata) }
                value.member?.let { encodeSerializableElement(descriptor, 24, ThreadMember.Serializer, value.member) }
                value.defaultAutoArchiveDuration?.let { encodeIntElement(descriptor, 25, value.defaultAutoArchiveDuration) }
                if (value.permissions.isNotEmpty())
                    encodeSerializableElement(descriptor, 26, DiscordPermission.SetSerializer, value.permissions)
                if (!value.flags.isNullOrEmpty())
                    encodeSerializableElement(descriptor, 27, ChannelFlags.SetSerializer, value.flags)
                value.totalMessageSent?.let { encodeIntElement(descriptor, 28, value.totalMessageSent) }
                value.availableTags?.let { encodeSerializableElement(descriptor, 29, ForumTag.serializer().arraySerializer(), value.availableTags) }
                value.appliedTags?.let { encodeSerializableElement(descriptor, 30, String.serializer().arraySerializer(), value.appliedTags) }
                value.defaultReactionEmoji?.let { encodeSerializableElement(descriptor, 31, DefaultReaction.serializer(), value.defaultReactionEmoji) }
                value.defaultThreadRateLimitPerUser?.let { encodeIntElement(descriptor, 32, value.defaultThreadRateLimitPerUser) }
                value.defaultSortOrder?.let { encodeSerializableElement(descriptor, 33, SortOrderType.Serializer, value.defaultSortOrder) }
                value.defaultForumLayout?.let { encodeSerializableElement(descriptor, 34, ForumLayoutType.Serializer, value.defaultForumLayout) }

                endStructure(descriptor)
            }
        }
    }
}