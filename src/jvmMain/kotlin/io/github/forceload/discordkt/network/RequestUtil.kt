package io.github.forceload.discordkt.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*

actual object ClientContainer {
    actual val client = HttpClient(CIO) { install(HttpTimeout) }
    actual const val platform = "Kotlin/JVM"
}