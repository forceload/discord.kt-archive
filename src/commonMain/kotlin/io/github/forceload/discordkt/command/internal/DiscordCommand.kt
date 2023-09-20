package io.github.forceload.discordkt.command.internal

import io.github.forceload.discordkt.channel.DiscordChannelType
import io.github.forceload.discordkt.command.internal.type.ApplicationCommandOptionType
import io.github.forceload.discordkt.command.internal.type.ApplicationCommandType
import io.github.forceload.discordkt.type.DiscordLocale
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
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

    data class ApplicationCommandOption(
        val type: ApplicationCommandOptionType,
        val name: String, val description: String, val required: Boolean = false
    ) {
        class ApplicationCommandOptionChoice(
            val name: String, val value: Any
        ) {
            val nameLocalizations = HashMap<DiscordLocale, String>()
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

        var autoComplete = true
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
                    result = 31 * result + (minLength + "ApplicationCommandOption.minLength".hashCode())
                    result = 31 * result + (maxLength + "ApplicationCommandOption.maxLength".hashCode())
                }
            }

            if (type == ApplicationCommandOptionType.SUB_COMMAND || type == ApplicationCommandOptionType.SUB_COMMAND_GROUP)
                result = 31 * result + options.hashCode()

            result = 31 * result + autoComplete.hashCode()
            return result
        }

        object Serializer: KSerializer<ApplicationCommandOption> {
            override val descriptor: SerialDescriptor
                get() = TODO("Not yet implemented")

            override fun deserialize(decoder: Decoder): ApplicationCommandOption {
                TODO("Not yet implemented")
            }

            override fun serialize(encoder: Encoder, value: ApplicationCommandOption) {
                TODO("Not yet implemented")
            }
        }
    }

    /**
     * https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-option-structure
     */
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