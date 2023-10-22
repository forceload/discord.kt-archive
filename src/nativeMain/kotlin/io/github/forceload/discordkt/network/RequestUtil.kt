package io.github.forceload.discordkt.network

import io.ktor.client.*
import io.ktor.client.engine.curl.*

actual object ClientContainer {
    actual val client = HttpClient(Curl)
    actual const val platform = "Kotlin/Native"
}