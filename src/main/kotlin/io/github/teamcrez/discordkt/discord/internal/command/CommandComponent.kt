package io.github.teamcrez.discordkt.discord.internal.command

import com.google.gson.JsonParser
import io.github.teamcrez.discordkt.discord.APIRequester
import io.github.teamcrez.discordkt.discord.internal.Commands
import io.github.teamcrez.discordkt.discord.internal.DiscordBot
import io.github.teamcrez.discordkt.discord.internal.command.context.CommandData

class CommandComponent(
    private val command: Commands, bot: DiscordBot,
    private val commandName: String,
    private val description: String,
    private val args: Map<String, CommandData>
) {
    private var isFirst: Boolean

    init {
        val commandJsonData = mapOf(
            "type" to 1,
            "description" to description,
            "name" to commandName
        )

        isFirst = true
        JsonParser.parseString(
            JsonParser.parseString(bot.commands["data"].toString()).asString
        ).asJsonArray.forEach { jsonCommand ->
            var sameData = 0
            commandJsonData.forEach {
                if (jsonCommand.asJsonObject[it.key].asString == it.value.toString()) {
                    if (it.key == "name")
                        sameData += commandJsonData.size
                    sameData++
                }
            }

            if (sameData == commandJsonData.size * 2) {
                isFirst = false
                return@forEach
            }
        }

        if (isFirst) {
            APIRequester.postRequest("applications/${bot.id}/commands", commandJsonData)
        }
    }
}
