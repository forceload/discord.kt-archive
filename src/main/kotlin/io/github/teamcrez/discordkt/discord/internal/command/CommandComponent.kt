package io.github.teamcrez.discordkt.discord.internal.command

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.github.teamcrez.discordkt.discord.APIRequester
import io.github.teamcrez.discordkt.discord.internal.Commands
import io.github.teamcrez.discordkt.discord.internal.DiscordBot
import io.github.teamcrez.discordkt.discord.wrapper.CommandArgument

class CommandComponent(
    private val command: Commands, bot: DiscordBot,
    private val commandName: String,
    private val description: String = "Default Description",
    private val args: Map<String, CommandArgument>
) {
    private var isFirst: Boolean

    init {
        val generatedOptions = JsonArray()

        args.forEach { (optionName, optionType) ->
            val currentObject = JsonObject()
            currentObject.addProperty("type", optionType.getIntType())
            currentObject.addProperty("name", optionName)
            currentObject.addProperty("description", optionType.description)
            currentObject.addProperty("required", optionType.required)

            if (!optionType.choices.isEmpty()) {
                val choiceArray = JsonArray()
                optionType.choices.forEach { (choiceName, choiceValue) ->
                    val choiceObject = JsonObject()
                    choiceObject.addProperty("name", choiceName)
                    choiceObject.addProperty("value", choiceValue.toString())

                    choiceArray.add(choiceObject)
                }

                currentObject.add("choices", choiceArray)
            }

            generatedOptions.add(currentObject)
        }

        val commandJsonData = mapOf(
            "type" to 1,
            "description" to description,
            "name" to commandName,
            "options" to generatedOptions,
        )

        isFirst = true
        JsonParser.parseString(
            JsonParser.parseString(bot.commands["data"].toString()).asString
        ).asJsonArray.forEach { jsonCommand ->
            var sameData = 0
            var comparisonSize = 0
            commandJsonData.forEach {
                if (jsonCommand.asJsonObject[it.key] != null) {
                    if (!jsonCommand.asJsonObject[it.key].isJsonArray) {
                        if (jsonCommand.asJsonObject[it.key].asString == it.value.toString()) {
                            if (it.key == "name")
                                sameData += commandJsonData.size
                            sameData++
                        }
                    } else {
                        if (jsonCommand.asJsonObject[it.key].toString() == it.value.toString()) {
                            sameData++
                        }
                    }

                    comparisonSize++
                }
            }

            if (sameData == comparisonSize * 2) {
                isFirst = false
                return@forEach
            }
        }

        if (isFirst) {
            APIRequester.postRequest("applications/${bot.id}/commands", commandJsonData)
        }
    }
}
