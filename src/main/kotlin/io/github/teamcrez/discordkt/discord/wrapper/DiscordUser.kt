package io.github.teamcrez.discordkt.discord.wrapper

import com.google.gson.JsonParser
import io.github.teamcrez.discordkt.discord.APIRequester
import io.github.teamcrez.discordkt.discord.api.DiscordFlags
import io.github.teamcrez.discordkt.discord.types.DiscordString
import io.github.teamcrez.discordkt.discord.types.DiscordType
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Suppress("MemberVisibilityCanBePrivate")
class DiscordUser(var id: String) {
    private var isDMOpened = false
    private lateinit var dmChannel: DiscordChannel

    init {
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
