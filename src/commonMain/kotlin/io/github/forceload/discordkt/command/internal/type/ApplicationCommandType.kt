package io.github.forceload.discordkt.command.internal.type

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = CommandTypeSerializer::class)
enum class ApplicationCommandType(val id: Int) {
    CHAT_INPUT(1), USER(2), MESSAGE(3);

    companion object {
        fun fromID(id: Int) = entries.first { it.id == id }
    }
}

object CommandTypeSerializer: KSerializer<ApplicationCommandType> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ApplicationCommandType", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder) =
        ApplicationCommandType.fromID(decoder.decodeInt())

    override fun serialize(encoder: Encoder, value: ApplicationCommandType) =
        encoder.encodeInt(value.id)
}