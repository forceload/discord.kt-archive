package io.github.teamcrez.discordkt.discord.internal

import com.google.gson.JsonParser
import io.github.teamcrez.discordkt.discord.APIRequester
import io.github.teamcrez.discordkt.discord.DiscordClient
import kotlinx.serialization.json.JsonObject

class DiscordBot(val client: DiscordClient) {
    var id: String?
        get() = internalID
        set(id) = run { this.internalID = id; generateHeader() }
    var token: String?
        get() = internalToken
        set(token) = run { this.internalToken = token; generateHeader() }
    var intentFlag = 0

    private var internalID: String? = null
    private var internalToken: String? = null

    @Suppress("MemberVisibilityCanBePrivate")
    var authHeader: MutableMap<String, String>? = null

    private fun generateHeader() {
        if (this.internalID != null && this.internalToken != null) {
            this.authHeader = mutableMapOf("Authorization" to "Bot $token")
        }
    }

    lateinit var commands: JsonObject

    lateinit var commandObject: Commands

    fun commands(init: Commands.() -> Unit) {
        commands = APIRequester.getRequest("applications/$id/commands")

        commandObject = Commands(this)
        commandObject.init()

        JsonParser.parseString(
            JsonParser.parseString(commands["data"].toString()).asString
        ).asJsonArray.forEach {
            val apiCommand = it.asJsonObject["name"].asJsonPrimitive.asString
            if (!commandObject.commandNames.contains(apiCommand)) {
                APIRequester.deleteRequest(
                    "applications/$id/commands/${it.asJsonObject["id"].asJsonPrimitive.asString}"
                )
            }
        }
    }
}
