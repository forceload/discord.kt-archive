package io.github.forceload.discordkt.type.channel

import io.github.forceload.discordkt.type.DiscordAttachment
import io.github.forceload.discordkt.type.DiscordEmbed
import io.github.forceload.discordkt.type.DiscordUser
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@Serializable
class ChannelMention(val id: String, val guildID: String, val type: DiscordChannelType, val name: String)

@Serializable(with = MessageActivityType.Serializer::class)
enum class MessageActivityType(val id: Int) {
    JOIN(1), SPECTATE(2), LISTEN(3), JOIN_REQUEST(5);
    object Serializer: KSerializer<MessageActivityType> {
        override val descriptor: SerialDescriptor
            get() = TODO("Not yet implemented")

        override fun deserialize(decoder: Decoder): MessageActivityType {
            TODO("Not yet implemented")
        }

        override fun serialize(encoder: Encoder, value: MessageActivityType) {
            TODO("Not yet implemented")
        }

    }
}

class MessageActivity(val type: MessageActivityType) {

}

/**
 * https://discord.com/developers/docs/resources/channel#message-object
 */
class DiscordMessage(
    val id: String, val channelID: String, val author: DiscordUser,
    val content: String, val timestamp: Instant, val editedTimestamp: Instant,
    val tts: Boolean, val mentionEveryone: Boolean, val mentions: Array<DiscordUser>,
    val mentionRoles: Array<String>, val mentionChannels: Array<ChannelMention>,
    val attachments: Array<DiscordAttachment>, val embeds: Array<DiscordEmbed>,
    val reactions: Array<DiscordReaction>, val nonce: String?, val pinned: Boolean,
    val webhookID: String?, val type: DiscordMessageType, val activity: MessageActivity
) {

}