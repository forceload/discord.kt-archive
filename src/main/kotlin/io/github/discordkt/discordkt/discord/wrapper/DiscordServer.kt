package io.github.discordkt.discordkt.discord.wrapper
import io.github.discordkt.discordkt.discord.APIRequester
import kotlinx.serialization.json.*
import kotlinx.serialization.json.Json.Default.encodeToJsonElement

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
        val internalServer = Json.parseToJsonElement(
            APIRequester.getRequest("guilds/$id")["data"]!!.jsonPrimitive.content
        ).jsonObject

        name = internalServer["name"]!!.jsonPrimitive.content
        icon = convertNullableString(internalServer["icon"])
        splash = convertNullableString(internalServer["splash"])
        discoverySplash = convertNullableString(internalServer["discovery_splash"])
        owner = DiscordUser(internalServer["owner_id"]!!.jsonPrimitive.content)
        afkChannel = convertNullableChannel(internalServer["afk_channel_id"])
        afkTimeout = internalServer["afk_timeout"]!!.jsonPrimitive.int
        verificationLevel = internalServer["verification_level"]!!.jsonPrimitive.int
        defaultMessageNotifications = internalServer["default_message_notifications"]!!.jsonPrimitive.int
        explicitContentFilter = internalServer["explicit_content_filter"]!!.jsonPrimitive.int

        val internalRoleMap = HashMap<String, Int>()
        internalServer["roles"]!!.jsonArray.forEach {
            val internalRole = it.jsonObject
            roles.add(DiscordRole(
                internalRole["id"]!!.jsonPrimitive.content, internalRole["name"]!!.jsonPrimitive.content,
                internalRole["color"]!!.jsonPrimitive.int, internalRole["hoist"]!!.jsonPrimitive.boolean,
                icon = convertNullableString(internalRole["icon"]),
                unicodeEmoji = convertNullableString(internalRole["unicode_emoji"]),
                internalRole["position"]!!.jsonPrimitive.int,
                internalRole["permissions"]!!.jsonPrimitive.content,
                internalRole["managed"]!!.jsonPrimitive.boolean,
                internalRole["mentionable"]!!.jsonPrimitive.boolean,
                tags = getRoleTag(internalRole)
            ))

            internalRoleMap[internalRole["id"]!!.jsonPrimitive.content] = roles.size - 1
        }

        internalServer["emoji"]!!.jsonArray.forEach {
            val internalEmoji = it.jsonObject
            val allowedRoles = ArrayList<DiscordRole>()

            if (internalEmoji.containsKey("roles")) {
                internalEmoji["roles"]!!.jsonArray.forEach { roleID ->
                    val internalRoleID = roleID.jsonPrimitive.content
                    allowedRoles.add(roles[internalRoleMap[internalRoleID]!!])
                }
            }

            var emojiOwner: DiscordUser? = null
            if (internalEmoji.contains("user")) {
                emojiOwner = DiscordUser(
                    internalEmoji["user"]!!.jsonObject["id"]!!.jsonPrimitive.content,
                    internalEmoji["user"]!!.jsonObject["username"]!!.jsonPrimitive.content,
                    internalEmoji["user"]!!.jsonObject["discriminator"]!!.jsonPrimitive.content
                )
            }

            emojis.add(DiscordEmoji(
                id = convertNullableString(internalEmoji["id"]),
                name = convertNullableString(internalEmoji["name"]),
                roles = allowedRoles,
                user = emojiOwner,
                requireColons = convertObscureBoolean(internalEmoji, "require_colons"),
                managed = convertObscureBoolean(internalEmoji, "managed"),
                animated = convertObscureBoolean(internalEmoji, "animated"),
                available = convertObscureBoolean(internalEmoji, "available")
            ))
        }

        internalServer["features"]!!.jsonArray.forEach {
            features.add(it.jsonPrimitive.content)
        }

        mfaLevel = internalServer["mfa_level"]!!.jsonPrimitive.int
        applicationId = convertNullableString(internalServer["application_id"])
        systemChannel = convertNullableChannel(internalServer["system_channel_id"])
        systemChannelFlags = internalServer["system_channel_flags"]!!.jsonPrimitive.int.toByte()
        rulesChannel = convertNullableChannel(internalServer["rules_channel_id"])
        vanityUrlCode = convertNullableString(internalServer["vanity_url_code"])
        description = convertNullableString(internalServer["description"])
        banner = convertNullableString(internalServer["banner"])
        premiumTier = internalServer["premium_tier"]!!.jsonPrimitive.int
        preferredLocale = internalServer["preferred_locale"]!!.jsonPrimitive.content
        publicUpdatesChannel = convertNullableChannel(internalServer["public_updates_channel_id"])
        nsfwLevel = internalServer["nsfw_level"]!!.jsonPrimitive.int
        premiumProgressBarEnabled = internalServer["premium_progress_bar_enabled"]!!.jsonPrimitive.boolean
    }

    private fun getRoleTag(internalRole: JsonObject) =
        if (!internalRole.containsKey("tags")) null else DiscordRoleTag(
            convertNullableString(internalRole["tags"]!!.jsonObject["bot_id"]),
            convertNullableString(internalRole["tags"]!!.jsonObject["integration_id"])
        )

    private fun convertObscureBoolean(jsonObject: JsonObject, key: String) =
        if (!jsonObject.containsKey("key")) null else jsonObject[key]!!.jsonPrimitive.boolean

    private fun convertNullableChannel(channel: JsonElement?) =
        if (channel == null) null else DiscordChannel(channel.jsonPrimitive.contentOrNull!!)

    private fun convertNullableString(string: JsonElement?) = string?.jsonPrimitive?.contentOrNull
    private fun convertNullableInt(int: JsonElement) = int.jsonPrimitive.intOrNull
}
