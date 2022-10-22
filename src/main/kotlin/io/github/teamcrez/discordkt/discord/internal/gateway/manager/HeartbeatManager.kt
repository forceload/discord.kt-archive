package io.github.teamcrez.discordkt.discord.internal.gateway.manager

import io.github.teamcrez.discordkt.discord.api.DiscordFlags
import io.github.teamcrez.discordkt.discord.internal.gateway.GatewayListener
import io.github.teamcrez.discordkt.discord.internal.gateway.GatewayStorage
import io.github.teamcrez.discordkt.discord.internal.gateway.event.GatewayEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object HeartbeatManager {
    suspend fun heartbeatLoop(scope: CoroutineScope, listener: GatewayListener) {
        while (listener.isRunning) {
            if (GatewayStorage.heartbeatInterval != -1) { break }
            delay(1)
        }

        while (scope.isActive && listener.isRunning) {
            listener.client.webSocket.send(
                Json.encodeToString(
                GatewayEvent(1, null, GatewayStorage.sequenceNumber, null)
            ))

            delay((GatewayStorage.heartbeatInterval * DiscordFlags.GatewayFlag.HEARTBEAT_TIMESTAMP_SCALE).toLong())
        }
    }
}
