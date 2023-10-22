package io.github.forceload.discordkt.type.channel

import io.github.forceload.discordkt.type.DiscordUser
import io.github.forceload.discordkt.util.SerializerExtension.arraySerializer
import io.github.forceload.discordkt.util.SerializerExtension.decodeNullableString
import io.github.forceload.discordkt.util.SerializerExtension.encodeNullableString
import io.github.forceload.discordkt.util.SerializerUtil.makeStructure
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
class ReactionCountDetails(val burst: Int, val normal: Int)

@Serializable(with = DiscordEmoji.Serializer::class)
class DiscordEmoji(
    val id: String?, val name: String?,
    val roles: Array<String>, val user: DiscordUser?,
    val requireColons: Boolean?, val managed: Boolean?,
    val animated: Boolean?, val available: Boolean?
) {
    object Serializer: KSerializer<DiscordEmoji> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("DiscordEmoji") {
                element<String?>("id")
                element<String?>("name")
                element<Array<String>>("roles", isOptional = true)
                element<DiscordUser>("user", isOptional = true)
                element<Boolean>("require_colons", isOptional = true)
                element<Boolean>("managed", isOptional = true)
                element<Boolean>("animated", isOptional = true)
                element<Boolean>("available", isOptional = true)
            }

        override fun deserialize(decoder: Decoder): DiscordEmoji {
            var id: String? = null
            var name: String? = null
            var roles = arrayOf<String>()
            var user: DiscordUser? = null
            var requireColons: Boolean? = null
            var managed: Boolean? = null
            var animated: Boolean? = null
            var available: Boolean? = null
            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> id = decodeNullableString(descriptor, 0)
                    1 -> name = decodeNullableString(descriptor, 1)
                    2 -> roles = decodeSerializableElement(descriptor, 2, String.serializer().arraySerializer())
                    3 -> user = decodeSerializableElement(descriptor, 3, DiscordUser.Serializer)
                    4 -> requireColons = decodeBooleanElement(descriptor, 4)
                    5 -> managed = decodeBooleanElement(descriptor, 5)
                    6 -> animated = decodeBooleanElement(descriptor, 6)
                    7 -> available = decodeBooleanElement(descriptor, 7)
                }
            }

            val result = DiscordEmoji(id, name, roles, user, requireColons, managed, animated, available)
            return result
        }

        override fun serialize(encoder: Encoder, value: DiscordEmoji) {
            encoder.beginStructure(descriptor).run {
                encodeNullableString(descriptor, 0, value.id)
                encodeNullableString(descriptor, 1, value.name)
            }
        }
    }
}

/**
 * https://discord.com/developers/docs/resources/channel#reaction-object
 */
@Serializable
class DiscordReaction(
    val count: Int, val countDetails: ReactionCountDetails,
    val me: Boolean, val meBurst: Boolean, val emoji: DiscordEmoji,
    val burstColors: Array<Int>
)