package io.github.forceload.discordkt.type.gateway.event

import io.github.forceload.discordkt.exception.gateway.GatewaySerializationFailException
import io.github.forceload.discordkt.type.gateway.event.dispatch.DispatchEventType
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

@Suppress("UNCHECKED_CAST")
@OptIn(InternalSerializationApi::class)
abstract class
GatewayEventType {
    companion object {
        val events = HashMap<Int, KSerializer<GatewayEventType>>()
        init {
            events.putAll(mapOf(
                // Dispatch Handled by DispatchEventType
                1 to Heartbeat::class.serializer(),
                2 to Identify::class.serializer(),
                10 to Hello::class.serializer(),
                11 to HeartbeatACK::class.serializer()
            ) as Map<Int, KSerializer<GatewayEventType>>)
        }

        operator fun get(op: Int, type: String? = null): KSerializer<GatewayEventType> {
            return when (op) {
                0 -> DispatchEventType[type!!] as KSerializer<GatewayEventType>
                else -> try { events[op]!! } catch (err: NullPointerException) {
                    throw GatewaySerializationFailException("Unregistered Event Type: Opcode $op")
                }
            }
        }
    }

    abstract val opCode: Int
}