package io.github.forceload.discordkt.network

import io.ktor.client.*
import io.ktor.client.engine.curl.*
import io.ktor.client.plugins.*

actual object ClientContainer {
    actual val client = HttpClient(Curl) { install(HttpTimeout) }
    actual const val platform = "Kotlin/Native"
}