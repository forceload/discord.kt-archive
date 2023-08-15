package io.github.forceload.discordkt

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

suspend fun DiscordBot.get(url: String) = client.get {
    url {
        protocol = URLProtocol.HTTPS
        host = "discord.com"

        appendPathSegments("api", "v10")
        appendPathSegments(url)
    }

    headers {
        append("Authorization", "Bot ${credentials.token}")
    }
}.body<String>()
