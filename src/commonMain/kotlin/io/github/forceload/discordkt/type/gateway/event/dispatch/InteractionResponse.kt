package io.github.forceload.discordkt.type.gateway.event.dispatch

import io.github.forceload.discordkt.type.gateway.event.dispatch.interaction.callback.InteractionMessageCallback
import io.github.forceload.discordkt.util.PrimitiveDescriptors
import io.github.forceload.discordkt.util.SerializerUtil.makeStructure
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = InteractionResponse.Serializer::class)
class InteractionResponse(
    val type: InteractionCallbackType,
    val data: InteractionCallbackData? = null
) {
    object Serializer: KSerializer<InteractionResponse> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("InteractionResponse") {
                element<InteractionCallbackType>("type")
                element<InteractionCallbackData>("data")
            }

        override fun deserialize(decoder: Decoder): InteractionResponse {
            var type: InteractionCallbackType? = null
            var data: InteractionCallbackData? = null
            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> type = decodeSerializableElement(descriptor, index, InteractionCallbackType.Serializer)
                    1 -> data = decodeSerializableElement(descriptor, index, InteractionCallbackData[type!!])
                }
            }

            return InteractionResponse(type!!, data)
        }

        override fun serialize(encoder: Encoder, value: InteractionResponse) {
            encoder.beginStructure(descriptor).run {
                encodeSerializableElement(descriptor, 0, InteractionCallbackType.Serializer, value.type)
                value.data?.let { encodeSerializableElement(descriptor, 1, InteractionCallbackData[value.type], value.data) }
                endStructure(descriptor)
            }
        }
    }
}

interface InteractionCallbackData {
    companion object {
        val dataSerializerMap = HashMap<InteractionCallbackType, KSerializer<InteractionCallbackData>>()

        init {
            @Suppress("UNCHECKED_CAST")
            dataSerializerMap.putAll(mapOf(
                InteractionCallbackType.CHANNEL_MESSAGE_WITH_SOURCE to InteractionMessageCallback.serializer()
            ) as Map<InteractionCallbackType, KSerializer<InteractionCallbackData>>)
        }

        operator fun get(type: InteractionCallbackType) = dataSerializerMap[type]!!
    }
}

@Serializable(with = InteractionCallbackType.Serializer::class)
enum class InteractionCallbackType(val id: Int) {
    PONG(1), CHANNEL_MESSAGE_WITH_SOURCE(4), DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE(5),
    DEFERRED_UPDATE_MESSAGE(6), UPDATE_MESSAGE(7), APPLICATION_COMMAND_AUTOCOMPLETE_RESULT(8),
    MODAL(9), PREMIUM_REQUIRED(10);

    companion object { fun fromID(id: Int) = entries.first { it.id == id } }
    object Serializer: KSerializer<InteractionCallbackType> {
        override val descriptor: SerialDescriptor =
            PrimitiveDescriptors["InteractionCallbackType"].INT

        override fun deserialize(decoder: Decoder) = fromID(decoder.decodeInt())
        override fun serialize(encoder: Encoder, value: InteractionCallbackType) = encoder.encodeInt(value.id)
    }
}
