package io.github.forceload.discordkt.type.gateway

import io.github.forceload.discordkt.type.gateway.event.GatewayEventType
import io.github.forceload.discordkt.util.DiscordConstants
import io.github.forceload.discordkt.util.SerializerUtil
import io.github.forceload.discordkt.util.SerializerUtil.makeStructure
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement

@Serializable(with = GatewayEvent.Serializer::class)
class GatewayEvent(val op: Int, val d: GatewayEventType) {
    var s: Int? = null
    var t: String? = null
    internal var auth: String? = null
        set(value) {
            field = value
            d.auth = field
        }

    object Serializer: KSerializer<GatewayEvent> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("GatewayEvent") {
                element<Int>("op")
                element<JsonElement>("d")
                element<Int?>("s", isOptional = true)
                element<String?>("t", isOptional = true)
            }

        @OptIn(ExperimentalSerializationApi::class)
        override fun deserialize(decoder: Decoder): GatewayEvent {
            var op = -1
            var d: GatewayEventType? = null
            var s: Int? = null
            var t: String? = null

            var element: JsonElement? = null
            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> op = decodeIntElement(descriptor, index)
                    1 -> {
                        element = decodeSerializableElement(descriptor, index, JsonElement.serializer())
                        if (op != DiscordConstants.OpCode.DISPATCH || t != null)
                            d = SerializerUtil.jsonBuild.decodeFromJsonElement(GatewayEventType[op, t], element!!)
                    }
                    2 -> s = decodeNullableSerializableElement(descriptor, index, Int.serializer())
                    3 -> {
                        t = decodeNullableSerializableElement(descriptor, index, String.serializer())
                        if (op == DiscordConstants.OpCode.DISPATCH && element != null) {
                            d = SerializerUtil.jsonBuild.decodeFromJsonElement(GatewayEventType[op, t], element!!)
                        }
                    }
                }
            }

            val event = GatewayEvent(op, d!!)
            event.s = s
            event.t = t
            return event
        }

        override fun serialize(encoder: Encoder, value: GatewayEvent) {
            encoder.beginStructure(descriptor).run {
                encodeIntElement(descriptor, 0, value.op)
                encodeSerializableElement(descriptor, 1, GatewayEventType[value.op, value.t], value.d)
                value.s?.let { encodeIntElement(descriptor, 2, value.s!!) }
                value.t?.let { encodeStringElement(descriptor, 3, value.t!!) }

                endStructure(descriptor)
            }
        }

    }

    override fun toString(): String {
        return "GatewayEvent(op=$op, d=$d, s=$s, t=$t)"
    }
}