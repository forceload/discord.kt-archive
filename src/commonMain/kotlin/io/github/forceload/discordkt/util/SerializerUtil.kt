package io.github.forceload.discordkt.util

import kotlinx.serialization.json.Json

object SerializerUtil {
    var commandOptionMaxDepth = 16
    val jsonBuild = Json { ignoreUnknownKeys = true }
}
