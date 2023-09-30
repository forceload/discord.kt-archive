package io.github.forceload.discordkt.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GatewayBot(
    val url: String, val shards: Int,
    @SerialName("session_start_limit") val sessionStartLimit: SessionStartLimit
) {
    @Serializable
    data class SessionStartLimit(
        val total: Int, val remaining: Int,
        @SerialName("reset_after") val resetAfter: Int,
        @SerialName("max_concurrency") val maxConcurrency: Int
    )
}