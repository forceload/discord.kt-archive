package io.github.forceload.discordkt.type

import io.github.forceload.discordkt.channel.DiscordChannelType
import io.github.forceload.discordkt.util.SerializerUtil.makeStructure
import kotlinx.datetime.Instant
import kotlinx.datetime.serializers.InstantIso8601Serializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ChannelOverwriteType.Serializer::class)
enum class ChannelOverwriteType(val id: Int) {
    ROLE(0), MEMBER(1);

    companion object {
        fun fromID(id: Int) = entries.first { it.id == id }
    }

    object Serializer: KSerializer<ChannelOverwriteType> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("ChannelOverwriteType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder) = ChannelOverwriteType.fromID(decoder.decodeInt())
        override fun serialize(encoder: Encoder, value: ChannelOverwriteType) = encoder.encodeInt(value.id)
    }
}

@Serializable(with = PermissionOverwrite.Serializer::class)
class PermissionOverwrite(
    val id: String, val type: ChannelOverwriteType,
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
            var type: ChannelOverwriteType? = null
            var allow: Set<DiscordPermission>? = null
            var deny: Set<DiscordPermission>? = null

            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> id = decodeStringElement(descriptor, index)
                    1 -> type = decodeSerializableElement(descriptor, index, ChannelOverwriteType.Serializer)
                    2 -> allow = decodeSerializableElement(descriptor, index, DiscordPermission.SetSerializer)
                    3 -> deny = decodeSerializableElement(descriptor, index, DiscordPermission.SetSerializer)
                }
            }

            return PermissionOverwrite(id!!, type!!, allow!!, deny!!)
        }

        override fun serialize(encoder: Encoder, value: PermissionOverwrite) {
            encoder.beginStructure(descriptor).run {
                encodeStringElement(descriptor, 0, value.id)
                encodeSerializableElement(descriptor, 1, ChannelOverwriteType.Serializer, value.type)
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
                element<Instant>("archive_timestamp")
                element<Boolean>("locked")
                element<Boolean>("invitable", isOptional = true)
                element<Instant?>("create_timestamp", isOptional = true)
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
                element<Instant>("join_timestamp")
                element<Int>("flags") // TODO("Find Usage: Unknown")
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
    val defaultAutoArchiveDuration: Int? = null, val permissions: Set<DiscordPermission>, val flags: Int? = null,
    val totalMessageSent: Int? = null, val availableTags: Array<ForumTag>? = null, val appliedTags: Array<String>? = null,
    val defaultReactionEmoji: DefaultReaction? = null, val defaultThreadRateLimitPerUser: Int? = null,
    val defaultSortOrder: SortOrderType? = null, val defaultForumLayout: ForumLayoutType? = ForumLayoutType.NOT_SET
) {
    object Serializer: KSerializer<DiscordChannel> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("DiscordChannel") {

            }

        override fun deserialize(decoder: Decoder): DiscordChannel {
            decoder.makeStructure(descriptor) {

            }

            TODO("야발 이걸 어케해")
        }

        override fun serialize(encoder: Encoder, value: DiscordChannel) {
            encoder.beginStructure(descriptor).run {
                TODO("분량 ㅁㅊ")
            }
        }
    }
}