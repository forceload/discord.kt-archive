package io.github.forceload.discordkt.exception.gateway

class GatewaySerializationFailException(val reason: String): Throwable() {
    override val message: String
        get() = reason
}