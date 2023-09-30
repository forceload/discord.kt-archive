package io.github.forceload.discordkt.type.gateway

import io.github.forceload.discordkt.util.SerializerUtil
import kotlinx.serialization.encodeToString
import org.junit.jupiter.api.Test

class GatewaySerializationTest {
    @Test
    fun gatewaySerializerTest() {
        val gatewayEvent = "{\"op\": 10, \"d\": {\"heartbeat_interval\": 45000}}"
        val decoded = SerializerUtil.jsonBuild.decodeFromString<GatewayEvent>(gatewayEvent)
        println(decoded)
        val encoded = SerializerUtil.jsonBuild.encodeToString(decoded)
        println(encoded)
    }
}