package io.github.discordkt.discordkt.discord.internal.command

import io.github.discordkt.discordkt.discord.APIRequester
import io.github.discordkt.discordkt.discord.internal.Commands
import io.github.discordkt.discordkt.discord.internal.DiscordBot
import io.github.discordkt.discordkt.discord.wrapper.CommandArgument
import io.github.discordkt.discordkt.serializer.AnySerializer
import kotlinx.serialization.json.*

class CommandComponent(
    private val command: Commands, bot: DiscordBot,
    private val commandName: String,
    private val description: String = "Default Description",
    private val args: Map<String, CommandArgument>
) {
    private var isFirst: Boolean
    val isDiffrent: Boolean

    init {
        val generatedOptions = ArrayList<JsonObject>()

        args.forEach { (optionName, optionType) ->
            val currentMap = mutableMapOf<String, Any>(
                "type" to optionType.getIntType(), "name" to optionName,
                "description" to optionType.description, "required" to optionType.required
            )

            if (!optionType.choices.isEmpty()) {
                val choiceArray = ArrayList<JsonElement>()
                optionType.choices.forEach { (choiceName, choiceValue) ->
                    val choiceObject = Json.encodeToJsonElement(
                        mapOf<String, Any>(
                            "name" to choiceName.toString(),
                            "value" to if (choiceValue is Number) { choiceValue } else { choiceValue.toString() }
                        )
                    )

                    choiceArray.add(choiceObject)
                }

                currentMap["choices"] = choiceArray
            }

            val currentObject = Json.encodeToJsonElement(AnySerializer, currentMap).jsonObject
            generatedOptions.add(currentObject)
        }

        val commandJsonData = mapOf(
            "type" to 1,
            "description" to description,
            "name" to commandName,
            "options" to generatedOptions,
        )

        isFirst = true
        var nameExists = false
        Json.parseToJsonElement(bot.commands["data"]!!.jsonPrimitive.content).jsonArray.forEach { jsonCommand ->
            var sameData = 0
            var comparisonSize = 0
            val command = jsonCommand.jsonObject
            commandJsonData.forEach {
                if (command[it.key] != null) {
                    if (command[it.key]!! as? JsonArray == null) {
                        if (command[it.key]!!.jsonPrimitive.content == it.value.toString()) {
                            if (it.key == "name") { sameData += commandJsonData.size; nameExists = true }
                            sameData++
                        }
                    } else {
                        if (jsonCommand.jsonObject[it.key].hashCode() == it.value.hashCode()) {
                            sameData++
                        }
                    }

                    comparisonSize++
                }
            }

            if (sameData == comparisonSize * 2) {
                isFirst = false
                print(sameData)
                return@forEach
            }
        }

        if (isFirst) {
            isDiffrent = false
            println(commandJsonData)
            APIRequester.postRequest("applications/${bot.id}/commands", commandJsonData)
        } else {
            isDiffrent = nameExists
        }
    }
}
