package io.github.forceload.discordkt.type

import io.github.forceload.discordkt.util.SerializerExtension.arraySerializer
import io.github.forceload.discordkt.util.SerializerExtension.decodeNullableString
import io.github.forceload.discordkt.util.SerializerUtil.makeStructure
import kotlinx.datetime.Instant
import kotlinx.datetime.serializers.InstantIso8601Serializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

enum class GuildMemberFlags(val id: Int) {
    DID_REJOIN(1 shl 0), COMPLETED_ONBOARDING(1 shl 1),
    BYPASSES_VERIFICATION(1 shl 2), STARTED_ONBOARDING(1 shl 3);

    object SetSerializer: KSerializer<Set<GuildMemberFlags>> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("GuildMemberFlag", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): Set<GuildMemberFlags> {
            val intentFlag = decoder.decodeInt()

            val permissionSet = mutableSetOf<GuildMemberFlags>()
            GuildMemberFlags.entries.forEach {
                if (intentFlag and it.id == it.id) permissionSet.add(it)
            }

            return permissionSet
        }

        override fun serialize(encoder: Encoder, value: Set<GuildMemberFlags>) {
            var result = 0
            value.forEach { result = result or it.id }

            encoder.encodeInt(result)
        }
    }
}

/**
 * https://discord.com/developers/docs/resources/guild#guild-member-object
 */
@Serializable(with = GuildMember.Serializer::class)
class GuildMember(
    val user: DiscordUser? = null, val nick: String? = null, val avatar: String? = null,
    val roles: Array<String>, val joinedAt: Instant, val premiumSince: Instant? = null,
    val deaf: Boolean, val mute: Boolean, val flags: Set<GuildMemberFlags>, val pending: Boolean? = null,
    val permissions: Set<DiscordPermission>, val communicationDisabledUntil: Instant? = null
) {
    object Serializer: KSerializer<GuildMember> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("GuildMember") {
                element<DiscordUser>("user", isOptional = true)
                element<String?>("nick", isOptional = true)
                element<String?>("avatar", isOptional = true)
                element<Array<String>>("roles", isOptional = true)
                element<Instant>("joined_at")
                element<Instant?>("premium_since", isOptional = true)
                element<Boolean>("deaf")
                element<Boolean>("mute")
                element<Int>("flags")
                element<Boolean>("pending", isOptional = true)
                element<String>("permissions", isOptional = true)
                element<Instant?>("communication_disabled_until", isOptional = true)
            }

        @OptIn(ExperimentalSerializationApi::class)
        override fun deserialize(decoder: Decoder): GuildMember {
            var user: DiscordUser? = null

            var nick: String? = null
            var avatar: String? = null
            var roles: Array<String>? = null
            var joinedAt: Instant? = null
            var premiumSince: Instant? = null
            var deaf: Boolean? = null
            var mute: Boolean? = null
            var flags: Set<GuildMemberFlags>? = null
            var pending: Boolean? = null
            var permissions = setOf<DiscordPermission>()
            var communicationDisabledUntil: Instant? = null

            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> user = decodeSerializableElement(descriptor, index, DiscordUser.Serializer)
                    1 -> nick = decodeNullableString(descriptor, index)
                    2 -> avatar = decodeNullableString(descriptor, index)
                    3 -> roles = decodeSerializableElement(descriptor, index, String.serializer().arraySerializer())
                    4 -> joinedAt = decodeSerializableElement(descriptor, index, InstantIso8601Serializer)
                    5 -> premiumSince = decodeNullableSerializableElement(descriptor, index, InstantIso8601Serializer)
                    6 -> deaf = decodeBooleanElement(descriptor, index)
                    7 -> mute = decodeBooleanElement(descriptor, index)
                    8 -> flags = decodeSerializableElement(descriptor, index, GuildMemberFlags.SetSerializer)
                    9 -> pending = decodeBooleanElement(descriptor, index)
                    10 -> permissions = decodeSerializableElement(descriptor, index, DiscordPermission.SetSerializer)
                    11 -> communicationDisabledUntil = decodeNullableSerializableElement(descriptor, index, InstantIso8601Serializer)
                }
            }

            val result = GuildMember(
                user, nick, avatar, roles!!, joinedAt!!, premiumSince, deaf!!, mute!!, flags!!, pending, permissions, communicationDisabledUntil
            )

            return result
        }

        override fun serialize(encoder: Encoder, value: GuildMember) {
            encoder.beginStructure(descriptor).run {
                value.user?.let { encodeSerializableElement(descriptor, 0, DiscordUser.Serializer, value.user) }
                value.nick?.let { encodeStringElement(descriptor, 1, value.nick) }
                value.avatar?.let { encodeStringElement(descriptor, 2, value.avatar) }
                encodeSerializableElement(descriptor, 3, String.serializer().arraySerializer(), value.roles)
                encodeSerializableElement(descriptor, 4, InstantIso8601Serializer, value.joinedAt)
                value.premiumSince?.let { encodeSerializableElement(descriptor, 5, InstantIso8601Serializer, value.premiumSince) }
                encodeBooleanElement(descriptor, 6, value.deaf)
                encodeBooleanElement(descriptor, 7, value.mute)
                encodeSerializableElement(descriptor, 8, GuildMemberFlags.SetSerializer, value.flags)
                value.pending?.let { encodeBooleanElement(descriptor, 9, value.pending) }
                if (value.permissions.isNotEmpty()) encodeSerializableElement(descriptor, 10, DiscordPermission.SetSerializer, value.permissions)
                value.communicationDisabledUntil?.let { encodeSerializableElement(descriptor, 11, InstantIso8601Serializer, value.communicationDisabledUntil) }
            }
        }
    }
}

class DiscordGuild {
}