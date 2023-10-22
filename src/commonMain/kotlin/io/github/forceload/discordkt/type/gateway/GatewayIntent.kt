package io.github.forceload.discordkt.type.gateway

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * https://discord.com/developers/docs/topics/gateway#gateway-intents
 */

enum class GatewayIntent(val id: Int) {
    GUILDS(1 shl 0),
    GUILD_MEMBERS(1 shl 1),
    GUILD_MODERATION(1 shl 2),
    GUILD_EMOJIS_AND_STICKERS(1 shl 3),
    GUILD_INTEGRATIONS(1 shl 4),
    GUILD_WEBHOOKS(1 shl 5),
    GUILD_INVITES(1 shl 6),
    GUILD_VOICE_STATES(1 shl 7),
    GUILD_PRESENCES(1 shl 8),
    GUILD_MESSAGES(1 shl 9),
    GUILD_MESSAGE_REACTIONS(1 shl 10),
    GUILD_MESSAGE_TYPING(1 shl 11),
    DIRECT_MESSAGES(1 shl 12),
    DIRECT_MESSAGE_REACTIONS(1 shl 13),
    DIRECT_MESSAGE_TYPING (1 shl 14),
    MESSAGE_CONTENT (1 shl 15),
    GUILD_SCHEDULED_EVENTS (1 shl 16),
    AUTO_MODERATION_CONFIGURATION (1 shl 20),
    AUTO_MODERATION_EXECUTION(1 shl 21);

    object SetSerializer: KSerializer<Set<GatewayIntent>> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("GatewayIntent", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): Set<GatewayIntent> {
            val intentFlag = decoder.decodeInt()

            val permissionSet = mutableSetOf<GatewayIntent>()
            GatewayIntent.entries.forEach {
                if (intentFlag and it.id == it.id) permissionSet.add(it)
            }

            return permissionSet
        }

        override fun serialize(encoder: Encoder, value: Set<GatewayIntent>) {
            var result = 0
            value.forEach { result = result or it.id }

            encoder.encodeInt(result)
        }
    }
}