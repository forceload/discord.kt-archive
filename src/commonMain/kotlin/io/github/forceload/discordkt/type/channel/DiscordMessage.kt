package io.github.forceload.discordkt.type.channel

import io.github.forceload.discordkt.DiscordBot
import io.github.forceload.discordkt.network.RequestUtil
import io.github.forceload.discordkt.type.DiscordChannel
import io.github.forceload.discordkt.type.DiscordEmbed
import io.github.forceload.discordkt.type.DiscordRole
import io.github.forceload.discordkt.type.DiscordUser
import io.github.forceload.discordkt.type.application.DiscordApplication
import io.github.forceload.discordkt.type.gateway.event.dispatch.InteractionType
import io.github.forceload.discordkt.type.guilds.DiscordSticker
import io.github.forceload.discordkt.type.guilds.GuildMember
import io.github.forceload.discordkt.type.guilds.StickerFormatType
import io.github.forceload.discordkt.util.PrimitiveDescriptors
import io.github.forceload.discordkt.util.SerializerUtil
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonPrimitive


@Serializable
class ChannelMention(val id: String, val guildID: String, val type: DiscordChannelType, val name: String)

@Serializable(with = MessageActivityType.Serializer::class)
enum class MessageActivityType(val id: Int) {
    JOIN(1), SPECTATE(2), LISTEN(3), JOIN_REQUEST(5);

    companion object { fun fromID(id: Int) = entries.first { it.id == id } }

    object Serializer: KSerializer<MessageActivityType> {
        override val descriptor: SerialDescriptor =
            PrimitiveDescriptors["MessageActivityType"].INT

        override fun deserialize(decoder: Decoder) =
            MessageActivityType.fromID(decoder.decodeInt())

        override fun serialize(encoder: Encoder, value: MessageActivityType) =
            encoder.encodeInt(value.id)
    }
}

@Serializable
class MessageActivity(
    val type: MessageActivityType,
    @SerialName("party_id") val partyID: String? = null
)

@Serializable
class MessageReference(
    @SerialName("message_id") val messageID: String? = null,
    @SerialName("channel_id") val channelID: String? = null,
    @SerialName("guild_id") val guildID: String? = null,
    @SerialName("fall_if_not_exists") val fallIfNotExists: Boolean = true
)

@Suppress("unused")
enum class MessageFlag(val id: Int) {
    CROSSPOSTED(1 shl 0), IS_CROSSPOST(1 shl 1), SUPPRESS_EMBEDS(1 shl 2),
    SOURCE_MESSAGE_DELETED(1 shl 3), URGENT(1 shl 4), HAS_THREAD(1 shl 5),
    EPHEMERAL(1 shl 6), LOADING(1 shl 7), FAILED_TO_MENTION_SOME_ROLES_IN_THREAD(1 shl 8),
    SUPPRESS_NOTIFICATIONS(1 shl 12), IS_VOICE_MESSAGE(1 shl 13);

    object SetSerializer: KSerializer<Set<MessageFlag>> {
        override val descriptor: SerialDescriptor = PrimitiveDescriptors["MessageFlag"].INT
        override fun deserialize(decoder: Decoder): Set<MessageFlag> {
            val intentFlag = decoder.decodeInt()

            val messageFlagSet = mutableSetOf<MessageFlag>()
            MessageFlag.entries.forEach {
                if (intentFlag and it.id == it.id) messageFlagSet.add(it)
            }

            return messageFlagSet
        }

        override fun serialize(encoder: Encoder, value: Set<MessageFlag>) {
            var result = 0
            value.forEach { result = result or it.id }

            encoder.encodeInt(result)
        }
    }
}

@Serializable
class MessageInteraction(
    val id: String, val type: InteractionType, val name: String, val user: DiscordUser, val member: GuildMember? = null
)

// TODO: Complete this in some way
@Serializable
class MessageComponent

@Suppress("unused")
enum class AttachmentFlag(val id: Int) {
    IS_REMIX(1 shl 2);

    object SetSerializer: KSerializer<Set<AttachmentFlag>> {
        override val descriptor: SerialDescriptor = PrimitiveDescriptors["AttachmentFlag"].INT
        override fun deserialize(decoder: Decoder): Set<AttachmentFlag> {
            val intentFlag = decoder.decodeInt()

            val attachmentFlagSet = mutableSetOf<AttachmentFlag>()
            AttachmentFlag.entries.forEach {
                if (intentFlag and it.id == it.id) attachmentFlagSet.add(it)
            }

            return attachmentFlagSet
        }

        override fun serialize(encoder: Encoder, value: Set<AttachmentFlag>) {
            var result = 0
            value.forEach { result = result or it.id }

            encoder.encodeInt(result)
        }
    }
}

@Serializable
data class AttachmentObject(
    val id: String, val filename: String, val description: String = "",
    @SerialName("content_type") val contentType: String, val size: Int,
    val url: String, @SerialName("proxy_url") val proxyURL: String,
    val height: Int? = null, val width: Int? = null,

    val ephemeral: Boolean = false, @SerialName("duration_secs") val durationSecs: Float = 0f,
    val waveform: String? = null, @Serializable(with = AttachmentFlag.SetSerializer::class) val flags: Set<AttachmentFlag> = setOf()
)

@Serializable
class StickerItem(
    val id: String, val name: String, @SerialName("format_type") val formatType: StickerFormatType
)

@Serializable
class RoleSubscriptionData(
    @SerialName("role_subscription_listing_id") val roleSubscriptionListingID: String,
    @SerialName("tier_name") val tierName: String,
    @SerialName("total_months_subscribed") val totalMonthsSubscribed: Int,
    @SerialName("is_renewal") val isRenewal: Boolean
)

@Serializable
data class ResolvedData(
    val users: Map<String, DiscordUser> = mapOf(),
    val members: Map<String, GuildMember> = mapOf(),
    val roles: Map<String, DiscordRole> = mapOf(),
    val channels: Map<String, DiscordChannel> = mapOf(),
    // val messages: Map<String, DiscordMessage> = mapOf(), // Disabled for recursive issues
    val attachments: Map<String, AttachmentObject> = mapOf()
)

/**
 * https://discord.com/developers/docs/resources/channel#message-object
 */
@Serializable
class DiscordMessage(
    val id: String, @SerialName("channel_id") val channelID: String,
    val author: DiscordUser, val content: String,

    val timestamp: Instant, @SerialName("edited_timestamp") val editedTimestamp: Instant, val tts: Boolean,

    @SerialName("mention_everyone") val mentionEveryone: Boolean, val mentions: Array<DiscordUser>,
    @SerialName("mention_roles") val mentionRoles: Array<String>, @SerialName("mention_channels") val mentionChannels: Array<ChannelMention> = arrayOf(),

    val attachments: Array<AttachmentObject>, val embeds: Array<DiscordEmbed>,
    val reactions: Array<DiscordReaction> = arrayOf(), val nonce: JsonPrimitive? = null, val pinned: Boolean,
    @SerialName("webhook_id") val webhookID: String? = null, val type: DiscordMessageType, val activity: MessageActivity? = null,
    val application: DiscordApplication? = null, @SerialName("application_id") val applicationID: String? = "",
    @SerialName("message_reference") val messageReference: MessageReference? = null,
    @Serializable(with = MessageFlag.SetSerializer::class) val flags: Set<MessageFlag> = setOf(),
    // @SerialName("referenced_message") val referencedMessage: DiscordMessage? = null, // Disabled for recursive issues
    val interaction: MessageInteraction? = null, val thread: DiscordChannel? = null,
    // val components: Array<MessageComponent> = arrayOf(), // FIXME
    val stickerItems: Array<StickerItem>, val stickers: Array<DiscordSticker>,
    val position: Int = 0,

    @SerialName("role_subscription_data") val roleSubscriptionData: RoleSubscriptionData,
    val resolved: ResolvedData? = null
) {
    var referencedMessage: DiscordMessage? = null
        get() =
            if (messageReference?.messageID != null) {
                val messageString = RequestUtil.get(
                    "channels/${messageReference.channelID ?: channelID}/messages/${messageReference.messageID}",
                    DiscordBot.availableInstances[0].token
                )
                field = SerializerUtil.jsonBuild.decodeFromString<DiscordMessage>(messageString); field
            } else null
}