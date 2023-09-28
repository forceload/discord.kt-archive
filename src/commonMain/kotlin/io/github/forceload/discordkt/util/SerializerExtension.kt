package io.github.forceload.discordkt.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder

object SerializerExtension {
    fun <T> KSerializer<T>.listSerializer() = ListSerializer(this)
    fun CompositeEncoder.encodeNumberElement(descriptor: SerialDescriptor, index: Int, value: Number) {
        when (value) {
            is Byte -> this.encodeByteElement(descriptor, index, value)
            is Double -> this.encodeDoubleElement(descriptor, index, value)
            is Float -> this.encodeFloatElement(descriptor, index, value)
            is Int -> this.encodeIntElement(descriptor, index, value)
            is Long -> this.encodeLongElement(descriptor, index, value)
            is Short -> this.encodeShortElement(descriptor, index, value)
        }
    }
}