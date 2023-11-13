package io.github.forceload.discordkt.type.gateway.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Resume(
    val token: String, @SerialName("session_id") val sessionID: String,
    @SerialName("seq") val sequenceNumber: Int
): GatewayEventType(), ClientSideEvent {
    override val opCode: Int = 6
}