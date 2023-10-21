package io.github.forceload.discordkt.type.application

import io.github.forceload.discordkt.util.PrimitiveDescriptors
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = OAuth2Scope.Serializer::class)
enum class OAuth2Scope(val id: String) {
    ACTIVITY_READ("activities.read"), ACTIVITY_WRITE("activities.write"),
    APPLICATION_BUILD_READ("applications.builds.read"), APPLICATION_BUILD_WRITE("applications.builds.write"),

    APPLICATION_COMMANDS("applications.commands"),
    APPLICATION_COMMAND_UPDATE("applications.commands.update"),
    APPLICATION_COMMAND_PERMISSION_UPDATE("applications.commands.permission.update"),

    APPLICATION_ENTITLEMENTS("applications.entitlements"), APPLICATION_STORE_UPDATE("applications.store.update"),
    BOT("bot"), CONNECTIONS("connections"), DM_CHANNEL_READ("dm_channels.read"),
    EMAIL("email"), GROUP_DM_JOIN("gdm.join"),

    GUILDS("guilds"), GUILD_JOIN("guilds.join"), GUILD_MEMBER_READ("guilds.members.read"),
    IDENTIFY("identify"), MESSAGE_READ("messages.read"), RELATIONSHIP_READ("relationships.read"),
    ROLE_CONNECTION_WRITE("role_connections.write"),

    RPC("rpc"), RPC_ACTIVITY_WRITE("rpc.activities.write"), RPC_NOTIFICATION_READ("rpc.notifications.read"),
    RPC_VOICE_READ("rpc.voice.read"), RPC_VOICE_WRITE("rpc.voice.write"),
    VOICE("voice"), WEBHOOK_INCOMING("webhook.incoming");

    companion object { fun fromID(id: String) = entries.first { it.id == id } }

    object Serializer: KSerializer<OAuth2Scope> {
        override val descriptor: SerialDescriptor =
            PrimitiveDescriptors["OAuth2Scope"].STRING

        override fun deserialize(decoder: Decoder) = fromID(decoder.decodeString())
        override fun serialize(encoder: Encoder, value: OAuth2Scope) = encoder.encodeString(value.id)
    }
}