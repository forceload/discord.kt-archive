@file:Suppress("DEPRECATION")

package io.github.forceload.discordkt.command.internal

import io.github.forceload.discordkt.command.internal.type.ApplicationCommandType
import io.github.forceload.discordkt.type.DiscordLocale
import io.github.forceload.discordkt.util.SerializerExtension.decodeNullableBoolean
import io.github.forceload.discordkt.util.SerializerExtension.encodeNull
import io.github.forceload.discordkt.util.SerializerExtension.listSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object CommandSerializer: KSerializer<DiscordCommand> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("DiscordCommand") {
            element<String>("id", isOptional = true) // `POST Request`에서 사용되지 않기에 Optional
            element<ApplicationCommandType>("type", isOptional = true)
            element<String>("application_id", isOptional = true) // `POST Request`에서 사용되지 않기에 Optional
            element<String>("guild_id", isOptional = true)
            element<String>("name")
            element<HashMap<DiscordLocale, String>?>("name_localizations", isOptional = true)
            element<String>("description")
            element<HashMap<DiscordLocale, String>?>("description_localizations", isOptional = true)
            element<List<DiscordCommand.ApplicationCommandOption>>("options", isOptional = true)
            element<String?>("default_member_permissions")
            element<Boolean>("dm_permission", isOptional = true)
            element<Boolean?>("default_permission", isOptional = true)
            element<Boolean>("nsfw", isOptional = true)
            element<String>("version")
        }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): DiscordCommand {
        var id: String? = null
        var type = ApplicationCommandType.CHAT_INPUT
        var appID: String? = null
        var guildID: String? = null

        var name = ""
        var nameLocalizations: HashMap<DiscordLocale, String>? = null

        var description = ""
        var descriptionLocalizations: HashMap<DiscordLocale, String>? = null

        var options: ArrayList<DiscordCommand.ApplicationCommandOption>? = null
        var defaultMemberPermissions: Set<DiscordPermission>? = null
        var dmPermission: Boolean? = null
        var defaultPermission = true

        var nsfw: Boolean? = null
        var version: String? = null
        decoder.beginStructure(descriptor).run {
            loop@ while (true) {
                when (val i = decodeElementIndex(descriptor)) {
                    CompositeDecoder.DECODE_DONE -> break@loop
                    0 -> id = decodeStringElement(descriptor, i)
                    1 -> type = decodeSerializableElement(descriptor, i, ApplicationCommandType.Serializer)
                    2 -> appID = decodeStringElement(descriptor, i)
                    3 -> guildID = decodeStringElement(descriptor, i)
                    4 -> name = decodeStringElement(descriptor, i)
                    5 -> nameLocalizations = decodeSerializableElement(descriptor, i, DiscordLocale.localizationSerializer) as HashMap
                    6 -> description = decodeStringElement(descriptor, i)
                    7 -> descriptionLocalizations = decodeSerializableElement(descriptor, i, DiscordLocale.localizationSerializer) as HashMap
                    8 -> options = ArrayList(decodeSerializableElement(descriptor, i, DiscordCommand.ApplicationCommandOption.Serializer.listSerializer()))
                    9 -> defaultMemberPermissions = decodeNullableSerializableElement(descriptor, i, DiscordPermission.SetSerializer)
                    10 -> dmPermission = decodeBooleanElement(descriptor, i)
                    11 -> defaultPermission = decodeNullableBoolean(descriptor, i) ?: true
                    12 -> nsfw = decodeBooleanElement(descriptor, i)
                    13 -> version = decodeStringElement(descriptor, i)
                    else -> throw SerializationException("Unknown Index $i")
                }
            }

            endStructure(descriptor)
        }

        val result = DiscordCommand(id, appID, name, description, version)

        result.type = type
        guildID?.let { result.guildID = guildID }

        nameLocalizations?.let { result.nameLocalizations.putAll(nameLocalizations!!) }
        descriptionLocalizations?.let { result.descriptionLocalizations.putAll(descriptionLocalizations!!) }

        options?.let { result.options = options!! }
        defaultMemberPermissions?.let { result.defaultMemberPermissions = defaultMemberPermissions as MutableSet<DiscordPermission> }
        dmPermission?.let { dmPermission = result.dmPermission }
        result.defaultPermission = defaultPermission
        nsfw?.let { result.nsfw = nsfw!! }

        return result
    }

    override fun serialize(encoder: Encoder, value: DiscordCommand) {
        encoder.beginStructure(descriptor).run {
            value.id?.let { encodeStringElement(descriptor, 0, value.id) }
            value.type?.let { encodeSerializableElement(descriptor, 1, ApplicationCommandType.Serializer, value.type!!) }
            value.appID?.let { encodeStringElement(descriptor, 2, value.appID) }
            value.guildID?.let { encodeStringElement(descriptor, 3, value.guildID!!) }

            encodeStringElement(descriptor, 4, value.name)
            if (value.nameLocalizations.isNotEmpty()) encodeSerializableElement(
                descriptor, 5, DiscordLocale.localizationSerializer, value.nameLocalizations
            )

            encodeStringElement(descriptor, 6, value.description)
            if (value.descriptionLocalizations.isNotEmpty()) encodeSerializableElement(
                descriptor, 7, DiscordLocale.localizationSerializer, value.descriptionLocalizations
            )

            if (value.options.isNotEmpty()) encodeSerializableElement(
                descriptor, 8, DiscordCommand.ApplicationCommandOption.Serializer.listSerializer(), value.options
            )

            if (value.defaultMemberPermissions.isNotEmpty()) encodeSerializableElement(
                descriptor, 9, DiscordPermission.SetSerializer, value.defaultMemberPermissions
            ) else encodeNull(descriptor, 9)

            encodeBooleanElement(descriptor, 10, value.dmPermission)
            if (value.defaultPermission != null) encodeBooleanElement(descriptor, 11, value.defaultPermission!!)

            encodeBooleanElement(descriptor, 12, value.nsfw)
            if (value.version != null) encodeStringElement(descriptor, 13, value.version)

            endStructure(descriptor)
        }
    }
}