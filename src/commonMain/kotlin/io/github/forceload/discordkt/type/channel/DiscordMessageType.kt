package io.github.forceload.discordkt.type.channel

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * https://discord.com/developers/docs/resources/channel#message-object-message-types
 */

@Serializable(with = DiscordMessageType.Serializer::class)
enum class DiscordMessageType(val value: Int, val deletable: Boolean) {
    DEFAULT(0, true),
    RECIPIENT_ADD(1, false),
    RECIPIENT_REMOVE(2, false),
    CALL(3, false),
    CHANNEL_NAME_CHANGE(4, false),
    CHANNEL_ICON_CHANGE(5, false),
    CHANNEL_PINNED_MESSAGE(6, true),
    USER_JOIN(7, true),
    GUILD_BOOST(8, true),
    GUILD_BOOST_TIER_1(9, true),
    GUILD_BOOST_TIER_2(10, true),
    GUILD_BOOST_TIER_3(11, true),
    CHANNEL_FLOW_ADD(12, true),
    GUILD_DISCOVERY_DISQUALIFIED(14, false),
    GUILD_DISCOVERY_REQUALIFIED(15, false),
    GUILD_DISCOVERY_GRACE_PERIOD_INITIAL_WARNING(16, false),
    GUILD_DISCOVERY_GRACE_PERIOD_FINAL_WARNING(17, false),
    THREAD_CREATED(18, true),
    REPLY(19, true),
    CHAT_INPUT_COMMAND(20, true),
    THREAD_STARTER_MESSAGE(21, false),
    GUILD_INVITE_REMINDER(22, true),
    CONTEXT_MENU_COMMAND(23, true),
    AUTO_MODERATION_ACTION(24, true), // Can only be deleted by members with `MANAGE_MESSAGES` permission
    ROLE_SUBSCRIPTION_PURCHASE(25, true),
    INTERACTION_PREMIUM_UPSELL(26, true),
    STAGE_START(27, true),
    STAGE_END(28, true),
    STAGE_SPEAKER(29, true),
    STAGE_TOPIC(31, true),
    GUILD_APPLICATION_PREMIUM_SUBSCRIPTION(32, false);

    companion object {
        fun fromID(id: Int) = entries.first { it.value == id }
    }

    object Serializer: KSerializer<DiscordMessageType> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("DiscordMessageType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): DiscordMessageType =
            DiscordMessageType.fromID(decoder.decodeInt())

        override fun serialize(encoder: Encoder, value: DiscordMessageType) =
            encoder.encodeInt(value.value)
    }
}