package io.github.forceload.discordkt.type.gateway.event

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = Reconnect.Serializer::class)
class Reconnect: GatewayEventType(), ServerSideEvent {
    object Serializer: KSerializer<Reconnect> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("Nothing", PrimitiveKind.BOOLEAN)

        override fun deserialize(decoder: Decoder): Reconnect = Reconnect()

        @OptIn(ExperimentalSerializationApi::class)
        override fun serialize(encoder: Encoder, value: Reconnect) =
            encoder.encodeNullableSerializableValue(Boolean.serializer(), null)

    }

    override fun toString() = "HeartbeatACK"
    override val opCode = 7
}