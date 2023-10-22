package io.github.forceload.discordkt.type.application

import io.github.forceload.discordkt.type.DiscordPermission
import io.github.forceload.discordkt.type.DiscordUser
import io.github.forceload.discordkt.type.guilds.DiscordGuild
import io.github.forceload.discordkt.util.PrimitiveDescriptors
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
class Team(
    val icon: String?, val id: String,
    val members: Array<TeamMember>, val name: String,
    @SerialName("owner_user_id") val ownerUserID: String
)

@Serializable
class TeamMember(
    @SerialName("membership_state") val membershipState: MembershipState,
    @SerialName("team_id") val teamID: String, val user: DiscordUser, val role: String
)

@Serializable
enum class MembershipState(val id: Int) {
    INVITED(1), ACCEPTED(2);
    companion object { fun fromID(id: Int) = entries.first { it.id == id } }

    object Serializer: KSerializer<MembershipState> {
        override val descriptor: SerialDescriptor =
            PrimitiveDescriptors["MembershipState"].INT

        override fun deserialize(decoder: Decoder) = fromID(decoder.decodeInt())
        override fun serialize(encoder: Encoder, value: MembershipState) = encoder.encodeInt(value.id)
    }
}

@Suppress("unused")
enum class ApplicationFlags(val id: Int) {
    APPLICATION_AUTO_MODERATION_RULE_CREATE_BADGE(1 shl 6),
    GATEWAY_PRESENCE(1 shl 12), GATEWAY_PRESENCE_LIMITED(1 shl 13),
    GATEWAY_GUILD_MEMBERS(1 shl 14), GATEWAY_GUILD_MEMBERS_LIMITED(1 shl 15),
    VERIFICATION_PENDING_GUILD_LIMIT(1 shl 16), EMBEDDED(1 shl 17),
    GATEWAY_MESSAGE_CONTENT(1 shl 18), GATEWAY_MESSAGE_CONTENT_LIMITED(1 shl 19),
    APPLICATION_COMMAND_BADGE(1 shl 23);

    object SetSerializer: KSerializer<Set<ApplicationFlags>> {
        override val descriptor: SerialDescriptor = PrimitiveDescriptors["ApplicationFlags"].INT
        override fun deserialize(decoder: Decoder): Set<ApplicationFlags> {
            val intentFlag = decoder.decodeInt()

            val appFlagSet = mutableSetOf<ApplicationFlags>()
            ApplicationFlags.entries.forEach {
                if (intentFlag and it.id == it.id) appFlagSet.add(it)
            }

            return appFlagSet
        }

        override fun serialize(encoder: Encoder, value: Set<ApplicationFlags>) {
            var result = 0
            value.forEach { result = result or it.id }

            encoder.encodeInt(result)
        }
    }
}

@Serializable
class InstallParams(
    val scopes: Array<OAuth2Scope>,
    @Serializable(with = DiscordPermission.SetSerializer::class) val permissions: Set<DiscordPermission>
)

/**
 * https://discord.com/developers/docs/resources/application#application-object
 */
@Serializable
class DiscordApplication(
    val id: String, val name: String, val icon: String?, val description: String,
    @SerialName("rpc_origins") val rpcOrigins: Array<String> = arrayOf(), @SerialName("bot_public") val botPublic: Boolean,
    @SerialName("bot_require_code_grant") val botRequireCodeGrant: Boolean, val bot: DiscordUser? = null,
    @SerialName("terms_of_service_url") val termsOfServiceURL: String? = null, @SerialName("privacy_policy_url") val privacyPolicyURL: String? = null,
    val owner: DiscordUser? = null, @Deprecated("Deprecated and will be removed in v11.") val summary: String = "",
    @SerialName("verify_key") val verifyKey: String, val team: Team?,
    @SerialName("guild_id") val guildID: String? = null, val guild: DiscordGuild? = null,
    @SerialName("primary_sku_id") val primarySKUID: String? = null, val slug: String? = null,
    @SerialName("cover_image") val coverImage: String = "",
    @Serializable(with = ApplicationFlags.SetSerializer::class) val flags: Set<ApplicationFlags> = setOf(),
    @SerialName("approximate_guild_count") val approximateGuildCount: Int = -1,
    @SerialName("redirect_uris") val redirectURIs: Array<String> = arrayOf(),
    @SerialName("interactions_endpoint_url") val interactionsEndpointURL: String = "",
    @SerialName("role_connections_verification_url") val roleConnectionsVerificationURL: String = "",
    @SerialName("tags") val tags: Array<String> = arrayOf(),
    @SerialName("install_params") val installParams: InstallParams? = null,
    @SerialName("custom_install_url") val customInstallURL: String = ""
)