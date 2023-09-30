package io.github.forceload.discordkt.network

import io.github.forceload.discordkt.util.DiscordConstants
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking

object RequestUtil {
    private val client = HttpClient(CIO)

    fun get(url: String, authorization: String, vararg params: Pair<String, Any>) =
        runBlocking {
            client.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "discord.com"

                    appendPathSegments("api", "v${DiscordConstants.apiVersion}")
                    appendPathSegments(url)

                    params.forEach {
                        parameters.append(it.first, it.second.toString())
                    }
                }

                headers {
                    append("Authorization", "Bot $authorization")
                }
            }.body<String>()
        }

    fun post(url: String, authorization: String, data: String, type: ContentType = ContentType.Application.Json) =
        runBlocking {
            client.post {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "discord.com"

                    appendPathSegments("api", "v${DiscordConstants.apiVersion}")
                    appendPathSegments(url)
                }

                headers {
                    append("Authorization", "Bot $authorization")
                }

                contentType(type)
                setBody(data)
            }.body<String>()
        }

    fun delete(url: String, authorization: String) =
        runBlocking {
            println(url)
            client.delete {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "discord.com"

                    appendPathSegments("api", "v${DiscordConstants.apiVersion}")
                    appendPathSegments(url)
                }

                headers {
                    append("Authorization", "Bot $authorization")
                }
            }.body<String>()
        }
}