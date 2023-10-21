package io.github.forceload.discordkt.type.gateway.event.dispatch

import io.github.forceload.discordkt.type.DiscordChannel
import io.github.forceload.discordkt.type.DiscordLocale
import io.github.forceload.discordkt.type.DiscordPermission
import io.github.forceload.discordkt.type.DiscordUser
import io.github.forceload.discordkt.type.channel.DiscordMessage
import io.github.forceload.discordkt.type.gateway.event.dispatch.interaction.ApplicationCommandData
import io.github.forceload.discordkt.type.guilds.GuildMember
import io.github.forceload.discordkt.util.SerializerExtension.arraySerializer
import io.github.forceload.discordkt.util.SerializerUtil.makeStructure
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

enum class InteractionType(val id: Int) {
    PING(1), APPLICATION_COMMAND(2), MESSAGE_COMPONENT(3), APPLICATION_COMMAND_AUTOCOMPLETE(4), MODAL_SUBMIT(5);

    companion object {
        fun fromID(id: Int) = entries.first { it.id == id }
    }

    object Serializer: KSerializer<InteractionType> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("InteractionType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder) =
            InteractionType.fromID(decoder.decodeInt())

        override fun serialize(encoder: Encoder, value: InteractionType) =
            encoder.encodeInt(value.id)
    }
}

@Serializable
open class InteractionData {
    companion object {
        private val serializers = HashMap<InteractionType, KSerializer<InteractionData>>()
        init {
            @Suppress("UNCHECKED_CAST")
            serializers.putAll(mapOf(
                InteractionType.APPLICATION_COMMAND to ApplicationCommandData.serializer()
            ) as Map<InteractionType, KSerializer<InteractionData>>)
        }

        operator fun get(type: InteractionType) = serializers[type]!!
    }
}

@Serializable
class Entitlement(
    val id: String, val skuID: String,
    val applicationID: String, val userID: String? = null, val type: EntitlementType
)

enum class EntitlementType(val id: Int) {
    APPLICATION_SUBSCRIPTION(8);
    companion object { fun fromID(id: Int) = entries.first { it.id == id } }
    
}

@Serializable(with = DiscordInteraction.Serializer::class)
data class DiscordInteraction(
    val id: String, val appID: String, val type: InteractionType,
    val data: InteractionData?, val guildID: String?, val channel: DiscordChannel?,
    val channelID: String?, val member: GuildMember?, val user: DiscordUser?,
    val token: String, val version: Int, val message: DiscordMessage?,
    val appPermissions: Set<DiscordPermission>, val locale: DiscordLocale?,
    val guildLocale: DiscordLocale?, val entitlements: Array<Entitlement>
): DispatchEventType() {

    object Serializer: KSerializer<DiscordInteraction> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("DiscordInteraction") {
                element<String>("id")
                element<String>("application_id")
                element<InteractionType>("type")
                element<InteractionData>("data", isOptional = true)
                element<String>("guild_id", isOptional = true)
                element<DiscordChannel>("channel", isOptional = true)
                element<String>("channel_id", isOptional = true)
                element<GuildMember>("member", isOptional = true)
                element<DiscordUser>("user", isOptional = true)
                element<String>("token")
                element<Int>("version")
                element<DiscordMessage>("message", isOptional = true)
                element<Long>("app_permissions", isOptional = true)
                element<DiscordLocale>("locale", isOptional = true)
                element<DiscordLocale>("guild_locale", isOptional = true)
                element<Array<Entitlement>>("entitlements")
            }

        override fun deserialize(decoder: Decoder): DiscordInteraction {
            var id = ""
            var appID = ""
            var type: InteractionType? = null
            var data: InteractionData? = null
            var guildID: String? = null
            var channel: DiscordChannel? = null
            var channelID: String? = null
            var member: GuildMember? = null
            var user: DiscordUser? = null
            var token = ""
            var version = 1
            var message: DiscordMessage? = null
            var appPermissions = setOf<DiscordPermission>()
            var locale: DiscordLocale? = null
            var guildLocale: DiscordLocale? = null
            var entitlements = arrayOf<Entitlement>()

            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> id = decodeStringElement(descriptor, index)
                    1 -> appID = decodeStringElement(descriptor, index)
                    2 -> type = decodeSerializableElement(descriptor, index, InteractionType.Serializer)
                    3 -> data = decodeSerializableElement(descriptor, index, InteractionData[type!!])
                    4 -> guildID = decodeStringElement(descriptor, index)
                    5 -> channel = decodeSerializableElement(descriptor, index, DiscordChannel.Serializer)
                    6 -> channelID = decodeStringElement(descriptor, index)
                    7 -> member = decodeSerializableElement(descriptor, index, GuildMember.Serializer)
                    8 -> user = decodeSerializableElement(descriptor, index, DiscordUser.Serializer)
                    9 -> token = decodeStringElement(descriptor, index)
                    10 -> version = decodeIntElement(descriptor, index)
                    11 -> message = decodeSerializableElement(descriptor, index, DiscordMessage.serializer())
                    12 -> appPermissions = decodeSerializableElement(descriptor, index, DiscordPermission.SetSerializer)
                    13 -> locale = decodeSerializableElement(descriptor, index, DiscordLocale.Serializer)
                    14 -> guildLocale = decodeSerializableElement(descriptor, index, DiscordLocale.Serializer)
                    15 -> entitlements = decodeSerializableElement(descriptor, index, Entitlement.serializer().arraySerializer())
                }
            }

            return DiscordInteraction(
                id, appID, type!!, data, guildID, channel, channelID, member, user, token, version,
                message, appPermissions, locale, guildLocale, entitlements
            )
        }

        override fun serialize(encoder: Encoder, value: DiscordInteraction) {
            encoder.beginStructure(descriptor).run {
                encodeStringElement(descriptor, 0, value.id)
                encodeStringElement(descriptor, 1, value.appID)
                encodeSerializableElement(descriptor, 2, InteractionType.Serializer, value.type)
                value.data?.let { encodeSerializableElement(descriptor, 3, InteractionData[value.type], value.data) }
                value.guildID?.let { encodeStringElement(descriptor, 4, value.guildID) }
                value.channel?.let { encodeSerializableElement(descriptor, 5, DiscordChannel.Serializer, value.channel) }
                value.channelID?.let { encodeStringElement(descriptor, 6, value.channelID) }
                value.member?.let { encodeSerializableElement(descriptor, 7, GuildMember.Serializer, value.member) }
                value.user?.let { encodeSerializableElement(descriptor, 8, DiscordUser.Serializer, value.user) }
                encodeStringElement(descriptor, 9, value.token)
                encodeIntElement(descriptor, 10, value.version)
                value.message?.let { encodeSerializableElement(descriptor, 11, DiscordMessage.serializer(), value.message) }
                if (value.appPermissions.isNotEmpty()) encodeSerializableElement(descriptor, 12, DiscordPermission.SetSerializer, value.appPermissions)
                value.locale?.let { encodeSerializableElement(descriptor, 13, DiscordLocale.Serializer, value.locale) }
                value.guildLocale?.let { encodeSerializableElement(descriptor, 14, DiscordLocale.Serializer, value.guildLocale) }
                if (value.entitlements.isNotEmpty()) encodeSerializableElement(descriptor, 15, Entitlement.serializer().arraySerializer(), value.entitlements)

                endStructure(descriptor)
            }
        }
    }

    override val code: String = "INTERACTION_CREATE"
}