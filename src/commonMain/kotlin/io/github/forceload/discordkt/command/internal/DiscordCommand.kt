package io.github.forceload.discordkt.command.internal

import io.github.forceload.discordkt.channel.DiscordChannelType
import io.github.forceload.discordkt.command.internal.type.ApplicationCommandOptionType
import io.github.forceload.discordkt.command.internal.type.ApplicationCommandType
import io.github.forceload.discordkt.command.internal.type.ValueType
import io.github.forceload.discordkt.type.DiscordLocale
import io.github.forceload.discordkt.util.SerializerExtension.encodeNumberElement
import io.github.forceload.discordkt.util.SerializerExtension.listSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


/**
 * https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-structure
 */
@Serializable(with = CommandSerializer::class)
data class DiscordCommand(
    val id: String? = null, val appID: String? = null,
    val name: String, val description: String, val version: String
) {
    var type: ApplicationCommandType?
        = ApplicationCommandType.CHAT_INPUT

    var guildID: String? = null
    val nameLocalizations = HashMap<DiscordLocale, String>()
    val descriptionLocalizations = HashMap<DiscordLocale, String>()

    /**
     * https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-option-structure
     */
    data class ApplicationCommandOption(
        val type: ApplicationCommandOptionType,
        val name: String, val description: String, val required: Boolean = false
    ) {
        @Serializable
        class ApplicationCommandOptionChoice(
            val name: String, val value: ValueType
        ) {
            val nameLocalizations = HashMap<DiscordLocale, String>()

            object Serializer: KSerializer<ApplicationCommandOptionChoice> {
                override val descriptor: SerialDescriptor =
                    buildClassSerialDescriptor("ApplicationCommandOptionChoice") {
                        element<String>("name")
                        element<HashMap<DiscordLocale, String>?>("name_localizations", isOptional = true)
                        element<ValueType>("value")
                    }

                override fun deserialize(decoder: Decoder): ApplicationCommandOptionChoice {
                    var name: String? = null
                    var nameLocalizations = HashMap<DiscordLocale, String>()
                    var value: ValueType? = null

                    decoder.beginStructure(descriptor).run {
                        loop@ while (true) {
                            when (val i = decodeElementIndex(descriptor)) {
                                CompositeDecoder.DECODE_DONE -> break@loop
                                0 -> name = decodeStringElement(descriptor, i)
                                1 -> nameLocalizations = decodeSerializableElement(descriptor, i, DiscordLocale.localizationSerializer) as HashMap
                                2 -> value = decodeSerializableElement(descriptor, i, ValueType.Serializer)
                                else -> throw SerializationException("Unknown Index $i")
                            }
                        }

                        endStructure(descriptor)
                    }

                    val result = ApplicationCommandOptionChoice(name!!, value!!)
                    result.nameLocalizations.putAll(nameLocalizations)
                    return result
                }

                override fun serialize(encoder: Encoder, value: ApplicationCommandOptionChoice) {
                    encoder.beginStructure(descriptor).run {
                        encodeStringElement(descriptor, 0, value.name)
                        if (value.nameLocalizations.isNotEmpty())
                            encodeSerializableElement(descriptor, 1, DiscordLocale.localizationSerializer, value.nameLocalizations)
                        encodeSerializableElement(descriptor, 2, ValueType.Serializer, value.value)

                        endStructure(descriptor)
                    }
                }
            }
        }

        val nameLocalizations = HashMap<DiscordLocale, String>()
        val descriptionLocalizations = HashMap<DiscordLocale, String>()
        val choices = ArrayList<ApplicationCommandOptionChoice>()

        val options = ArrayList<ApplicationCommandOption>()
        val channelTypes = ArrayList<DiscordChannelType>()

        var minValue: Number? = null
        var maxValue: Number? = null

        var minLength = -1
        var maxLength = -1

        var autoComplete: Boolean? = null
        override fun hashCode(): Int {
            var result = type.hashCode()
            result = 31 * result + name.hashCode()
            result = 31 * result + description.hashCode()
            result = 31 * result + required.hashCode()
            result = 31 * result + nameLocalizations.hashCode()
            result = 31 * result + descriptionLocalizations.hashCode()
            result = 31 * result + channelTypes.hashCode()
            if (
                type == ApplicationCommandOptionType.INTEGER || type == ApplicationCommandOptionType.NUMBER ||
                type == ApplicationCommandOptionType.STRING
            ) {
                result = 31 * result + choices.hashCode()

                if (type == ApplicationCommandOptionType.INTEGER || type == ApplicationCommandOptionType.NUMBER) {
                    result = 31 * result + minValue.hashCode()
                    result = 31 * result + maxValue.hashCode()
                } else {
                    result = 31 * result + minLength.hashCode()
                    result = 31 * result + maxLength.hashCode()
                }
            }

            if (type == ApplicationCommandOptionType.SUB_COMMAND || type == ApplicationCommandOptionType.SUB_COMMAND_GROUP)
                result = 31 * result + options.hashCode()

            result = 31 * result + autoComplete.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as ApplicationCommandOption

            if (type != other.type) return false
            if (name != other.name) return false
            if (description != other.description) return false
            if (required != other.required) return false
            if (nameLocalizations != other.nameLocalizations) return false
            if (descriptionLocalizations != other.descriptionLocalizations) return false
            if (choices != other.choices) return false
            if (options != other.options) return false
            if (channelTypes != other.channelTypes) return false
            if (minValue != other.minValue) return false
            if (maxValue != other.maxValue) return false
            if (minLength != other.minLength) return false
            if (maxLength != other.maxLength) return false
            return autoComplete == other.autoComplete
        }

        object Serializer: KSerializer<ApplicationCommandOption> {
            override val descriptor: SerialDescriptor =
                buildClassSerialDescriptor("ApplicationCommandOption") {
                    element<ApplicationCommandOptionType>("type")
                    element<String>("name")
                    element<HashMap<DiscordLocale, String>?>("name_localizations", isOptional = true)
                    element<String>("description")
                    element<HashMap<DiscordLocale, String>?>("description_localizations", isOptional = true)
                    element<Boolean>("required", isOptional = true)
                    element<ArrayList<ApplicationCommandOptionChoice>>("choices", isOptional = true)
                    element<ArrayList<ApplicationCommandOption>>("options", isOptional = true)
                    element<ArrayList<DiscordChannelType>>("channel_types", isOptional = true)
                    element<Number>("min_value", isOptional = true)
                    element<Number>("max_value", isOptional = true)
                    element<Int>("min_length", isOptional = true)
                    element<Int>("max_length", isOptional = true)
                    element<Boolean>("autocomplete", isOptional = true)
                }

            override fun deserialize(decoder: Decoder): ApplicationCommandOption {
                var type: ApplicationCommandOptionType? = null

                var name: String? = null
                var nameLocalizations = HashMap<DiscordLocale, String>()

                var description: String? = null
                var descriptionLocalizations = HashMap<DiscordLocale, String>()

                var required = false
                var choices = ArrayList<ApplicationCommandOptionChoice>()
                var options = ArrayList<ApplicationCommandOption>()
                var channelTypes = ArrayList<DiscordChannelType>()

                var minValue: Number? = null
                var maxValue: Number? = null

                var minLength: Int? = null
                var maxLength: Int? = null
                var autoComplete: Boolean? = null

                decoder.beginStructure(descriptor).run {
                    loop@ while (true) {
                        when (val i = decodeElementIndex(descriptor)) {
                            CompositeDecoder.DECODE_DONE -> break@loop
                            0 -> type = decodeSerializableElement(descriptor, i, ApplicationCommandOptionType.Serializer)

                            1 -> name = decodeStringElement(descriptor, i)
                            2 -> nameLocalizations = decodeSerializableElement(descriptor, i, DiscordLocale.localizationSerializer) as HashMap

                            3 -> description = decodeStringElement(descriptor, i)
                            4 -> descriptionLocalizations = decodeSerializableElement(descriptor, i, DiscordLocale.localizationSerializer) as HashMap

                            5 -> required = decodeBooleanElement(descriptor, i)
                            6 -> choices = decodeSerializableElement(descriptor, i, ApplicationCommandOptionChoice.Serializer.listSerializer()) as ArrayList
                            7 -> options = decodeSerializableElement(descriptor, i, Serializer.listSerializer()) as ArrayList
                            8 -> channelTypes = decodeSerializableElement(descriptor, i, DiscordChannelType.Serializer.listSerializer()) as ArrayList

                            9 -> minValue = when (type) {
                                ApplicationCommandOptionType.INTEGER -> decodeIntElement(descriptor, i)
                                ApplicationCommandOptionType.NUMBER -> decodeDoubleElement(descriptor, i)
                                else -> throw SerializationException("Unknown type for `min_value` field")
                            }

                            10 -> maxValue = when (type) {
                                ApplicationCommandOptionType.INTEGER -> decodeIntElement(descriptor, i)
                                ApplicationCommandOptionType.NUMBER -> decodeDoubleElement(descriptor, i)
                                else -> throw SerializationException("Unknown type for `max_value` field")
                            }

                            11 -> minLength = decodeIntElement(descriptor, i)
                            12 -> maxLength = decodeIntElement(descriptor, i)
                            13 -> autoComplete = decodeBooleanElement(descriptor, i)
                            else -> throw SerializationException("Unknown Index $i")
                        }
                    }

                    endStructure(descriptor)
                }

                val result = ApplicationCommandOption(type!!, name!!, description!!, required)

                result.nameLocalizations.putAll(nameLocalizations)
                result.descriptionLocalizations.putAll(descriptionLocalizations)

                result.choices.addAll(choices)
                result.options.addAll(options)
                result.channelTypes.addAll(channelTypes)

                minValue?.let { result.minValue = minValue }
                maxValue?.let { result.maxValue = maxValue }
                minLength?.let { result.minLength = minLength!! }
                maxLength?.let { result.maxLength = maxLength!! }

                autoComplete?.let { result.autoComplete = autoComplete }
                return result
            }

            override fun serialize(encoder: Encoder, value: ApplicationCommandOption) {
                encoder.beginStructure(descriptor).run {
                    encodeSerializableElement(descriptor, 0, ApplicationCommandOptionType.Serializer, value.type)

                    encodeStringElement(descriptor, 1, value.name)
                    if (value.nameLocalizations.isNotEmpty()) encodeSerializableElement(
                        descriptor, 2, DiscordLocale.localizationSerializer, value.nameLocalizations
                    )

                    encodeStringElement(CommandSerializer.descriptor, 3, value.description)
                    if (value.descriptionLocalizations.isNotEmpty()) encodeSerializableElement(
                        descriptor, 4, DiscordLocale.localizationSerializer, value.descriptionLocalizations
                    )

                    encodeBooleanElement(descriptor, 5, value.required)
                    if (value.choices.isNotEmpty()) encodeSerializableElement(
                        descriptor, 6, ApplicationCommandOptionChoice.Serializer.listSerializer(), value.choices
                    )

                    if (value.options.isNotEmpty())
                        encodeSerializableElement(descriptor, 7, Serializer.listSerializer(), value.options)

                    if (value.channelTypes.isNotEmpty()) encodeSerializableElement(
                        descriptor, 8, DiscordChannelType.Serializer.listSerializer(), value.channelTypes
                    )

                    value.minValue?.let { encodeNumberElement(descriptor, 9, value.minValue!!) }
                    value.maxValue?.let { encodeNumberElement(descriptor, 10, value.maxValue!!) }
                    if (value.minLength >= 0) encodeIntElement(descriptor, 11, value.minLength)
                    if (value.maxLength >= 0) encodeIntElement(descriptor, 12, value.maxLength)
                    value.autoComplete?.let { encodeBooleanElement(descriptor, 13, value.autoComplete!!) }

                    endStructure(descriptor)
                }
            }
        }
    }

    var options = ArrayList<ApplicationCommandOption>()
    var defaultMemberPermissions = mutableSetOf<DiscordPermission>()
    var dmPermission: Boolean = true

    var defaultPermission: Boolean = true // Will be Deprecated
    var nsfw: Boolean = false
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DiscordCommand

        if (name != other.name) return false
        if (description != other.description) return false
        if (type != other.type) return false
        if (guildID != other.guildID) return false
        if (nameLocalizations != other.nameLocalizations) return false
        if (descriptionLocalizations != other.descriptionLocalizations) return false
        if (options != other.options) return false
        if (defaultMemberPermissions != other.defaultMemberPermissions) return false
        if (dmPermission != other.dmPermission) return false
        if (defaultPermission != other.defaultPermission) return false
        return nsfw == other.nsfw
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + guildID.hashCode()
        result = 31 * result + nameLocalizations.hashCode()
        result = 31 * result + descriptionLocalizations.hashCode()
        result = 31 * result + options.hashCode()
        result = 31 * result + defaultMemberPermissions.hashCode()
        result = 31 * result + dmPermission.hashCode()
        result = 31 * result + defaultPermission.hashCode()
        result = 31 * result + nsfw.hashCode()
        return result
    }
}