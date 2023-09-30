package io.github.forceload.discordkt.type.gateway.event

import io.github.forceload.discordkt.util.DiscordConstants
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = Dispatch.Serializer::class)
class Dispatch: GatewayEventType(), ServerSideEvent {
    object Serializer: KSerializer<Dispatch> {
        override val descriptor: SerialDescriptor
            get() = TODO("Not yet implemented")

        override fun deserialize(decoder: Decoder): Dispatch {
            TODO("Not yet implemented")
        }

        override fun serialize(encoder: Encoder, value: Dispatch) {
            TODO("Not yet implemented")
        }
    }

    override val opCode = DiscordConstants.OpCode.DISPATCH
}