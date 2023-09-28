package io.github.forceload.discordkt

import io.github.forceload.discordkt.command.internal.type.ValueType
import io.github.forceload.discordkt.command.internal.type.ValueType.IntType.Companion.convert
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import org.junit.jupiter.api.Test
import kotlin.test.assertNull

object JustSerializer: KSerializer<JustClass> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("JustClass") {
            element<Int>("a", isOptional = true)
            element<Int>("b", isOptional = true)
            element<ValueType>("c", isOptional = true)
        }

    override fun deserialize(decoder: Decoder): JustClass {
        var a: Int? = null
        var b: Int? = null
        var c: ValueType? = null

        decoder.beginStructure(descriptor).run {
            loop@ while (true) {
                when (val i = decodeElementIndex(descriptor)) {
                    CompositeDecoder.DECODE_DONE -> break@loop
                    0 -> a = decodeIntElement(descriptor, i)
                    1 -> b = decodeIntElement(descriptor, i)
                    2 -> c = decodeSerializableElement(descriptor, i, ValueType.Serializer)
                    else -> throw SerializationException("Unknown Index $i")
                }
            }

            endStructure(descriptor)
        }

        val cls = JustClass()
        cls.a = a; cls.b = b; cls.c = c
        return cls
    }

    override fun serialize(encoder: Encoder, value: JustClass) {
        encoder.beginStructure(descriptor).run {
            value.a?.let { encodeIntElement(descriptor, 0, value.a!!) }
            value.b?.let { encodeIntElement(descriptor, 1, value.b!!) }
            value.c?.let {
                encodeSerializableElement(descriptor, 2, ValueType.Serializer, value.c!!)
            }
            endStructure(descriptor)
        }
    }

}

@Serializable(with = JustSerializer::class)
class JustClass {
    var a: Int? = null
    var b: Int? = null

    var c: ValueType? = null
}

class SerializerTest {
    @Test
    fun customSerializerTest() {
        val newClass = JustClass()

        newClass.b = 5
        var string = Json.encodeToString(newClass)
        var parsed = Json.decodeFromString<JustClass>(string)
        assert(string == "{\"b\":5}")
        assertNull(parsed.a)

        newClass.b = null
        newClass.a = -1
        string = Json.encodeToString(newClass)
        parsed = Json.decodeFromString<JustClass>(string)
        assert(string == "{\"a\":-1}")
        assertNull(parsed.b)

        newClass.a = -275
        newClass.b = 83
        newClass.c = 30.convert()
        string = Json.encodeToString(newClass)
        parsed = Json.decodeFromString<JustClass>(string)
        assert(string == "{\"a\":-275,\"b\":83,\"c\":30}")
        assert(parsed.c!!.value == 30)
    }

    @Test
    fun primitiveSerializerTest() {
        val arrList = ArrayList<String>()

        arrList.add("Hi")
        var string = Json.encodeToString(ListSerializer(String.serializer()), arrList)
        assert(string == "[\"Hi\"]")

        arrList.add("Hello")
        string = Json.encodeToString(ListSerializer(String.serializer()), arrList)
        assert(string == "[\"Hi\",\"Hello\"]")
    }
}