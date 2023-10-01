package io.github.forceload.discordkt.type.guilds

import kotlinx.serialization.Serializable

@Serializable
data class UnavailableGuild(val id: String, val unavailable: Boolean)