package io.github.forceload.discordkt.type

import io.github.forceload.discordkt.util.PrimitiveDescriptors
import io.github.forceload.discordkt.util.SerializerExtension.encodeNull
import io.github.forceload.discordkt.util.SerializerUtil.makeStructure
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = RoleTags.Serializer::class)
class RoleTags(
    val botID: String? = null,
    val integrationID: String? = null,
    val premiumSubscriber: Boolean = false,
    val subscriptionListingID: String? = null,
    val availableForPurchase: Boolean = false,
    val guildConnections: Boolean = false
) {
    object Serializer: KSerializer<RoleTags> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("RoleTags") {
                element<String>("bot_id", isOptional = true)
                element<String>("integration_id", isOptional = true)
                element<Boolean?>("premium_subscriber", isOptional = true) // will be null or not present
                element<String>("subscription_listing_id", isOptional = true)
                element<Boolean?>("available_for_purchase", isOptional = true) // will be null or not present
                element<Boolean?>("guild_connections", isOptional = true) // will be null or not present
            }

        override fun deserialize(decoder: Decoder): RoleTags {
            var botID: String? = null
            var integrationID: String? = null
            var premiumSubscriber = false
            var subscriptionListingID: String? = null
            var availableForPurchase = false
            var guildConnections = false

            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> botID = decodeStringElement(descriptor, index)
                    1 -> integrationID = decodeStringElement(descriptor, index)
                    2 -> premiumSubscriber = true
                    3 -> subscriptionListingID = decodeStringElement(descriptor, index)
                    4 -> availableForPurchase = true
                    5 -> guildConnections = true
                }
            }

            return RoleTags(
                botID, integrationID,
                premiumSubscriber, subscriptionListingID,
                availableForPurchase, guildConnections
            )
        }

        override fun serialize(encoder: Encoder, value: RoleTags) {
            encoder.beginStructure(descriptor).run {
                value.botID?.let { encodeStringElement(descriptor, 0, value.botID) }
                value.integrationID?.let { encodeStringElement(descriptor, 1, value.integrationID) }
                if (value.premiumSubscriber) encodeNull(descriptor, 2)
                value.subscriptionListingID?.let { encodeStringElement(descriptor, 3, value.subscriptionListingID) }
                if (value.availableForPurchase) encodeNull(descriptor, 4)
                if (value.guildConnections) encodeNull(descriptor, 5)
            }
        }
    }
}


enum class RoleFlags(val id: Int) {
    IN_PROMPT(1 shl 0);

    object SetSerializer: KSerializer<Set<RoleFlags>> {
        override val descriptor: SerialDescriptor = PrimitiveDescriptors["RoleFlags"].INT
        override fun deserialize(decoder: Decoder): Set<RoleFlags> {
            val intentFlag = decoder.decodeInt()

            val roleFlagSet = mutableSetOf<RoleFlags>()
            RoleFlags.entries.forEach {
                if (intentFlag and it.id == it.id) roleFlagSet.add(it)
            }

            return roleFlagSet
        }

        override fun serialize(encoder: Encoder, value: Set<RoleFlags>) {
            var result = 0
            value.forEach { result = result or it.id }

            encoder.encodeInt(result)
        }
    }
}

@Serializable
class DiscordRole(
    val id: String, val name: String, val color: Int, val hoist: Boolean,
    val icon: String? = null, @SerialName("unicode_emoji") val unicodeEmoji: String? = null, val position: Int,
    @Serializable(with = DiscordPermission.SetSerializer::class) val permissions: Set<DiscordPermission>,
    val managed: Boolean, val mentionable: Boolean, val tags: RoleTags? = null, val flags: Set<RoleFlags>
)