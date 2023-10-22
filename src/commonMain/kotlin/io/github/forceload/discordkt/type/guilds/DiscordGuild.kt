package io.github.forceload.discordkt.type.guilds

import io.github.forceload.discordkt.type.DiscordLocale
import io.github.forceload.discordkt.type.DiscordPermission
import io.github.forceload.discordkt.type.DiscordRole
import io.github.forceload.discordkt.type.DiscordUser
import io.github.forceload.discordkt.type.channel.DiscordEmoji
import io.github.forceload.discordkt.util.PrimitiveDescriptors
import io.github.forceload.discordkt.util.SerializerExtension.arraySerializer
import io.github.forceload.discordkt.util.SerializerExtension.decodeNullableString
import io.github.forceload.discordkt.util.SerializerUtil.makeStructure
import kotlinx.datetime.Instant
import kotlinx.datetime.serializers.InstantIso8601Serializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
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
data class GuildMember(
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
                    11 -> communicationDisabledUntil =
                        decodeNullableSerializableElement(descriptor, index, InstantIso8601Serializer)
                }
            }

            return GuildMember(
                user, nick, avatar, roles!!,
                joinedAt!!, premiumSince,
                deaf!!, mute!!, flags!!, pending,
                permissions, communicationDisabledUntil
            )
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GuildMember) return false

        if (user != other.user) return false
        if (nick != other.nick) return false
        if (avatar != other.avatar) return false
        if (!roles.contentEquals(other.roles)) return false
        if (joinedAt != other.joinedAt) return false
        if (premiumSince != other.premiumSince) return false
        if (deaf != other.deaf) return false
        if (mute != other.mute) return false
        if (flags != other.flags) return false
        if (pending != other.pending) return false
        if (permissions != other.permissions) return false
        return communicationDisabledUntil == other.communicationDisabledUntil
    }

    override fun hashCode(): Int {
        var result = user?.hashCode() ?: 0
        result = 31 * result + (nick?.hashCode() ?: 0)
        result = 31 * result + (avatar?.hashCode() ?: 0)
        result = 31 * result + roles.contentHashCode()
        result = 31 * result + joinedAt.hashCode()
        result = 31 * result + (premiumSince?.hashCode() ?: 0)
        result = 31 * result + deaf.hashCode()
        result = 31 * result + mute.hashCode()
        result = 31 * result + flags.hashCode()
        result = 31 * result + (pending?.hashCode() ?: 0)
        result = 31 * result + permissions.hashCode()
        result = 31 * result + (communicationDisabledUntil?.hashCode() ?: 0)
        return result
    }
}

@Serializable(with = VerificationLevel.Serializer::class)
enum class VerificationLevel(val level: Int) {
    NONE(0), // unrestricted
    LOW(1), // must have verified email on account
    MEDIUM(2), // must be registered on Discord for longer than 5 minutes
    HIGH(3), // must be a member of the server for longer than 10 minutes
    VERY_HIGH(4); // must have a verified phone number

    companion object { fun fromID(id: Int) = entries.first { it.level == id } }
    object Serializer: KSerializer<VerificationLevel> {
        override val descriptor: SerialDescriptor =
            PrimitiveDescriptors["VerificationLevel"].INT

        override fun deserialize(decoder: Decoder) = fromID(decoder.decodeInt())

        override fun serialize(encoder: Encoder, value: VerificationLevel) =
            encoder.encodeInt(value.level)
    }
}

@Serializable(with = MessageNotificationLevel.Serializer::class)
enum class MessageNotificationLevel(val value: Int) {
    ALL_MESSAGES(0), ONLY_MENTIONS(1);
    companion object { fun fromID(id: Int) = entries.first { it.value == id } }
    object Serializer: KSerializer<MessageNotificationLevel> {
        override val descriptor: SerialDescriptor =
            PrimitiveDescriptors["MessageNotificationLevel"].INT

        override fun deserialize(decoder: Decoder) = fromID(decoder.decodeInt())
        override fun serialize(encoder: Encoder, value: MessageNotificationLevel) =
            encoder.encodeInt(value.value)
    }
}

@Serializable(with = ExplicitContentFilterLevel.Serializer::class)
enum class ExplicitContentFilterLevel(val value: Int) {
    DISABLED(0), MEMBERS_WITHOUT_ROLES(1), ALL_MEMBERS(2);
    companion object { fun fromID(id: Int) = entries.first { it.value == id } }
    object Serializer: KSerializer<ExplicitContentFilterLevel> {
        override val descriptor: SerialDescriptor =
            PrimitiveDescriptors["ExplicitContentFilterLevel"].INT

        override fun deserialize(decoder: Decoder) = fromID(decoder.decodeInt())
        override fun serialize(encoder: Encoder, value: ExplicitContentFilterLevel) =
            encoder.encodeInt(value.value)
    }
}

@Serializable(with = GuildFeature.Serializer::class)
enum class GuildFeature {
    ANIMATED_BANNER, ANIMATED_ICON,
    APPLICATION_COMMAND_PERMISSIONS_V2,
    AUTO_MODERATION, BANNER, COMMUNITY,
    CREATOR_MONETIZABLE_PROVISIONAL, CREATOR_STORE_PAGE,
    DISCOVERABLE, FEATURABLE, INVITES_DISABLED, INVITE_SPLASH,
    MEMBER_VERIFICATION_GATE_ENABLED, MORE_STICKERS, NEWS, PARTNERED,
    PREVIEW_ENABLED, RAID_ALERTS_DISABLED, ROLE_ICONS,
    ROLE_SUBSCRIPTIONS_AVAILABLE_FOR_PURCHASE, ROLE_SUBSCRIPTIONS_ENABLED,
    TICKETED_EVENTS_ENABLED, VANITY_URL, VERIFIED, VIP_REGIONS, WELCOME_SCREEN_ENABLED;

    companion object { fun fromID(id: String) = entries.first { it.name == id } }
    object Serializer: KSerializer<GuildFeature> {
        override val descriptor: SerialDescriptor =
            PrimitiveDescriptors["GuildFeature"].STRING

        override fun deserialize(decoder: Decoder) = fromID(decoder.decodeString())
        override fun serialize(encoder: Encoder, value: GuildFeature) = encoder.encodeString(value.name)
    }
}

@Serializable(with = MFALevel.Serializer::class)
enum class MFALevel(val id: Int) {
    NONE(0), ELEVATED(1);

    companion object { fun fromID(id: Int) = entries.first { it.id == id } }
    object Serializer: KSerializer<MFALevel> {
        override val descriptor: SerialDescriptor = PrimitiveDescriptors["MFALevel"].INT
        override fun deserialize(decoder: Decoder) = fromID(decoder.decodeInt())
        override fun serialize(encoder: Encoder, value: MFALevel) = encoder.encodeInt(value.id)
    }
}


enum class SystemChannelFlags(val id: Int) {
    SUPPRESS_JOIN_NOTIFICATIONS(1 shl 0),
    SUPPRESS_PREMIUM_SUBSCRIPTIONS(1 shl 1),
    SUPPRESS_GUILD_REMINDER_NOTIFICATIONS(1 shl 2),
    SUPPRESS_JOIN_NOTIFICATION_REPLIES(1 shl 3),
    SUPPRESS_ROLE_SUBSCRIPTION_PURCHASE_NOTIFICATIONS(1 shl 4),
    SUPPRESS_ROLE_SUBSCRIPTION_PURCHASE_NOTIFICATION_REPLIES(1 shl 5);

    object SetSerializer: KSerializer<Set<SystemChannelFlags>> {
        override val descriptor: SerialDescriptor = PrimitiveDescriptors["SystemChannelFlags"].INT
        override fun deserialize(decoder: Decoder): Set<SystemChannelFlags> {
            val flag = decoder.decodeInt()

            val systemChannelFlagSet = mutableSetOf<SystemChannelFlags>()
            SystemChannelFlags.entries.forEach {
                if (flag and it.id == it.id) systemChannelFlagSet.add(it)
            }

            return systemChannelFlagSet
        }

        override fun serialize(encoder: Encoder, value: Set<SystemChannelFlags>) {
            var result = 0
            value.forEach { result = result or it.id }

            encoder.encodeInt(result)
        }
    }
}

@Serializable(with = PremiumTier.Serializer::class)
enum class PremiumTier(val id: Int) {
    NONE(0), TIER_1(1), TIER_2(2), TIER_3(3);

    companion object { fun fromID(id: Int) = entries.first { it.id == id } }
    object Serializer: KSerializer<PremiumTier> {
        override val descriptor: SerialDescriptor = PrimitiveDescriptors["PremiumTier"].INT
        override fun deserialize(decoder: Decoder) = fromID(decoder.decodeInt())
        override fun serialize(encoder: Encoder, value: PremiumTier) = encoder.encodeInt(value.id)
    }
}

@Serializable(with = GuildNSFWLevel.Serializer::class)
enum class GuildNSFWLevel(val id: Int) {
    DEFAULT(0), EXPLICIT(1), SAFE(2), AGE_RESTRICTED(3);

    companion object { fun fromID(id: Int) = entries.first { it.id == id } }
    object Serializer: KSerializer<GuildNSFWLevel> {
        override val descriptor: SerialDescriptor = PrimitiveDescriptors["GuildNSFWLevel"].INT
        override fun deserialize(decoder: Decoder) = fromID(decoder.decodeInt())
        override fun serialize(encoder: Encoder, value: GuildNSFWLevel) = encoder.encodeInt(value.id)
    }
}

/**
 * https://discord.com/developers/docs/resources/guild#guild-object-guild-structure
 */

@Serializable
class DiscordGuild(
    val id: String, val name: String, val icon: String?, @SerialName("icon_hash") val iconHash: String? = null,
    val splash: String?, @SerialName("discovery_splash") val discoverySplash: String?,
    val owner: Boolean = false, @SerialName("owner_id") val ownerID: String,
    @Serializable(with = DiscordPermission.SetSerializer::class) val permissions: Set<DiscordPermission>,
    @Deprecated("Deprecated") val region: String? = null,

    @SerialName("afk_channel_id") val afkChannelID: String?, @SerialName("afk_timeout") val afkTimeout: Int,
    @SerialName("widget_enabled") val widgetEnabled: Boolean, @SerialName("widget_channel_id") val widgetChannelID: String?,
    @SerialName("verification_level") val verificationLevel: VerificationLevel,
    @SerialName("default_message_notifications") val defaultMessageNotifications: MessageNotificationLevel,
    @SerialName("explicit_content_filter") val explicitContentFilter: ExplicitContentFilterLevel,
    val roles: Array<DiscordRole>, val emojis: Array<DiscordEmoji>, val features: Array<GuildFeature>,
    @SerialName("mfa_level") val mfaLevel: MFALevel, @SerialName("application_id") val applicationID: String?,

    @SerialName("system_channel_id") val systemChannelID: String?,
    @Serializable(with = SystemChannelFlags.SetSerializer::class) @SerialName("system_channel_flags") val systemChannelFlags: Set<SystemChannelFlags>,

    @SerialName("rules_channel_id") val rulesChannelID: String?,
    @SerialName("max_presences") val maxPresences: Int? = null, @SerialName("max_members") val maxMembers: Int? = null,
    @SerialName("vanity_url_code") val vanityURLCode: String?, val description: String?, val banner: String?,
    @SerialName("premium_tier") val premiumTier: PremiumTier, @SerialName("premium_subscription_count") val premiumSubscriptionCount: Int = 0,
    @SerialName("preferred_locale") val preferredLocale: DiscordLocale, @SerialName("public_updates_channel_id") val publicUpdatesChannelID: String?,

    @SerialName("max_video_channel_users") val maxVideoChannelUsers: Int = Int.MAX_VALUE,
    @SerialName("max_stage_video_channel_users") val maxStageVideoChannelUsers: Int = Int.MAX_VALUE,
    @SerialName("approximate_member_count") val approximateMemberCount: Int = -1,
    @SerialName("approximate_presence_count") val approximatePresenceCount: Int = -1,
    @SerialName("welcome_screen") val welcomeScreen: WelcomeScreen,
    @SerialName("nsfw_level") val nsfwLevel: GuildNSFWLevel,
    val stickers: Array<DiscordSticker> = arrayOf(),

    @SerialName("premium_progress_bar_enabled") val premiumProgressBarEnabled: Boolean,
    @SerialName("safety_alerts_channel_id") val safetyAlertsChannelID: String?
)