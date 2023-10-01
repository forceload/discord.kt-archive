package io.github.forceload.discordkt.type.gateway.event.dispatch

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

enum class InteractionType(val id: Int) {
    PING(1), APPLICATION_COMMAND(2), MESSAGE_COMPONENT(3), APPLICATION_COMMAND_AUTOCOMPLETE(4), MODAL_SUBMIT(5);

    companion object {
        fun fromID(id: Int) = entries.first { it.id == id }
    }

    object Serializer: KSerializer<InteractionType> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("InteractionType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder) =
            InteractionType.fromID(decoder.decodeInt())

        override fun serialize(encoder: Encoder, value: InteractionType) =
            encoder.encodeInt(value.id)
    }
}

@Serializable(with = DiscordInteraction.Serializer::class)
class DiscordInteraction(
    val id: String, val appID: String, val type: InteractionType
): DispatchEventType() {

    object Serializer: KSerializer<DiscordInteraction> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("DiscordInteraction") {

            }

        override fun deserialize(decoder: Decoder): DiscordInteraction {
            TODO("Not yet implemented")
        }

        override fun serialize(encoder: Encoder, value: DiscordInteraction) {
            TODO("Not yet implemented")
        }

    }

    override val code: String = "INTERACTION_CREATE"
}