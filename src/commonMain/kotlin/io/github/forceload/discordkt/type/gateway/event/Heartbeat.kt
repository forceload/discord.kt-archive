package io.github.forceload.discordkt.type.gateway.event

import io.github.forceload.discordkt.util.DiscordConstants
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = Heartbeat.Serializer::class)
class Heartbeat(val sequenceNumber: Int?): GatewayEventType(), ServerSideEvent, ClientSideEvent {
    object Serializer: KSerializer<Heartbeat> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("sequenceNumber", PrimitiveKind.INT)

        @OptIn(ExperimentalSerializationApi::class)
        override fun deserialize(decoder: Decoder): Heartbeat =
            Heartbeat(decoder.decodeNullableSerializableValue(Int.serializer()))

        @OptIn(ExperimentalSerializationApi::class)
        override fun serialize(encoder: Encoder, value: Heartbeat) =
            encoder.encodeNullableSerializableValue(Int.serializer(), value.sequenceNumber)

    }

    override fun toString(): String {
        return "Hello(sequenceNumber=$sequenceNumber)"
    }

    override val opCode = DiscordConstants.OpCode.HEARTBEAT
}