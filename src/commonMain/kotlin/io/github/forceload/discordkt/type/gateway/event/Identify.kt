package io.github.forceload.discordkt.type.gateway.event

import io.github.forceload.discordkt.type.gateway.GatewayConnectionProperty
import io.github.forceload.discordkt.type.gateway.GatewayIntent
import io.github.forceload.discordkt.util.DiscordConstants
import io.github.forceload.discordkt.util.SerializerUtil.makeStructure
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject

@Serializable(with = Identify.Serializer::class)
class Identify(
    val token: String, val properties: GatewayConnectionProperty = GatewayConnectionProperty(),
    val largeThreshold: Int = 50, val intent: MutableSet<GatewayIntent> = mutableSetOf(
        GatewayIntent.DIRECT_MESSAGES, GatewayIntent.DIRECT_MESSAGE_REACTIONS
    )
): GatewayEventType(), ClientSideEvent {
    object Serializer: KSerializer<Identify> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("Identify") {
                element<String>("token")
                element<GatewayConnectionProperty>("properties")
                element<Boolean>("compress", isOptional = true)
                element<Int>("large_threshold", isOptional = true)
                element<Array<Int>>("shard", isOptional = true)
                element<JsonObject>("presence", isOptional = true) // 구현 귀찮아
                element<Int>("intents")
            }

        @OptIn(ExperimentalSerializationApi::class)
        override fun deserialize(decoder: Decoder): Identify {
            var token: String? = null
            var properties: GatewayConnectionProperty? = null
            var largeThreshold: Int? = null
            var intent: Set<GatewayIntent>? = null

            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> token = decodeStringElement(descriptor, index)
                    1 -> properties = decodeSerializableElement(descriptor, index, GatewayConnectionProperty.serializer())
                    2 -> decodeNullableSerializableElement(descriptor, index, Boolean.serializer()) // Skip Serializing
                    3 -> largeThreshold = decodeNullableSerializableElement(descriptor, index, Int.serializer()) ?: 50
                    4 -> decodeNullableSerializableElement(descriptor, index, ArraySerializer(Int.serializer())) // Skip Serializing
                    5 -> decodeNullableSerializableElement(descriptor, index, JsonObject.serializer())
                    6 -> intent = decodeSerializableElement(descriptor, index, GatewayIntent.SetSerializer)
                }
            }

            return Identify(token!!, properties!!, largeThreshold!!, intent!!.toMutableSet())
        }

        @OptIn(ExperimentalSerializationApi::class)
        override fun serialize(encoder: Encoder, value: Identify) {
            encoder.beginStructure(descriptor).run {
                encodeStringElement(descriptor, 0, value.token)
                encodeSerializableElement(descriptor, 1, GatewayConnectionProperty.serializer(), value.properties)
                encodeIntElement(descriptor, 3, value.largeThreshold)
                encodeSerializableElement(descriptor, 6, GatewayIntent.SetSerializer, value.intent)
                endStructure(descriptor)
            }
        }
    }

    override val opCode = DiscordConstants.OpCode.IDENTIFY
}