package io.github.forceload.discordkt.type.gateway.event

import io.github.forceload.discordkt.util.DiscordConstants
import io.github.forceload.discordkt.util.SerializerUtil.makeStructure
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = Hello.Serializer::class)
class Hello(val heartbeatInterval: Int): GatewayEventType(), ServerSideEvent {
    object Serializer: KSerializer<Hello> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("Hello") {
                element<Int>("heartbeat_interval")
            }

        override fun deserialize(decoder: Decoder): Hello {
            var heartbeatInterval = 0

            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> heartbeatInterval = decodeIntElement(descriptor, index)
                }
            }

            return Hello(heartbeatInterval)
        }

        override fun serialize(encoder: Encoder, value: Hello) {
            encoder.beginStructure(descriptor).run {
                encodeIntElement(descriptor, 0, value.heartbeatInterval)
                endStructure(descriptor)
            }
        }
    }

    override fun toString(): String {
        return "Hello(heartbeatInterval=$heartbeatInterval)"
    }

    override val opCode = DiscordConstants.OpCode.HELLO
}