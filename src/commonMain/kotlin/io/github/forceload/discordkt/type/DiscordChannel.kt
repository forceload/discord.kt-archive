package io.github.forceload.discordkt.type

import io.github.forceload.discordkt.channel.DiscordChannelType
import io.github.forceload.discordkt.util.SerializerExtension.arraySerializer
import io.github.forceload.discordkt.util.SerializerExtension.decodeNullableString
import io.github.forceload.discordkt.util.SerializerUtil.makeStructure
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
class DiscordChannel(
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
                TODO("분량 ㅁㅊ")
            }
        }
    }
}