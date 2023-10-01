package io.github.forceload.discordkt.util

import io.github.forceload.discordkt.command.internal.type.ValueType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object SerializerExtension {
    fun <T> KSerializer<T>.listSerializer() = ListSerializer(this)
    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified T : Any> KSerializer<T>.arraySerializer() = ArraySerializer(this)

    fun CompositeEncoder.encodeNumberElement(descriptor: SerialDescriptor, index: Int, value: Number) {
        when (value) {
            is Int -> this.encodeSerializableElement(descriptor, index, ValueType.Serializer, ValueType.IntType(value))
            is Double -> this.encodeSerializableElement(descriptor, index, ValueType.Serializer, ValueType.DoubleType(value))
            else -> throw Exception("Value Type is invalid")
        }
        /*
        when (value) {
            is Byte -> this.encodeByteElement(descriptor, index, value)
            is Double -> this.encodeDoubleElement(descriptor, index, value)
            is Float -> this.encodeFloatElement(descriptor, index, value)
            is Int -> this.encodeIntElement(descriptor, index, value)
            is Long -> this.encodeLongElement(descriptor, index, value)
            is Short -> this.encodeShortElement(descriptor, index, value)
        }*/
    }

    private object DummySerializer: KSerializer<Boolean?> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("Nothing", PrimitiveKind.BOOLEAN)

        @OptIn(ExperimentalSerializationApi::class)
        override fun deserialize(decoder: Decoder) = decoder.decodeNull()

        @OptIn(ExperimentalSerializationApi::class)
        override fun serialize(encoder: Encoder, value: Boolean?) = encoder.encodeNull()
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun CompositeEncoder.encodeNull(descriptor: SerialDescriptor, index: Int) {
        this.encodeNullableSerializableElement(descriptor, index, DummySerializer, null)
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun CompositeDecoder.decodeNull(descriptor: SerialDescriptor, index: Int) {
        this.decodeNullableSerializableElement(descriptor, index, DummySerializer)
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun CompositeDecoder.decodeNullableBoolean(descriptor: SerialDescriptor, index: Int) =
        this.decodeNullableSerializableElement(descriptor, index, Boolean.serializer())

    @OptIn(ExperimentalSerializationApi::class)
    fun CompositeEncoder.encodeNullableString(descriptor: SerialDescriptor, index: Int, value: String?) =
        this.encodeNullableSerializableElement(descriptor, index, String.serializer(), value)

    @OptIn(ExperimentalSerializationApi::class)
    fun CompositeDecoder.decodeNullableString(descriptor: SerialDescriptor, index: Int) =
        this.decodeNullableSerializableElement(descriptor, index, String.serializer())

    @OptIn(ExperimentalSerializationApi::class)
    fun CompositeDecoder.decodeNullableInt(descriptor: SerialDescriptor, index: Int) =
        this.decodeNullableSerializableElement(descriptor, index, Int.serializer())

}