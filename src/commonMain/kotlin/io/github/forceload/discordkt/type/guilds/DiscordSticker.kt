package io.github.forceload.discordkt.type.guilds

import io.github.forceload.discordkt.type.DiscordUser
import io.github.forceload.discordkt.util.PrimitiveDescriptors
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = StickerType.Serializer::class)
enum class StickerType(val id: Int) {
    STANDARD(1), GUILD(2);

    companion object { fun fromID(id: Int) = entries.first { it.id == id } }
    object Serializer: KSerializer<StickerType> {
        override val descriptor: SerialDescriptor = PrimitiveDescriptors["StickerType"].INT
        override fun deserialize(decoder: Decoder) = fromID(decoder.decodeInt())
        override fun serialize(encoder: Encoder, value: StickerType) = encoder.encodeInt(value.id)
    }
}

@Serializable(with = StickerFormatType.Serializer::class)
enum class StickerFormatType(val id: Int) {
    PNG(1), APNG(2), LOTTIE(3), GIF(4);

    companion object { fun fromID(id: Int) = entries.first { it.id == id } }
    object Serializer: KSerializer<StickerFormatType> {
        override val descriptor: SerialDescriptor = PrimitiveDescriptors["StickerFormatType"].INT
        override fun deserialize(decoder: Decoder) = fromID(decoder.decodeInt())
        override fun serialize(encoder: Encoder, value: StickerFormatType) = encoder.encodeInt(value.id)
    }
}

@Serializable
class DiscordSticker(
    val id: String, @SerialName("pack_id") val packID: String? = null,
    val name: String, val description: String?, val tags: String,
    @Deprecated("Deprecated previously the sticker asset hash, now an empty string") val asset: String = "",
    val type: StickerType, @SerialName("format_type") val formatType: StickerFormatType,
    val available: Boolean = false,

    @SerialName("guild_id") val guildID: String = "", val user: DiscordUser? = null,
    @SerialName("sort_value") var sortValue: Int = -1
)