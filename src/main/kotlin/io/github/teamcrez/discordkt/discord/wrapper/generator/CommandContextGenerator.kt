package io.github.teamcrez.discordkt.discord.wrapper.generator

import io.github.teamcrez.discordkt.discord.internal.command.context.CommandContext
import io.github.teamcrez.discordkt.discord.internal.gateway.event.GatewayEvent
import io.github.teamcrez.discordkt.discord.wrapper.DiscordChannel
import io.github.teamcrez.discordkt.discord.wrapper.DiscordInteraction
import io.github.teamcrez.discordkt.discord.wrapper.DiscordUser
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object CommandContextGenerator {
    fun fromEvent(event: GatewayEvent): CommandContext {
        if (event.d?.get("member") != null) {
            return CommandContext(
                DiscordUser(
                    event.d["member"]!!.jsonObject["user"]!!.jsonObject["id"]!!.jsonPrimitive.content,
                    event.d["member"]!!.jsonObject["user"]!!.jsonObject["username"]!!.jsonPrimitive.content,
                    event.d["member"]!!.jsonObject["user"]!!.jsonObject["discriminator"]!!.jsonPrimitive.content
                ),
                DiscordChannel(event.d["channel_id"]!!.jsonPrimitive.content),

                DiscordInteraction(event.d["id"]!!.jsonPrimitive.content, event.d["token"]!!.jsonPrimitive.content)
            )
        } else {
            return CommandContext(
                DiscordUser(
                    event.d?.get("user")!!.jsonObject["id"]!!.jsonPrimitive.content,
                    event.d["user"]!!.jsonObject["username"]!!.jsonPrimitive.content,
                    event.d["user"]!!.jsonObject["discriminator"]!!.jsonPrimitive.content
                ),
                DiscordChannel(event.d["channel_id"]!!.jsonPrimitive.content),

                DiscordInteraction(event.d["id"]!!.jsonPrimitive.content, event.d["token"]!!.jsonPrimitive.content)
            )
        }
    }
}
