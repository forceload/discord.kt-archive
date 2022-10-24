package io.github.teamcrez.discordkt.discord.wrapper
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.github.teamcrez.discordkt.discord.APIRequester
import kotlinx.serialization.json.jsonPrimitive

@Suppress("MemberVisibilityCanBePrivate")
data class DiscordServer(val id: String) {
    val name: String
    val icon: String?
    val splash: String?
    val discoverySplash: String?
    val owner: DiscordUser
    val afkChannel: DiscordChannel?
    val afkTimeout: Int
    val verificationLevel: Int
    val defaultMessageNotifications: Int
    val explicitContentFilter: Int
    val roles = ArrayList<DiscordRole>()
    val emojis = ArrayList<DiscordEmoji>()
    val features = ArrayList<String>()
    val mfaLevel: Int
    val applicationId: String?
    val systemChannel: DiscordChannel?
    val systemChannelFlags: Byte
    val rulesChannel: DiscordChannel?
    val vanityUrlCode: String?
    val description: String?
    val banner: String?
    val premiumTier: Int
    val preferredLocale: String
    val publicUpdatesChannel: DiscordChannel?
    val nsfwLevel: Int
    val premiumProgressBarEnabled: Boolean

    init {
        val internalServer = JsonParser.parseString(
            APIRequester.getRequest("guilds/$id")["data"]!!.jsonPrimitive.content
        ).asJsonObject

        name = internalServer["name"].asString
        icon = convertNullableString(internalServer["icon"])
        splash = convertNullableString(internalServer["splash"])
        discoverySplash = convertNullableString(internalServer["discovery_splash"])
        owner = DiscordUser(internalServer["owner_id"].asString)
        afkChannel = convertNullableChannel(internalServer["afk_channel_id"])
        afkTimeout = internalServer["afk_timeout"].asInt
        verificationLevel = internalServer["verification_level"].asInt
        defaultMessageNotifications = internalServer["default_message_notifications"].asInt
        explicitContentFilter = internalServer["explicit_content_filter"].asInt

        val internalRoleMap = HashMap<String, Int>()
        internalServer["roles"].asJsonArray.forEach {
            val internalRole = it.asJsonObject
            roles.add(DiscordRole(
                internalRole["id"].asString, internalRole["name"].asString,
                internalRole["color"].asInt, internalRole["hoist"].asBoolean,
                icon = convertNullableString(internalRole["icon"]),
                unicode_emoji = convertNullableString(internalRole["unicode_emoji"]),
                internalRole["position"].asInt,
                internalRole["permissions"].asString,
                internalRole["managed"].asBoolean,
                internalRole["mentionable"].asBoolean,
                tags = getRoleTag(internalRole)
            ))

            internalRoleMap[internalRole["id"].asString] = roles.size - 1
        }

        internalServer["emoji"].asJsonArray.forEach {
            val internalEmoji = it.asJsonObject
            val allowedRoles = ArrayList<DiscordRole>()

            if (internalEmoji.has("roles")) {
                internalEmoji["roles"].asJsonArray.forEach { roleID ->
                    val internalRoleID = roleID.asString
                    allowedRoles.add(roles[internalRoleMap[internalRoleID]!!])
                }
            }

            var emojiOwner: DiscordUser? = null
            if (internalEmoji.has("user")) {
                emojiOwner = DiscordUser(
                    internalEmoji["user"].asJsonObject["id"].asString,
                    internalEmoji["user"].asJsonObject["username"].asString,
                    internalEmoji["user"].asJsonObject["discriminator"].asString
                )
            }

            emojis.add(DiscordEmoji(
                id = convertNullableString(internalEmoji["id"]),
                name = convertNullableString(internalEmoji["name"]),
                roles = allowedRoles,
                user = emojiOwner,
                require_colons = convertObscureBoolean(internalEmoji, "require_colons"),
                managed = convertObscureBoolean(internalEmoji, "managed"),
                animated = convertObscureBoolean(internalEmoji, "animated"),
                available = convertObscureBoolean(internalEmoji, "available")
            ))
        }

        internalServer["features"].asJsonArray.forEach {
            features.add(it.asString)
        }

        mfaLevel = internalServer["mfa_level"].asInt
        applicationId = convertNullableString(internalServer["application_id"])
        systemChannel = convertNullableChannel(internalServer["system_channel_id"])
        systemChannelFlags = internalServer["system_channel_flags"].asInt.toByte()
        rulesChannel = convertNullableChannel(internalServer["rules_channel_id"])
        vanityUrlCode = convertNullableString(internalServer["vanity_url_code"])
        description = convertNullableString(internalServer["description"])
        banner = convertNullableString(internalServer["banner"])
        premiumTier = internalServer["premium_tier"].asInt
        preferredLocale = internalServer["preferred_locale"].asString
        publicUpdatesChannel = convertNullableChannel(internalServer["public_updates_channel_id"])
        nsfwLevel = internalServer["nsfw_level"].asInt
        premiumProgressBarEnabled = internalServer["premium_progress_bar_enabled"].asBoolean
    }

    private fun getRoleTag(internalRole: JsonObject) =
        if (!internalRole.has("tags")) null else DiscordRoleTag(
            convertNullableString(internalRole["tags"]!!.asJsonObject["bot_id"]),
            convertNullableString(internalRole["tags"]!!.asJsonObject["integration_id"])
        )

    private fun convertObscureBoolean(jsonObject: JsonObject, key: String) =
        if (!jsonObject.has(key)) null else jsonObject[key].asBoolean

    private fun convertNullableChannel(channel: JsonElement) =
        if (!channel.isJsonNull) null else DiscordChannel(channel.asString)

    private fun convertNullableString(string: JsonElement) =
        if (!string.isJsonNull) null else string.asString

    private fun convertNullableInt(int: JsonElement) =
        if (!int.isJsonNull) null else int.asInt
}
