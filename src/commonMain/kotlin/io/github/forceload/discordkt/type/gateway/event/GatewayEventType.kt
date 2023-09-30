package io.github.forceload.discordkt.type.gateway.event

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

@Suppress("UNCHECKED_CAST")
@OptIn(InternalSerializationApi::class)
abstract class GatewayEventType {
    companion object {
        val events = HashMap<Int, KSerializer<GatewayEventType>>()
        init {
            events.putAll(mapOf(
                0 to Dispatch::class.serializer() as KSerializer<GatewayEventType>,
                1 to Heartbeat::class.serializer() as KSerializer<GatewayEventType>,
                2 to Identify::class.serializer() as KSerializer<GatewayEventType>,
                10 to Hello::class.serializer() as KSerializer<GatewayEventType>,
                11 to HeartbeatACK::class.serializer() as KSerializer<GatewayEventType>
            ))
        }

        operator fun get(index: Int): KSerializer<GatewayEventType> = events[index]!!
    }

    abstract val opCode: Int
}