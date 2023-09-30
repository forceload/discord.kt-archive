package io.github.forceload.discordkt.util

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.Json

object SerializerUtil {
    var commandOptionMaxDepth = 16
    val jsonBuild = Json { ignoreUnknownKeys = true }

    fun Decoder.makeStructure(descriptor: SerialDescriptor, block: CompositeDecoder.(Int) -> Unit) {
        this.beginStructure(descriptor).run {
            loop@ while (true) {
                val i = decodeElementIndex(descriptor)
                if (i == CompositeDecoder.DECODE_DONE) break
                else this.block(i)
            }

            endStructure(descriptor)
        }
    }
}
