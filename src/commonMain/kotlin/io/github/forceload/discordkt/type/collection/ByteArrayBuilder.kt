package io.github.forceload.discordkt.type.collection

class ByteArrayBuilder {
    private val array = ArrayList<ByteArray>()
    private var length = 0

    operator fun plusAssign(byteArray: ByteArray) { length += byteArray.size; array.add(byteArray) }
    inline operator fun plusAssign(byte: Byte) { this += ByteArray(1) { byte } }

    fun build(): ByteArray {
        var offset = 0; val result = ByteArray(length)
        for (byteArray in array) { byteArray.copyInto(result, offset); offset += byteArray.size }
        return result
    }
}