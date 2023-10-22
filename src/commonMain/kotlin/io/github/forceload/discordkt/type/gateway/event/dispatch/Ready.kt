package io.github.forceload.discordkt.type.gateway.event.dispatch

import io.github.forceload.discordkt.type.DiscordUser
import io.github.forceload.discordkt.type.guilds.UnavailableGuild
import io.github.forceload.discordkt.util.SerializerExtension.arraySerializer
import io.github.forceload.discordkt.util.SerializerUtil.makeStructure
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject

@Serializable(with = Ready.Serializer::class)
class Ready(
    val version: Int, val user: DiscordUser,
    val guilds: Array<UnavailableGuild>, val sessionID: String,
    val resumeGatewayURL: String, val shard: Array<Int>? = null
): DispatchEventType() {
    override val code = "READY"

    object Serializer: KSerializer<Ready> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("READY") {
                element<Int>("v")
                element<DiscordUser>("user")
                element<Array<UnavailableGuild>>("guilds")
                element<String>("session_id")
                element<String>("resume_gateway_url")
                element<Array<Int>>("shard", isOptional = true)
                element<JsonObject>("application") // TODO("귀찮아")
            }

        override fun deserialize(decoder: Decoder): Ready {
            var version: Int? = null
            var user: DiscordUser? = null
            var guilds: Array<UnavailableGuild>? = null
            var sessionID: String? = null
            var resumeGatewayURL: String? = null
            var shard: Array<Int>? = null

            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> version = decodeIntElement(descriptor, index)
                    1 -> user = decodeSerializableElement(descriptor, index, DiscordUser.Serializer)
                    2 -> guilds = decodeSerializableElement(descriptor, index, UnavailableGuild.serializer().arraySerializer())
                    3 -> sessionID = decodeStringElement(descriptor, index)
                    4 -> resumeGatewayURL = decodeStringElement(descriptor, index)
                    5 -> shard = decodeSerializableElement(descriptor, index, Int.serializer().arraySerializer())
                    6 -> decodeSerializableElement(descriptor, index, JsonObject.serializer())
                }
            }

            return Ready(version!!, user!!, guilds!!, sessionID!!, resumeGatewayURL!!, shard)
        }

        override fun serialize(encoder: Encoder, value: Ready) {
            encoder.beginStructure(descriptor).run {
                encodeIntElement(descriptor, 0, value.version)
                encodeSerializableElement(descriptor, 1, DiscordUser.Serializer, value.user)
                encodeSerializableElement(descriptor, 2, UnavailableGuild.serializer().arraySerializer(), value.guilds)
                encodeStringElement(descriptor, 3, value.sessionID)
                encodeStringElement(descriptor, 4, value.resumeGatewayURL)
                value.shard?.let { encodeSerializableElement(descriptor, 5, Int.serializer().arraySerializer(), value.shard) }

                endStructure(descriptor)
            }
        }
    }

    override fun toString(): String {
        return "Ready(version=$version, user=$user, guilds=${guilds.contentToString()}, sessionID='$sessionID', resumeGatewayURL='$resumeGatewayURL', shard=${shard?.contentToString()}, code='$code')"
    }
}