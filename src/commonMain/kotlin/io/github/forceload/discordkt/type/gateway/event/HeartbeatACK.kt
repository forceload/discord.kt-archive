package io.github.forceload.discordkt.type.gateway.event

import io.github.forceload.discordkt.util.DiscordConstants
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = HeartbeatACK.Serializer::class)
class HeartbeatACK: GatewayEventType(), ServerSideEvent, ClientSideEvent {
    object Serializer: KSerializer<HeartbeatACK> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("Nothing", PrimitiveKind.BOOLEAN)

        override fun deserialize(decoder: Decoder): HeartbeatACK = HeartbeatACK()

        @OptIn(ExperimentalSerializationApi::class)
        override fun serialize(encoder: Encoder, value: HeartbeatACK) =
            encoder.encodeNullableSerializableValue(Boolean.serializer(), null)

    }

    override fun toString(): String {
        return "HeartbeatACK"
    }

    override val opCode = DiscordConstants.OpCode.HEARTBEAT_ACK
}