package io.github.discordkt.discordkt.discord.internal.gateway.event

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class GatewayEvent(val op: Int, val d: JsonObject?, val s: Int?, val t: String?)
