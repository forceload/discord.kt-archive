package io.github.forceload.discordkt.type

import io.github.forceload.discordkt.util.SerializerExtension.arraySerializer
import io.github.forceload.discordkt.util.SerializerUtil.makeStructure
import kotlinx.datetime.Instant
import kotlinx.datetime.serializers.InstantIso8601Serializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = EmbedType.Serializer::class)
enum class EmbedType(val type: String) {
    RICH("rich"), IMAGE("image"), VIDEO("video"),
    GIFV("gifv"), ARTICLE("article"), LINK("link");

    companion object {
        fun fromID(id: String) = entries.first { it.type == id }
    }

    object Serializer: KSerializer<EmbedType> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("EmbedType", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder) =
            EmbedType.fromID(decoder.decodeString())

        override fun serialize(encoder: Encoder, value: EmbedType) =
            encoder.encodeString(value.type)
    }
}

@Serializable(with = EmbedFooter.Serializer::class)
class EmbedFooter(
    val text: String, val iconURL: String?, val proxyIconURL: String?
) {
    object Serializer: KSerializer<EmbedFooter> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("EmbedFooter") {
                element<String>("text")
                element<String>("icon_url", isOptional = true)
                element<String>("proxy_icon_url", isOptional = true)
            }

        override fun deserialize(decoder: Decoder): EmbedFooter {
            var text: String? = null
            var iconURL: String? = null
            var proxyIconURL: String? = null

            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> text = decodeStringElement(descriptor, index)
                    1 -> iconURL = decodeStringElement(descriptor, index)
                    2 -> proxyIconURL = decodeStringElement(descriptor, index)
                }
            }

            return EmbedFooter(text!!, iconURL, proxyIconURL)
        }

        override fun serialize(encoder: Encoder, value: EmbedFooter) {
            encoder.beginStructure(descriptor).run {
                encodeStringElement(descriptor, 0, value.text)
                value.iconURL?.let { encodeStringElement(descriptor, 1, value.iconURL) }
                value.proxyIconURL?.let { encodeStringElement(descriptor, 2, value.proxyIconURL) }

                endStructure(descriptor)
            }
        }
    }
}

open class EmbedMedia(
    open val url: String?, open val proxyURL: String?,
    open val width: Int?, open val height: Int?
)

open class EmbedMediaSerializer<T: EmbedMedia>: KSerializer<T> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("EmbedMedia") {
            element<String>("url", isOptional = true)
            element<String>("proxy_url", isOptional = true)
            element<Int>("height", isOptional = true)
            element<Int>("width", isOptional = true)
        }

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(decoder: Decoder): T {
        var url: String? = null
        var proxyURL: String? = null
        var width: Int? = null
        var height: Int? = null
        decoder.makeStructure(descriptor) { index ->
            when (index) {
                0 -> url = decodeStringElement(descriptor, index)
                1 -> proxyURL = decodeStringElement(descriptor, index)
                2 -> height = decodeIntElement(descriptor, index)
                3 -> width = decodeIntElement(descriptor, index)
            }
        }

        return EmbedMedia(url, proxyURL, width, height) as T
    }

    override fun serialize(encoder: Encoder, value: T) {
        encoder.beginStructure(descriptor).run {
            value.url?.let { encodeStringElement(descriptor, 0, value.url!!) }
            value.proxyURL?.let { encodeStringElement(descriptor, 1, value.proxyURL!!) }
            value.height?.let { encodeIntElement(descriptor, 2, value.height!!) }
            value.width?.let { encodeIntElement(descriptor, 3, value.width!!) }

            endStructure(descriptor)
        }
    }
}

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = EmbedImage.Serializer::class)
class EmbedImage(
    override val url: String, override val proxyURL: String?,
    override val width: Int?, override val height: Int?
): EmbedMedia(url, proxyURL, width, height) {
    object Serializer: KSerializer<EmbedImage>, EmbedMediaSerializer<EmbedImage>()
}

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = EmbedThumbnail.Serializer::class)
class EmbedThumbnail(
    override val url: String, override val proxyURL: String?,
    override val width: Int?, override val height: Int?
): EmbedMedia(url, proxyURL, width, height) {
    object Serializer: KSerializer<EmbedThumbnail>, EmbedMediaSerializer<EmbedThumbnail>()
}

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = EmbedVideo.Serializer::class)
class EmbedVideo(
    override val url: String?, override val proxyURL: String?,
    override val width: Int?, override val height: Int?
): EmbedMedia(url, proxyURL, width, height) {
    object Serializer: KSerializer<EmbedVideo>, EmbedMediaSerializer<EmbedVideo>()
}

@Serializable
class EmbedProvider(val name: String? = null, val url: String? = null)

@Serializable(with = EmbedAuthor.Serializer::class)
class EmbedAuthor(val name: String, val url: String?, val iconURL: String?, val proxyIconURL: String?) {
    object Serializer: KSerializer<EmbedAuthor> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("EmbedAuthor") {
                element<String>("name")
                element<String>("url", isOptional = true)
                element<String>("icon_url", isOptional = true)
                element<String>("proxy_icon_url", isOptional = true)
            }

        override fun deserialize(decoder: Decoder): EmbedAuthor {
            var name: String? = null
            var url: String? = null
            var iconURL: String? = null
            var proxyIconURL: String? = null

            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> name = decodeStringElement(descriptor, index)
                    1 -> url = decodeStringElement(descriptor, index)
                    2 -> iconURL = decodeStringElement(descriptor, index)
                    3 -> proxyIconURL = decodeStringElement(descriptor, index)
                }
            }

            return EmbedAuthor(name!!, url, iconURL, proxyIconURL)
        }

        override fun serialize(encoder: Encoder, value: EmbedAuthor) {
            encoder.beginStructure(descriptor).run {
                encodeStringElement(descriptor, 0, value.name)
                value.url?.let { encodeStringElement(descriptor, 1, value.url) }
                value.iconURL?.let { encodeStringElement(descriptor, 2, value.iconURL) }
                value.proxyIconURL?.let { encodeStringElement(descriptor, 3, value.proxyIconURL) }

                endStructure(descriptor)
            }
        }
    }
}

@Serializable(with = EmbedField.Serializer::class)
class EmbedField(val name: String, val value: String, val inline: Boolean?) {
    object Serializer: KSerializer<EmbedField> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("EmbedAuthor") {
                element<String>("name")
                element<String>("value")
                element<Boolean>("inline", isOptional = true)
            }

        override fun deserialize(decoder: Decoder): EmbedField {
            var name: String? = null
            var value: String? = null
            var inline: Boolean? = null

            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> name = decodeStringElement(descriptor, index)
                    1 -> value = decodeStringElement(descriptor, index)
                    2 -> inline = decodeBooleanElement(descriptor, index)
                }
            }

            return EmbedField(name!!, value!!, inline)
        }

        override fun serialize(encoder: Encoder, value: EmbedField) {
            encoder.beginStructure(descriptor).run {
                encodeStringElement(descriptor, 0, value.name)
                encodeStringElement(descriptor, 1, value.value)
                value.inline?.let { encodeBooleanElement(descriptor, 2, value.inline) }

                endStructure(descriptor)
            }
        }
    }
}

@Serializable
class DiscordEmbed(
    val title: String?, val type: EmbedType?, val description: String?,
    val url: String?, val timestamp: Instant?, val color: Int?,
    val footer: EmbedFooter?, val image: EmbedImage?, val thumbnail: EmbedThumbnail?,
    val video: EmbedVideo?, val provider: EmbedProvider?, val author: EmbedAuthor?, val fields: Array<EmbedField>
) {
    object Serializer: KSerializer<DiscordEmbed> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("DiscordEmbed") {
                element<String>("title", isOptional = true)
                element<EmbedType>("type", isOptional = true)
                element<String>("description", isOptional = true)
                element<String>("url", isOptional = true)
                element<String>("timestamp", isOptional = true)
                element<Int>("color", isOptional = true)
                element<EmbedFooter>("footer", isOptional = true)
                element<EmbedImage>("image", isOptional = true)
                element<EmbedThumbnail>("thumbnail", isOptional = true)
                element<EmbedVideo>("video", isOptional = true)
                element<EmbedProvider>("provider", isOptional = true)
                element<EmbedAuthor>("author", isOptional = true)
                element<Array<EmbedField>>("fields", isOptional = true)
            }

        override fun deserialize(decoder: Decoder): DiscordEmbed {
            var title: String? = null
            var embedType: EmbedType? = null
            var description: String? = null
            var url: String? = null
            var timestamp: Instant? = null
            var color: Int? = null
            var footer: EmbedFooter? = null
            var image: EmbedImage? = null
            var thumbnail: EmbedThumbnail? = null
            var video: EmbedVideo? = null
            var provider: EmbedProvider? = null
            var author: EmbedAuthor? = null
            var fields = arrayOf<EmbedField>()

            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> title = decodeStringElement(descriptor, index)
                    1 -> embedType = decodeSerializableElement(descriptor, index, EmbedType.Serializer)
                    2 -> description = decodeStringElement(descriptor, index)
                    3 -> url = decodeStringElement(descriptor, index)
                    4 -> timestamp = decodeSerializableElement(descriptor, index, InstantIso8601Serializer)
                    5 -> color = decodeIntElement(descriptor, index)
                    6 -> footer = decodeSerializableElement(descriptor, index, EmbedFooter.Serializer)
                    7 -> image = decodeSerializableElement(descriptor, index, EmbedImage.serializer())
                    8 -> thumbnail = decodeSerializableElement(descriptor, index, EmbedThumbnail.serializer())
                    9 -> video = decodeSerializableElement(descriptor, index, EmbedVideo.serializer())
                    10 -> provider = decodeSerializableElement(descriptor, index, EmbedProvider.serializer())
                    11 -> author = decodeSerializableElement(descriptor, index, EmbedAuthor.Serializer)
                    12 -> fields = decodeSerializableElement(descriptor, index, EmbedField.Serializer.arraySerializer())
                }
            }

            return DiscordEmbed(title, embedType, description, url, timestamp, color, footer, image, thumbnail, video, provider, author, fields)
        }

        override fun serialize(encoder: Encoder, value: DiscordEmbed) {
            encoder.beginStructure(descriptor).run {
                value.title?.let { encodeStringElement(descriptor, 0, value.title) }
                value.type?.let { encodeSerializableElement(descriptor, 1, EmbedType.Serializer, value.type) }
                value.description?.let { encodeStringElement(descriptor, 2, value.description) }
                value.url?.let { encodeStringElement(descriptor, 3, value.url) }
                value.timestamp?.let { encodeSerializableElement(descriptor, 4, InstantIso8601Serializer, value.timestamp) }
                value.color?.let { encodeIntElement(descriptor, 5, value.color) }
                value.footer?.let { encodeSerializableElement(descriptor, 6, EmbedFooter.Serializer, value.footer) }
                value.image?.let { encodeSerializableElement(descriptor, 7, EmbedImage.serializer(), value.image) }
                value.thumbnail?.let { encodeSerializableElement(descriptor, 8, EmbedThumbnail.serializer(), value.thumbnail) }
                value.video?.let { encodeSerializableElement(descriptor, 9, EmbedVideo.serializer(), value.video) }
                value.provider?.let { encodeSerializableElement(descriptor, 10, EmbedProvider.serializer(), value.provider) }
                value.author?.let { encodeSerializableElement(descriptor, 11, EmbedAuthor.Serializer, value.author) }
                if (value.fields.isNotEmpty()) encodeSerializableElement(descriptor, 12, EmbedField.Serializer.arraySerializer(), value.fields)

                endStructure(descriptor)
            }
        }
    }
}