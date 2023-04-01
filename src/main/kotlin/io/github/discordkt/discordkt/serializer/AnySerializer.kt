package io.github.discordkt.discordkt.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.*
import kotlin.reflect.full.memberProperties

@Serializable
data class Generic<T>(
    val data: T? = null,
    val extensions: Map<String, @Serializable(with = AnySerializer::class) Any>? = null
)

@ExperimentalSerializationApi
object AnySerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Any")

    override fun serialize(encoder: Encoder, value: Any) {
        val serialized = serializeAny(encoder, value)
        return serialized
    }
    private fun serializeAny(encoder: Encoder, value: Any?) {
        when (value) {
            null -> encoder.encodeNull()
            is Map<*, *> -> (encoder as JsonEncoder).encodeJsonElement(mapSerializer(value))
            is List<*> -> (encoder as JsonEncoder).encodeJsonElement(listSerializer(value))

            is Int -> encoder.encodeInt(value)
            is Float -> encoder.encodeFloat(value)
            is Double -> encoder.encodeDouble(value)
            is Char -> encoder.encodeChar(value)
            is Long -> encoder.encodeLong(value)
            is Byte -> encoder.encodeByte(value)
            is Short -> encoder.encodeShort(value)
            is Boolean -> JsonPrimitive(value)
            is String -> JsonPrimitive(value)

            else -> {
                val contents = value::class.memberProperties.associate { property ->
                    property.name to subSerializer(property.getter.call(value))
                }

                (encoder as JsonEncoder).encodeJsonElement(JsonObject(contents))
            }
        }
    }

    private fun subSerializer(value: Any?): JsonElement = when (value) {
        null -> JsonNull
        is Map<*, *> -> {
            val mapContents = value.entries.associate { mapEntry ->
                mapEntry.key.toString() to subSerializer(mapEntry.value)
            }
            JsonObject(mapContents)
        }
        is List<*> -> {
            val arrayContents = value.map { listEntry -> subSerializer(listEntry) }
            JsonArray(arrayContents)
        }
        is Number -> JsonPrimitive(value)
        is Boolean -> JsonPrimitive(value)
        is String -> JsonPrimitive(value)
        else -> {
            val contents = value::class.memberProperties.associate { property ->
                property.name to subSerializer(property.getter.call(value))
            }
            JsonObject(contents)
        }
    }

    private fun mapSerializer(value: Map<*, *>): JsonObject {
        val mapContents = value.entries.associate { mapEntry ->
            val serialized = subSerializer(mapEntry.value)
            if (serialized as? JsonArray != null || serialized as? JsonObject != null) {
                mapEntry.key.toString() to serialized
            } else {
                mapEntry.key.toString() to serialized.jsonPrimitive
            }
        }

        return JsonObject(mapContents)
    }

    private fun listSerializer(value: List<*>): JsonArray {
        val arrayContents = value.map { listEntry ->
            val serialized = subSerializer(listEntry)
            if (serialized as? JsonArray != null || serialized as? JsonObject != null) { serialized } else {
                serialized.jsonPrimitive
            }
        }
        return JsonArray(arrayContents)
    }

    override fun deserialize(decoder: Decoder): Any {
        val jsonDecoder = decoder as JsonDecoder
        val element = jsonDecoder.decodeJsonElement()

        return deserializeJsonElement(element)
    }

    private fun deserializeJsonElement(element: JsonElement): Any = when (element) {
        is JsonObject -> {
            element.mapValues { deserializeJsonElement(it.value) }
        }
        is JsonArray -> {
            element.map { deserializeJsonElement(it) }
        }
        is JsonPrimitive -> element.toString()
    }
}