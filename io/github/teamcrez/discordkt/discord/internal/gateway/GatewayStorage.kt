package io.github.teamcrez.discordkt.discord.internal.gateway

import io.github.teamcrez.discordkt.discord.internal.gateway.event.GatewayEvent

object GatewayStorage {
    val messages: ArrayList<String> = ArrayList()
    val events: ArrayList<GatewayEvent> = ArrayList()

    var heartbeatInterval: Int = -1
    var sequenceNumber: Int? = null

}