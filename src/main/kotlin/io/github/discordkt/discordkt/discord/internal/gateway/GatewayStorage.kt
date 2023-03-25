package io.github.discordkt.discordkt.discord.internal.gateway

import io.github.discordkt.discordkt.discord.internal.gateway.event.GatewayEvent

object GatewayStorage {
    val messages: ArrayList<String> = ArrayList()
    val events: ArrayList<GatewayEvent> = ArrayList()
    var gatewayDebug = false

    var heartbeatInterval: Int = -1
    var sequenceNumber: Int? = null
}
