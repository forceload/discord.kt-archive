package io.github.forceload.discordkt.type.gateway.event.dispatch.interaction.callback

import io.github.forceload.discordkt.type.DiscordEmbed
import io.github.forceload.discordkt.type.channel.AllowedMentions
import io.github.forceload.discordkt.type.channel.DiscordAttachment
import io.github.forceload.discordkt.type.channel.MessageComponent
import io.github.forceload.discordkt.type.channel.MessageFlag
import io.github.forceload.discordkt.type.gateway.event.dispatch.InteractionCallbackData
import io.github.forceload.discordkt.util.SerializerExtension.arraySerializer
import io.github.forceload.discordkt.util.SerializerUtil.makeStructure
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = InteractionMessageCallback.Serializer::class)
class InteractionMessageCallback(
    val tts: Boolean? = null,
    val content: String? = null,
    val embeds: Array<DiscordEmbed> = arrayOf(),
    val allowedMentions: AllowedMentions? = null,
    val flags: Set<MessageFlag> = setOf(),
    val components: Array<MessageComponent> = arrayOf(),
    val attachments: Array<DiscordAttachment> = arrayOf()
): InteractionCallbackData {
    object Serializer: KSerializer<InteractionMessageCallback> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("InteractionMessageCallback") {
                element<Boolean>("tts", isOptional = true)
                element<String>("content", isOptional = true)
                element<Array<DiscordEmbed>>("embeds", isOptional = true)
                element<AllowedMentions>("allowed_mentions", isOptional = true)
                element<Int>("flags")
                element<Array<MessageComponent>>("components")
                element<Array<DiscordAttachment>>("attachments")
            }

        override fun deserialize(decoder: Decoder): InteractionMessageCallback {
            var tts: Boolean? = null
            var content: String? = null
            var embeds = arrayOf<DiscordEmbed>()
            var allowedMentions: AllowedMentions? = null
            var flags: Set<MessageFlag> = setOf()
            var components = arrayOf<MessageComponent>()
            var attachments = arrayOf<DiscordAttachment>()
            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> tts = decodeBooleanElement(descriptor, index)
                    1 -> content = decodeStringElement(descriptor, index)
                    2 -> embeds = decodeSerializableElement(descriptor, index, DiscordEmbed.Serializer.arraySerializer())
                    3 -> allowedMentions = decodeSerializableElement(descriptor, index, AllowedMentions.serializer())
                    4 -> flags = decodeSerializableElement(descriptor, index, MessageFlag.SetSerializer)
                    5 -> components = decodeSerializableElement(descriptor, index, MessageComponent.serializer().arraySerializer())
                    6 -> attachments = decodeSerializableElement(descriptor, index, DiscordAttachment.serializer().arraySerializer())
                }
            }

            return InteractionMessageCallback(tts, content, embeds, allowedMentions, flags, components, attachments)
        }

        override fun serialize(encoder: Encoder, value: InteractionMessageCallback) {
            encoder.beginStructure(descriptor).run {
                value.tts?.let { encodeBooleanElement(descriptor, 0, value.tts) }
                value.content?.let { encodeStringElement(descriptor, 1, value.content) }
                if (value.embeds.isNotEmpty()) encodeSerializableElement(descriptor, 2, DiscordEmbed.Serializer.arraySerializer(), value.embeds)
                value.allowedMentions?.let { encodeSerializableElement(descriptor, 3, AllowedMentions.serializer(), value.allowedMentions) }
                if (value.flags.isNotEmpty()) encodeSerializableElement(descriptor, 4, MessageFlag.SetSerializer, value.flags)
                if (value.components.isNotEmpty()) encodeSerializableElement(descriptor, 5, MessageComponent.serializer().arraySerializer(), value.components)
                if (value.attachments.isNotEmpty()) encodeSerializableElement(descriptor, 6, DiscordAttachment.serializer().arraySerializer(), value.attachments)

                endStructure(descriptor)
            }
        }
    }
}