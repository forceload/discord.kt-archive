package io.github.forceload.discordkt.type.gateway

import io.github.forceload.discordkt.type.gateway.event.GatewayEventType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = PresenceStatus.Serializer::class)
enum class PresenceStatus(val status: String) {
    ONLINE("online"), DO_NOT_DISTURB("dnd"), IDLE("idle"), INVISIBLE("invisible"), OFFLINE("offline");

    companion object {
        fun fromID(name: String) = entries.first { it.status == name }
    }

    object Serializer: KSerializer<PresenceStatus> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("PresenceStatus", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder) =
            PresenceStatus.fromID(decoder.decodeString())

        override fun serialize(encoder: Encoder, value: PresenceStatus) =
            encoder.encodeString(value.status)
    }
}

@Serializable
class DiscordPresence(
    val since: Int?,
    val activities: Array<DiscordActivity>,
    val status: PresenceStatus,
    val afk: Boolean
): GatewayEventType() {
    override val opCode = 3
}