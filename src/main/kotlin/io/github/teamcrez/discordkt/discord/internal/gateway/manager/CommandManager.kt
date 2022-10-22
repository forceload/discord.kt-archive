package io.github.teamcrez.discordkt.discord.internal.gateway.manager

import io.github.teamcrez.discordkt.discord.api.DiscordFlags
import io.github.teamcrez.discordkt.discord.api.DiscordFlags.CommandArgumentType.*
import io.github.teamcrez.discordkt.discord.collections.DiscordArgumentMap
import io.github.teamcrez.discordkt.discord.internal.command.CommandStorage
import io.github.teamcrez.discordkt.discord.internal.command.context.CommandData
import io.github.teamcrez.discordkt.discord.internal.gateway.event.GatewayEvent
import io.github.teamcrez.discordkt.discord.types.DiscordNull
import io.github.teamcrez.discordkt.discord.types.DiscordString
import io.github.teamcrez.discordkt.discord.wrapper.generator.CommandContextGenerator
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
                        when (DiscordFlags.matchType(it.jsonObject["type"]!!.jsonPrimitive.int)) {
                            STRING -> {
                                argumentMap[it.jsonObject["name"]!!.jsonPrimitive.content] =
                                    DiscordString(it.jsonObject["value"]!!.jsonPrimitive.content)
                            }

                            NULL -> {
                                argumentMap[it.jsonObject["name"]!!.jsonPrimitive.content] = DiscordNull()
                            }

                            SUB_COMMAND -> TODO()
                            SUB_COMMAND_GROUP -> TODO()
                            INTEGER -> TODO()
                            BOOLEAN -> TODO()
                            USER -> TODO()
                            CHANNEL -> TODO()
                            ROLE -> TODO()
                            MENTIONABLE -> TODO()
                            NUMBER -> TODO()
                            ATTACHMENT -> TODO()
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
