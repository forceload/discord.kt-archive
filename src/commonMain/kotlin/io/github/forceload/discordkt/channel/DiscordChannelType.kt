package io.github.forceload.discordkt.channel

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

enum class DiscordChannelType(val id: Int) {
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
    GUILD_FORUM(15),
    GUILD_MEDIA(16);

    companion object {
        fun fromID(id: Int) = entries.first { it.id == id }
    }

    object Serializer: KSerializer<DiscordChannelType> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("DiscordChannelType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder) =
            DiscordChannelType.fromID(decoder.decodeInt())

        override fun serialize(encoder: Encoder, value: DiscordChannelType) =
            encoder.encodeInt(value.id)
    }
}