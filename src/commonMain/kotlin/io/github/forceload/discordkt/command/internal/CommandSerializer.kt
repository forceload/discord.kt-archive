package io.github.forceload.discordkt.command.internal

import io.github.forceload.discordkt.command.internal.type.ApplicationCommandType
import io.github.forceload.discordkt.type.DiscordLocale
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure

object CommandSerializer: KSerializer<DiscordCommand> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("DiscordCommand") {
            element<String>("id")
            element<ApplicationCommandType>("type", isOptional = true)
            element<String>("application_id")
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
        TODO("귀찮음")
    }

    override fun serialize(encoder: Encoder, value: DiscordCommand) {
        TODO("이런거 어떻게 하는 거에요?")
    }
}