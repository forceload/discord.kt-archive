package io.github.forceload.discordkt.command.internal

import io.github.forceload.discordkt.command.internal.type.ApplicationCommandType
import io.github.forceload.discordkt.type.DiscordLocale
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
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
            element<ArrayList<DiscordCommand.ApplicationCommandOption>>("options", isOptional = true)
            element<String?>("default_member_permissions")
            element<Boolean>("dm_permission", isOptional = true)
            element<Boolean?>("default_permission", isOptional = true)
            element<Boolean>("nsfw", isOptional = true)
            element<String>("version")
        }

    override fun deserialize(decoder: Decoder): DiscordCommand {
        TODO("Decoding 작성")
    }

    override fun serialize(encoder: Encoder, value: DiscordCommand) {
        encoder.beginStructure(descriptor).run {
            value.id?.let { encodeStringElement(descriptor, 0, value.id) }
            value.type?.let { encodeSerializableElement(descriptor, 1, ApplicationCommandType.Serializer, value.type!!) }
            value.appID?.let { encodeStringElement(descriptor, 2, value.appID) }
            value.guildID?.let { encodeStringElement(descriptor, 3, value.guildID!!) }

            encodeStringElement(descriptor, 4, value.name)
            if (value.nameLocalizations.isNotEmpty()) encodeSerializableElement(
                descriptor, 5, MapSerializer(
                    DiscordLocale.Serializer, String.serializer()
                ), value.nameLocalizations
            )

            encodeStringElement(descriptor, 6, value.description)
            if (value.descriptionLocalizations.isNotEmpty()) encodeSerializableElement(
                descriptor, 7, MapSerializer(
                    DiscordLocale.Serializer, String.serializer()
                ), value.descriptionLocalizations
            )

            if (value.options.isNotEmpty()) encodeSerializableElement(
                descriptor, 8, ListSerializer(
                    DiscordCommand.ApplicationCommandOption.Serializer
                ), value.options
            )

            TODO("추가 Encoding 작업")

            endStructure(descriptor)
        }
    }
}