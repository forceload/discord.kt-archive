package io.github.forceload.discordkt.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking

object RequestUtil {
    val client = HttpClient(CIO)

    fun get(url: String, authorization: String) =
        runBlocking {
            client.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "discord.com"

                    appendPathSegments("api", "v10")
                    appendPathSegments(url)
                }

                headers {
                    append("Authorization", "Bot $authorization")
                }
            }.body<String>()
        }
}