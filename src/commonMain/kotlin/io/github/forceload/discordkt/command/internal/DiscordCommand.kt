package io.github.forceload.discordkt.command.internal

import io.github.forceload.discordkt.command.internal.type.ApplicationCommandOptionType
import io.github.forceload.discordkt.command.internal.type.ApplicationCommandType
import io.github.forceload.discordkt.command.internal.type.ValueType
import io.github.forceload.discordkt.network.RequestUtil
import io.github.forceload.discordkt.type.DiscordLocale
import io.github.forceload.discordkt.type.DiscordPermission
import io.github.forceload.discordkt.type.LocalizationMap
import io.github.forceload.discordkt.type.channel.DiscordChannelType
import io.github.forceload.discordkt.util.SerializerExtension.encodeNumberElement
import io.github.forceload.discordkt.util.SerializerExtension.listSerializer
import io.github.forceload.discordkt.util.SerializerUtil
import io.github.forceload.discordkt.util.logger.DebugLogger
import kotlinx.serialization.ExperimentalSerializationApi
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
@Suppress("DEPRECATION")
@Serializable(with = CommandSerializer::class)
data class DiscordCommand(
    val id: String? = null, val appID: String? = null,
    val name: String, val description: String, val version: String? = null
) {
    var type: ApplicationCommandType?
        = ApplicationCommandType.CHAT_INPUT

    var guildID: String? = null
    val nameLocalizations = LocalizationMap()
    val descriptionLocalizations = LocalizationMap()

    /**
     * https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-option-structure
     */
    @Serializable(with = ApplicationCommandOption.Serializer::class)
    class ApplicationCommandOption(
        val type: ApplicationCommandOptionType,
        val name: String, val description: String, val required: Boolean = false
    ) {
        @Serializable(with = ApplicationCommandOptionChoice.Serializer::class)
        class ApplicationCommandOptionChoice(
            val name: String, val value: ValueType
        ) {
            val nameLocalizations = LocalizationMap()

            object Serializer: KSerializer<ApplicationCommandOptionChoice> {
                override val descriptor: SerialDescriptor =
                    buildClassSerialDescriptor("ApplicationCommandOptionChoice") {
                        element<String>("name")
                        element<LocalizationMap?>("name_localizations", isOptional = true)
                        element<ValueType>("value")
                    }

                @OptIn(ExperimentalSerializationApi::class)
                override fun deserialize(decoder: Decoder): ApplicationCommandOptionChoice {
                    var name: String? = null
                    var nameLocalizations: LocalizationMap? = null
                    var value: ValueType? = null

                    decoder.beginStructure(descriptor).run {
                        loop@ while (true) {
                            when (val i = decodeElementIndex(descriptor)) {
                                CompositeDecoder.DECODE_DONE -> break@loop
                                0 -> name = decodeStringElement(descriptor, i)
                                1 -> nameLocalizations = decodeNullableSerializableElement(descriptor, i, DiscordLocale.localizationSerializer) as HashMap?
                                2 -> value = decodeSerializableElement(descriptor, i, ValueType.Serializer)
                                else -> throw SerializationException("Unknown Index $i")
                            }
                        }

                        endStructure(descriptor)
                    }

                    val result = ApplicationCommandOptionChoice(name!!, value!!)
                    nameLocalizations?.let { result.nameLocalizations.putAll(nameLocalizations!!) }
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

            override fun toString(): String {
                return "ApplicationCommandOptionChoice(name='$name', value=$value, nameLocalizations=$nameLocalizations)"
            }

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is ApplicationCommandOptionChoice) return false

                if (name != other.name) return false
                if (value != other.value) return false
                return nameLocalizations == other.nameLocalizations
            }

            override fun hashCode(): Int {
                var result = name.hashCode()
                result = 31 * result + value.hashCode()
                result = 31 * result + nameLocalizations.hashCode()
                return result
            }
        }

        val nameLocalizations = LocalizationMap()
        val descriptionLocalizations = LocalizationMap()
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

        override fun toString(): String {
            return "ApplicationCommandOption(type=$type, name='$name', description='$description', required=$required, nameLocalizations=$nameLocalizations, descriptionLocalizations=$descriptionLocalizations, choices=$choices, options=$options, channelTypes=$channelTypes, minValue=$minValue, maxValue=$maxValue, minLength=$minLength, maxLength=$maxLength, autoComplete=$autoComplete)"
        }

        object Serializer: KSerializer<ApplicationCommandOption> {
            private var descriptorDepth = 0

            override val descriptor: SerialDescriptor
                get() = buildClassSerialDescriptor("ApplicationCommandOption") {
                    element<ApplicationCommandOptionType>("type")
                    element<String>("name")
                    element<LocalizationMap?>("name_localizations", isOptional = true)
                    element<String>("description")
                    element<LocalizationMap?>("description_localizations", isOptional = true)
                    element<Boolean>("required", isOptional = true)
                    element<List<ApplicationCommandOptionChoice>>("choices", isOptional = true)
                    if (descriptorDepth <= SerializerUtil.commandOptionMaxDepth) {
                        descriptorDepth++
                        element<List<ApplicationCommandOption>>("options", isOptional = true)
                    }

                    element<List<DiscordChannelType>>("channel_types", isOptional = true)
                    element<ValueType>("min_value", isOptional = true)
                    element<ValueType>("max_value", isOptional = true)
                    element<Int>("min_length", isOptional = true)
                    element<Int>("max_length", isOptional = true)
                    element<Boolean>("autocomplete", isOptional = true)
                }

            @OptIn(ExperimentalSerializationApi::class)
            override fun deserialize(decoder: Decoder): ApplicationCommandOption {
                var type: ApplicationCommandOptionType? = null

                var name: String? = null
                var nameLocalizations: LocalizationMap? = LocalizationMap()

                var description: String? = null
                var descriptionLocalizations: LocalizationMap? = LocalizationMap()

                var required = false
                var choices = ArrayList<ApplicationCommandOptionChoice>()
                var options = ArrayList<ApplicationCommandOption>()
                var channelTypes = ArrayList<DiscordChannelType>()

                var minValue: Number? = null
                var maxValue: Number? = null

                var minLength: Int? = null
                var maxLength: Int? = null
                var autoComplete: Boolean? = null

                descriptorDepth = 0
                val descriptorCopy = descriptor
                decoder.beginStructure(descriptorCopy).run {
                    loop@ while (true) {
                        when (val i = decodeElementIndex(descriptorCopy)) {
                            CompositeDecoder.DECODE_DONE -> break@loop
                            0 -> type = decodeSerializableElement(descriptorCopy, i, ApplicationCommandOptionType.Serializer)

                            1 -> name = decodeStringElement(descriptorCopy, i)
                            2 -> nameLocalizations = decodeNullableSerializableElement(descriptorCopy, i, DiscordLocale.localizationSerializer) as HashMap?

                            3 -> description = decodeStringElement(descriptorCopy, i)
                            4 -> descriptionLocalizations = decodeNullableSerializableElement(descriptorCopy, i, DiscordLocale.localizationSerializer) as HashMap?

                            5 -> required = decodeBooleanElement(descriptorCopy, i)
                            6 -> choices = ArrayList(decodeSerializableElement(descriptorCopy, i, ApplicationCommandOptionChoice.Serializer.listSerializer()))
                            7 -> options = ArrayList(decodeSerializableElement(descriptorCopy, i, Serializer.listSerializer()))
                            8 -> channelTypes = ArrayList(decodeSerializableElement(descriptorCopy, i, DiscordChannelType.Serializer.listSerializer()))

                            9 -> minValue = decodeSerializableElement(descriptorCopy, i, ValueType.Serializer).value as Number
                            10 -> maxValue = decodeSerializableElement(descriptorCopy, i, ValueType.Serializer).value as Number

                            11 -> minLength = decodeIntElement(descriptorCopy, i)
                            12 -> maxLength = decodeIntElement(descriptorCopy, i)
                            13 -> autoComplete = decodeBooleanElement(descriptorCopy, i)
                            else -> throw SerializationException("Unknown Index $i")
                        }
                    }

                    endStructure(descriptorCopy)
                }

                val result = ApplicationCommandOption(type!!, name!!, description!!, required)

                nameLocalizations?.let { result.nameLocalizations.putAll(nameLocalizations!!) }
                descriptionLocalizations?.let { result.descriptionLocalizations.putAll(descriptionLocalizations!!) }

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
                descriptorDepth = 0
                val descriptorCopy = descriptor
                encoder.beginStructure(descriptorCopy).run {
                    encodeSerializableElement(descriptorCopy, 0, ApplicationCommandOptionType.Serializer, value.type)

                    encodeStringElement(descriptorCopy, 1, value.name)
                    if (value.nameLocalizations.isNotEmpty()) encodeSerializableElement(
                        descriptorCopy, 2, DiscordLocale.localizationSerializer, value.nameLocalizations
                    )

                    encodeStringElement(descriptorCopy, 3, value.description)
                    if (value.descriptionLocalizations.isNotEmpty()) encodeSerializableElement(
                        descriptorCopy, 4, DiscordLocale.localizationSerializer, value.descriptionLocalizations
                    )

                    encodeBooleanElement(descriptorCopy, 5, value.required)
                    if (value.choices.isNotEmpty()) encodeSerializableElement(
                        descriptorCopy, 6, ApplicationCommandOptionChoice.Serializer.listSerializer(), value.choices
                    )

                    if (value.options.isNotEmpty())
                        encodeSerializableElement(descriptorCopy, 7, Serializer.listSerializer(), value.options)

                    if (value.channelTypes.isNotEmpty()) encodeSerializableElement(
                        descriptorCopy, 8, DiscordChannelType.Serializer.listSerializer(), value.channelTypes
                    )

                    value.minValue?.let { encodeNumberElement(descriptorCopy, 9, value.minValue!!) }
                    value.maxValue?.let { encodeNumberElement(descriptorCopy, 10, value.maxValue!!) }
                    if (value.minLength >= 0) encodeIntElement(descriptorCopy, 11, value.minLength)
                    if (value.maxLength >= 0) encodeIntElement(descriptorCopy, 12, value.maxLength)
                    value.autoComplete?.let { encodeBooleanElement(descriptorCopy, 13, value.autoComplete!!) }

                    endStructure(descriptorCopy)
                }
            }
        }
    }

    var options = ArrayList<ApplicationCommandOption>()
    var defaultMemberPermissions = mutableSetOf<DiscordPermission>()
    var dmPermission: Boolean = true

    @Deprecated("Not recommended for use as field will soon be deprecated", level = DeprecationLevel.WARNING)
    var defaultPermission: Boolean? = null
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
        // if (defaultPermission != other.defaultPermission) return false
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
        // result = 31 * result + defaultPermission.hashCode()
        result = 31 * result + nsfw.hashCode()
        return result
    }

    override fun toString(): String {
        return "DiscordCommand(id=$id, appID=$appID, name='$name', description='$description', version=$version, type=$type, guildID=$guildID, nameLocalizations=$nameLocalizations, descriptionLocalizations=$descriptionLocalizations, options=$options, defaultMemberPermissions=$defaultMemberPermissions, dmPermission=$dmPermission, defaultPermission=$defaultPermission, nsfw=$nsfw)"
    }

    fun destroy(token: String): Boolean {
        var url = "applications/$appID/"
        url += if (guildID != null) "guilds/$guildID/commands/$id"
        else "commands/$id"

        DebugLogger.log(RequestUtil.delete(url, token))
        return true
    }
}