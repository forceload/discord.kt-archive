package io.github.discordkt.discordkt.discord.internal.gateway.manager

import io.github.discordkt.discordkt.discord.api.DiscordFlags
import io.github.discordkt.discordkt.discord.collections.DiscordArgumentMap
import io.github.discordkt.discordkt.discord.internal.command.CommandStorage
import io.github.discordkt.discordkt.discord.internal.command.context.CommandData
import io.github.discordkt.discordkt.discord.internal.gateway.event.GatewayEvent
import io.github.discordkt.discordkt.discord.types.*
import io.github.discordkt.discordkt.discord.wrapper.DiscordUser
import io.github.discordkt.discordkt.discord.wrapper.generator.CommandContextGenerator
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object CommandManager {
    fun processCommand(event: GatewayEvent) {
        val commandName = event.d?.get("data")!!.jsonObject["name"]!!.jsonPrimitive.content

        if (CommandStorage.commandProcesses.keys.contains(commandName)) {
            val commandComponent = CommandStorage.commandProcesses[commandName]!!

            commandComponent[commandComponent.keys.first()]?.let {
                val argumentMap = DiscordArgumentMap<String>()
                if (event.d["data"]!!.jsonObject["options"] != null) {
                    val options = event.d["data"]!!.jsonObject["options"]
                    options?.jsonArray?.forEach {
                        argumentMap[it.jsonObject["name"]!!.jsonPrimitive.content] = when (DiscordFlags.matchArgumentType(it.jsonObject["type"]!!.jsonPrimitive.int)) {
                            DiscordFlags.CommandArgumentType.STRING ->
                                DiscordString(it.jsonObject["value"]!!.jsonPrimitive.content)

                            DiscordFlags.CommandArgumentType.NULL -> DiscordNull()
                            DiscordFlags.CommandArgumentType.SUB_COMMAND -> TODO()
                            DiscordFlags.CommandArgumentType.SUB_COMMAND_GROUP -> TODO()
                            DiscordFlags.CommandArgumentType.INTEGER ->
                                DiscordInteger(it.jsonObject["value"]!!.jsonPrimitive.content.toLong())
                            DiscordFlags.CommandArgumentType.BOOLEAN ->
                                DiscordBoolean(it.jsonObject["value"]!!.jsonPrimitive.content.toBoolean())
                            DiscordFlags.CommandArgumentType.USER -> {
                                val userJson = event.d["data"]!!.jsonObject["resolved"]!!.jsonObject["users"]!!.jsonObject
                                val userData = userJson[userJson.keys.first()]!!.jsonObject
                                DiscordUserType(DiscordUser(
                                    userData["id"]!!.jsonPrimitive.content,
                                    userData["username"]!!.jsonPrimitive.content,
                                    userData["discriminator"]!!.jsonPrimitive.content
                                ))
                            }
                            DiscordFlags.CommandArgumentType.CHANNEL -> TODO()
                            DiscordFlags.CommandArgumentType.ROLE -> TODO()
                            DiscordFlags.CommandArgumentType.MENTIONABLE -> TODO()
                            DiscordFlags.CommandArgumentType.NUMBER ->
                                DiscordNumber(it.jsonObject["value"]!!.jsonPrimitive.content.toDouble())
                            DiscordFlags.CommandArgumentType.ATTACHMENT -> TODO()
                        }
                    }
                }

                it(
                    CommandData(CommandContextGenerator.fromEvent(event), args = argumentMap)
                )
            }
        }
    }
}
