package io.github.forceload.discordkt.type.gateway

import kotlinx.serialization.Serializable

@Serializable
data class GatewayConnectionProperty(
    val os: String = "bot",
    val browser: String = "discord.kt", val device: String = "discord.kt"
)