package io.github.forceload.discordkt.type.gateway.event

import io.github.forceload.discordkt.type.gateway.DiscordActivity
import io.github.forceload.discordkt.type.gateway.PresenceStatus
import kotlinx.serialization.Serializable

@Serializable
class UpdatePresence(
    val since: Int?, val activities: Array<DiscordActivity>,
    val status: PresenceStatus, val afk: Boolean
): GatewayEventType(), ClientSideEvent {
    override val opCode: Int = 3
}