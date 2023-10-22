package io.github.forceload.discordkt.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*

actual object ClientContainer {
    actual val client = HttpClient(CIO)
    actual const val platform = "Kotlin/JVM"
}