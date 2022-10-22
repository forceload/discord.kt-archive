package io.github.teamcrez.discordkt.discord.internal.gateway.manager

import io.github.teamcrez.discordkt.discord.internal.command.CommandStorage
import io.github.teamcrez.discordkt.discord.internal.command.context.CommandContext
import io.github.teamcrez.discordkt.discord.internal.command.context.CommandData
import io.github.teamcrez.discordkt.discord.internal.gateway.event.GatewayEvent
import io.github.teamcrez.discordkt.discord.wrapper.DiscordChannel
import io.github.teamcrez.discordkt.discord.wrapper.DiscordInteraction
import io.github.teamcrez.discordkt.discord.wrapper.DiscordUser
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object CommandManager {
    fun processCommand(event: GatewayEvent) {
        val commandName = event.d?.get("data")!!.jsonObject["name"]!!.jsonPrimitive.content

        if (CommandStorage.commandProcesses.keys.contains(commandName)) {
            val commandComponent = CommandStorage.commandProcesses[commandName]!!
            commandComponent[commandComponent.keys.first()]?.let {it(
                CommandData(
                    CommandContext(
                        DiscordUser(event.d["member"]!!.jsonObject["user"]!!.jsonObject["id"]!!.jsonPrimitive.content),
                        DiscordChannel(event.d["channel_id"]!!.jsonPrimitive.content),

                        DiscordInteraction(event.d["id"]!!.jsonPrimitive.content, event.d["token"]!!.jsonPrimitive.content)
                    )
                )
            )}
        }
    }
}
