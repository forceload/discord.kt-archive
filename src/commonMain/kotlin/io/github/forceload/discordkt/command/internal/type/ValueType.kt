package io.github.forceload.discordkt.command.internal.type

import io.github.forceload.discordkt.exception.InvalidArgumentTypeException
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable(with = ValueType.Serializer::class)
open class ValueType(open val value: Any) {
    object Serializer: KSerializer<ValueType> {
        override val descriptor: SerialDescriptor = serialDescriptor<JsonElement>()

        private val invalidJSONTypeException
            get() = InvalidArgumentTypeException("The type is invalid in JSON")

        override fun deserialize(decoder: Decoder): ValueType {
            val jsonDecoder = decoder as? JsonDecoder ?: throw invalidJSONTypeException
            val json = jsonDecoder.decodeJsonElement()
            if (json is JsonPrimitive) {
                return if (json.isString) { StringType(json.content) }
                else if (json.intOrNull != null) { IntType(json.int) } // `doubleOrNull`은 `Int`도 감지하므로 `intOrNull`먼저 감지
                else if (json.doubleOrNull != null) { DoubleType(json.double) }
                else { throw Exception("Type is undefined") }
            }

            throw invalidJSONTypeException
        }

        override fun serialize(encoder: Encoder, value: ValueType) {
            when (value) {
                is StringType -> encoder.encodeString(value.value)
                is DoubleType -> encoder.encodeDouble(value.value)
                is IntType -> encoder.encodeInt(value.value)
            }
        }
    }

    @Serializable(with = IntType.Serializer::class)
    class IntType(override val value: Int): ValueType(value) {
        companion object { fun Int.convert() = IntType(this) }
        object Serializer: KSerializer<IntType> {
            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("IntType", PrimitiveKind.INT)

            override fun deserialize(decoder: Decoder) = IntType(decoder.decodeInt())
            override fun serialize(encoder: Encoder, value: IntType) = encoder.encodeInt(value.value)
        }
    }

    @Serializable(with = StringType.Serializer::class)
    class StringType(override val value: String): ValueType(value) {
        companion object { fun String.convert() = StringType(this) }
        object Serializer: KSerializer<StringType> {
            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("StringType", PrimitiveKind.STRING)

            override fun deserialize(decoder: Decoder) = StringType(decoder.decodeString())
            override fun serialize(encoder: Encoder, value: StringType) = encoder.encodeString(value.value)
        }
    }

    @Serializable(with = DoubleType.Serializer::class)
    class DoubleType(override val value: Double): ValueType(value) {
        companion object { fun Double.convert() = DoubleType(this) }
        object Serializer: KSerializer<DoubleType> {
            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("StringType", PrimitiveKind.STRING)

            override fun deserialize(decoder: Decoder) = DoubleType(decoder.decodeDouble())
            override fun serialize(encoder: Encoder, value: DoubleType) = encoder.encodeDouble(value.value)
        }
    }
}