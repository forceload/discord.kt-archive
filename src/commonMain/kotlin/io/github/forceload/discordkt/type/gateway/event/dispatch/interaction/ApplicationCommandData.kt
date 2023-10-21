package io.github.forceload.discordkt.type.gateway.event.dispatch.interaction

import io.github.forceload.discordkt.command.internal.type.ApplicationCommandOptionType
import io.github.forceload.discordkt.command.internal.type.ApplicationCommandType
import io.github.forceload.discordkt.type.channel.ResolvedData
import io.github.forceload.discordkt.type.gateway.event.dispatch.InteractionData
import io.github.forceload.discordkt.util.SerializerExtension.arraySerializer
import io.github.forceload.discordkt.util.SerializerUtil
import io.github.forceload.discordkt.util.SerializerUtil.makeStructure
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement

@Serializable
data class ApplicationCommandData(
    val id: String, val name: String, val type: ApplicationCommandType,
    val resolved: ResolvedData? = null, val options: Array<ApplicationCommandInteractionDataOption> = arrayOf()
): InteractionData() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ApplicationCommandData) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (type != other.type) return false
        if (resolved != other.resolved) return false
        return options.contentEquals(other.options)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + (resolved?.hashCode() ?: 0)
        result = 31 * result + options.contentHashCode()
        return result
    }
}

@Serializable(with = ApplicationCommandInteractionDataOption.Serializer::class)
data class ApplicationCommandInteractionDataOption(
    val name: String, val type: ApplicationCommandOptionType, val value: JsonElement? = null,
    val options: Array<ApplicationCommandInteractionDataOption>, val focused: Boolean = true
) {
    object Serializer: KSerializer<ApplicationCommandInteractionDataOption> {
        private var descriptorDepth = 0

        override val descriptor: SerialDescriptor
            get() = buildClassSerialDescriptor("ApplicationCommandInteractionDataOption") {
                element<String>("name")
                element<ApplicationCommandOptionType>("type")
                element<JsonElement>("value", isOptional = true)
                if (descriptorDepth <= SerializerUtil.commandOptionMaxDepth) {
                    descriptorDepth++
                    element<Array<ApplicationCommandInteractionDataOption>>("options", isOptional = true)
                }

                element<Boolean>("focused", isOptional = true)
            }


        override fun deserialize(decoder: Decoder): ApplicationCommandInteractionDataOption {
            var name = ""
            var type: ApplicationCommandOptionType? = null
            var value: JsonElement? = null
            var options: Array<ApplicationCommandInteractionDataOption> = arrayOf()
            var focused = true

            descriptorDepth = 0
            val descriptorCopy = descriptor
            decoder.makeStructure(descriptorCopy) { index ->
                when (index) {
                    0 -> name = decodeStringElement(descriptorCopy, index)
                    1 -> type = decodeSerializableElement(descriptorCopy, index, ApplicationCommandOptionType.Serializer)
                    2 -> value = decodeSerializableElement(descriptorCopy, index, JsonElement.serializer())
                    3 -> options = decodeSerializableElement(descriptorCopy, index, Serializer.arraySerializer())
                    4 -> focused = decodeBooleanElement(descriptorCopy, index)
                }
            }

            return ApplicationCommandInteractionDataOption(name, type!!, value, options, focused)
        }

        override fun serialize(encoder: Encoder, value: ApplicationCommandInteractionDataOption) {
            descriptorDepth = 0
            val descriptorCopy = descriptor
            encoder.beginStructure(descriptorCopy).run {
                encodeStringElement(descriptorCopy, 0, value.name)
                encodeSerializableElement(descriptorCopy, 1, ApplicationCommandOptionType.Serializer, value.type)
                value.value?.let { encodeSerializableElement(descriptorCopy, 2, JsonElement.serializer(), value.value) }
                if (value.options.isNotEmpty()) encodeSerializableElement(descriptorCopy, 3, Serializer.arraySerializer(), value.options)
                encodeBooleanElement(descriptorCopy, 4, value.focused)

                endStructure(descriptorCopy)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ApplicationCommandInteractionDataOption) return false

        if (name != other.name) return false
        if (type != other.type) return false
        if (value != other.value) return false
        if (!options.contentEquals(other.options)) return false
        return focused == other.focused
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + (value?.hashCode() ?: 0)
        result = 31 * result + options.contentHashCode()
        result = 31 * result + focused.hashCode()
        return result
    }
}
