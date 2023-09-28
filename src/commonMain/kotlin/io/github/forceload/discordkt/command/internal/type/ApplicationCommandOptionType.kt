package io.github.forceload.discordkt.command.internal.type

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ApplicationCommandOptionType.Serializer::class)
enum class ApplicationCommandOptionType(val id: Int) {
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
    ATTACHMENT(11);

    companion object {
        fun fromID(id: Int) = entries.first { it.id == id }
    }

    object Serializer: KSerializer<ApplicationCommandOptionType> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("ApplicationCommandOptionType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder) =
            ApplicationCommandOptionType.fromID(decoder.decodeInt())

        override fun serialize(encoder: Encoder, value: ApplicationCommandOptionType) =
            encoder.encodeInt(value.id)
    }
}