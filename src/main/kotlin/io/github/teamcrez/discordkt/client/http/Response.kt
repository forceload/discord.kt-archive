package io.github.teamcrez.discordkt.client.http

import kotlinx.serialization.Serializable

@Serializable
data class Response(val status: Int, val data: String) {
    constructor(status: Int, data: StringBuilder) : this(status, data.toString())
}
