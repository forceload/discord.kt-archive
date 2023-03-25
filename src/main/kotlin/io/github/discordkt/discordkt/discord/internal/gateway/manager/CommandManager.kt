package io.github.discordkt.discordkt.discord.internal.gateway.manager

import io.github.discordkt.discordkt.discord.api.DiscordFlags
import io.github.discordkt.discordkt.discord.collections.DiscordArgumentMap
import io.github.discordkt.discordkt.discord.internal.command.CommandStorage
import io.github.discordkt.discordkt.discord.internal.command.context.CommandData
import io.github.discordkt.discordkt.discord.internal.gateway.event.GatewayEvent
import io.github.discordkt.discordkt.discord.types.*
import io.github.discordkt.discordkt.discord.wrapper.DiscordAttachment
import io.github.discordkt.discordkt.discord.wrapper.DiscordUser
import io.github.discordkt.discordkt.discord.wrapper.generator.CommandContextGenerator
import kotlinx.serialization.json.*

object CommandManager {
    fun processCommand(event: GatewayEvent) {
        val commandName = event.d?.get("data")!!.jsonObject["name"]!!.jsonPrimitive.content

        if (CommandStorage.commandProcesses.keys.contains(commandName)) {
            val commandComponent = CommandStorage.commandProcesses[commandName]!!

            commandComponent[commandComponent.keys.first()]?.let {
                val argumentMap = DiscordArgumentMap<String>()
                if (event.d["data"]!!.jsonObject["options"] != null) {
                    val options = event.d["data"]!!.jsonObject["options"]
                    val resolved = event.d["data"]!!.jsonObject["resolved"]
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
                                val userJson = resolved!!.jsonObject["users"]!!.jsonObject
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
                            DiscordFlags.CommandArgumentType.ATTACHMENT -> {
                                val attachmentJson = resolved!!.jsonObject["attachments"]!!.jsonObject
                                val attachmentData = attachmentJson[attachmentJson.keys.first()]!!.jsonObject

                                var width: Int? = attachmentData["width"]?.jsonPrimitive?.int
                                var height: Int? = attachmentData["height"]?.jsonPrimitive?.int
                                var ephemeral = attachmentData["ephemeral"]?.jsonPrimitive?.boolean ?: false

                                val description = attachmentData["description"]?.jsonPrimitive?.content ?: ""
                                val contentType = attachmentData["content_type"]?.jsonPrimitive?.content ?: "text/plain"

                                DiscordAttachmentType(DiscordAttachment(
                                    id = attachmentData["id"]!!.jsonPrimitive.content,
                                    filename = attachmentData["filename"]!!.jsonPrimitive.content,
                                    description = description, contentType = contentType,
                                    size = attachmentData["size"]!!.jsonPrimitive.int,
                                    url = attachmentData["url"]!!.jsonPrimitive.content,
                                    proxyUrl = attachmentData["proxy_url"]!!.jsonPrimitive.content,
                                    width = width, height = height, ephemeral = ephemeral
                                ))
                            }
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
