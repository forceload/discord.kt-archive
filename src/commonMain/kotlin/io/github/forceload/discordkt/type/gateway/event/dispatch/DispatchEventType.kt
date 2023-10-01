package io.github.forceload.discordkt.type.gateway.event.dispatch

import io.github.forceload.discordkt.exception.gateway.GatewaySerializationFailException
import io.github.forceload.discordkt.type.gateway.event.GatewayEventType
import io.github.forceload.discordkt.util.DiscordConstants
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable
@Suppress("UNCHECKED_CAST")
@OptIn(InternalSerializationApi::class)
abstract class DispatchEventType: GatewayEventType() {
    companion object {
        val events = HashMap<String, KSerializer<DispatchEventType>>()
        init {
            events.putAll(mapOf(
                "READY" to Ready::class.serializer(),
                "INTERACTION_CREATE" to DiscordInteraction::class.serializer()
            ) as Map<String, KSerializer<DispatchEventType>>)
        }

        operator fun get(key: String): KSerializer<DispatchEventType> {
            try { return events[key.uppercase()]!! } catch (err: NullPointerException) {
                throw GatewaySerializationFailException("Unregistered Dispatch Type: $key")
            }
        }
    }

    abstract val code: String
    override val opCode = DiscordConstants.OpCode.DISPATCH
}