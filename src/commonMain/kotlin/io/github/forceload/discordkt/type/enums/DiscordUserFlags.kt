package io.github.forceload.discordkt.type.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Suppress("unused")
enum class DiscordUserFlags(val id: Int) {
    STAFF(1 shl 0),
    PARTNER(1 shl 1),
    HYPESQUAD(1 shl 2),
    BUG_HUNTER_LEVEL_1(1 shl 3),
    HYPESQUAD_ONLINE_HOUSE_1(1 shl 6),
    HYPESQUAD_ONLINE_HOUSE_2(1 shl 7),
    HYPESQUAD_ONLINE_HOUSE_3(1 shl 8),
    PREMIUM_EARLY_SUPPORTER(1 shl 9),
    TEAM_PSEUDO_USER(1 shl 10),
    BUG_HUNTER_LEVEL_2(1 shl 14),
    VERIFIED_BOT(1 shl 16),
    VERIFIED_DEVELOPER(1 shl 17),
    CERTIFIED_MODERATOR(1 shl 18),
    BOT_HTTP_INTERACTIONS(1 shl 19),
    ACTIVE_DEVELOPER(1 shl 22);

    object SetSerializer: KSerializer<Set<DiscordUserFlags>> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("DiscordUserFlags", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): Set<DiscordUserFlags> {
            val intentFlag = decoder.decodeInt()

            val permissionSet = mutableSetOf<DiscordUserFlags>()
            DiscordUserFlags.entries.forEach {
                if (intentFlag and it.id == it.id) permissionSet.add(it)
            }

            return permissionSet
        }

        override fun serialize(encoder: Encoder, value: Set<DiscordUserFlags>) {
            var result = 0
            value.forEach { result = result or it.id }

            encoder.encodeInt(result)
        }
    }
}

@Serializable(with = PremiumType.Serializer::class)
enum class PremiumType(val level: Int) {
    NONE(0), NITRO_CLASSIC(1), NITRO(2), NITRO_BASIC(3);

    companion object {
        fun fromID(level: Int) = entries.first { it.level == level }
    }

    object Serializer: KSerializer<PremiumType> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("PremiumType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder) =
            PremiumType.fromID(decoder.decodeInt())

        override fun serialize(encoder: Encoder, value: PremiumType) =
            encoder.encodeInt(value.level)

    }
}