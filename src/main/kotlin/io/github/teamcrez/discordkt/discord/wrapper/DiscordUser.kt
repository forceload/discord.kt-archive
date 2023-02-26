package io.github.teamcrez.discordkt.discord.wrapper

import com.google.gson.JsonParser
import io.github.teamcrez.discordkt.discord.APIRequester
import io.github.teamcrez.discordkt.discord.api.DiscordFlags
import io.github.teamcrez.discordkt.discord.types.DiscordString
import io.github.teamcrez.discordkt.discord.types.DiscordType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Suppress("MemberVisibilityCanBePrivate")
class DiscordUser {
    private var isDMOpened = false
    private lateinit var dmChannel: DiscordChannel

    val id: String
    val name: String
    val discriminator: String

    // Constructor with API call overhead (for general use)
    constructor(id: String) {
        this.id = id
        val internalUser = APIRequester.getRequest("users/$id")
        val internalUserData = Json.parseToJsonElement(internalUser["data"]!!.jsonPrimitive.content).jsonObject

        name = internalUserData["username"]!!.jsonPrimitive.content
        discriminator = internalUserData["discriminator"]!!.jsonPrimitive.content

        if (WrapperStorage.userChannel.keys.contains(id)) {
            dmChannel = WrapperStorage.userChannel[id]!!
            isDMOpened = true
        }
    }

    // Constructor without API call overhead (for internal use)
    constructor(id: String, name: String, discriminator: String) {
        this.id = id
        this.name = name
        this.discriminator = discriminator

        if (WrapperStorage.userChannel.keys.contains(id)) {
            dmChannel = WrapperStorage.userChannel[id]!!
            isDMOpened = true
        }
    }

    fun directMessage(message: String) {
        if (!isDMOpened) {
            val dmChannelData = mapOf("recipient_id" to id)
            dmChannel = DiscordChannel(
                JsonParser.parseString(
                    APIRequester.postRequest(
                        "users/@me/channels", dmChannelData
                    ).jsonObject["data"]!!.jsonPrimitive.content
                ).asJsonObject["id"].asJsonPrimitive.asString
            )

            WrapperStorage.userChannel[id] = dmChannel
            isDMOpened = true
        }

        dmChannel.sendMessage(message)
    }

    fun directMessage(message: DiscordType<*>) {
        if (message.type == DiscordFlags.CommandArgumentType.STRING) {
            message as DiscordString
            directMessage(message.coveredValue)
        }
    }
}
