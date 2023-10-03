package io.github.forceload.discordkt.type.gateway

import io.github.forceload.discordkt.util.SerializerExtension.arraySerializer
import io.github.forceload.discordkt.util.SerializerExtension.decodeNullableString
import io.github.forceload.discordkt.util.SerializerUtil.makeStructure
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * https://discord.com/developers/docs/topics/gateway-events#activity-object-activity-types
 */
@Serializable(with = ActivityType.Serializer::class)
enum class ActivityType(val id: Int) {
    GAME(0), // Playing {name}
    STREAMING(1), // Streaming {details}
    LISTENING(2), // Listening to {name}
    WATCHING(3), // Watching {name}
    CUSTOM(4), // {emoji} {state}
    COMPETING(5); // Competing in {name}

    companion object {
        fun fromID(id: Int) = entries.first { it.id == id }
    }

    object Serializer: KSerializer<ActivityType> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("ActivityType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder) =
            ActivityType.fromID(decoder.decodeInt())

        override fun serialize(encoder: Encoder, value: ActivityType) =
            encoder.encodeInt(value.id)
    }
}

@Suppress("unused")
enum class ActivityFlags(val id: Int) {
    INSTANCE(1 shl 0),
    JOIN(1 shl 1),
    SPECTATE(1 shl 2),
    JOIN_REQUEST(1 shl 3),
    SYNC(1 shl 6),
    PLAY(1 shl 7),
    PARTY_PRIVACY_FRIENDS(1 shl 8),
    PARTY_PRIVACY_VOICE_CHANNEL(1 shl 9),
    EMBEDDED(1 shl 10);

    object SetSerializer: KSerializer<Set<ActivityFlags>> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("DiscordUserFlags", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): Set<ActivityFlags> {
            val intentFlag = decoder.decodeInt()

            val permissionSet = mutableSetOf<ActivityFlags>()
            ActivityFlags.entries.forEach {
                if (intentFlag and it.id == it.id) permissionSet.add(it)
            }

            return permissionSet
        }

        override fun serialize(encoder: Encoder, value: Set<ActivityFlags>) {
            var result = 0
            value.forEach { result = result or it.id }

            encoder.encodeInt(result)
        }
    }
}

@Serializable(with = ActivityTimestamps.Serializer::class)
class ActivityTimestamps(val start: Int? = null, val end: Int? = null) {
    object Serializer: KSerializer<ActivityTimestamps> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("ActivityTimestamp") {
                element<Int>("start", isOptional = true)
                element<Int>("end", isOptional = true)
            }

        override fun deserialize(decoder: Decoder): ActivityTimestamps {
            var start: Int? = null
            var end: Int? = null

            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> start = decodeIntElement(descriptor, index)
                    1 -> end = decodeIntElement(descriptor, index)
                }
            }

            return ActivityTimestamps(start, end)
        }

        override fun serialize(encoder: Encoder, value: ActivityTimestamps) {
            encoder.beginStructure(descriptor).run {
                value.start?.let { encodeIntElement(descriptor, 0, value.start) }
                value.end?.let { encodeIntElement(descriptor, 0, value.end) }
                endStructure(descriptor)
            }
        }
    }
}

@Serializable(with = ActivityEmoji.Serializer::class)
class ActivityEmoji(
    val name: String, val id: String? = null,
    val animated: Boolean? = null
) {
    object Serializer: KSerializer<ActivityEmoji> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("ActivityEmoji") {
                element<String>("name")
                element<String>("id", isOptional = true)
                element<Boolean>("animated", isOptional = true)
            }

        override fun deserialize(decoder: Decoder): ActivityEmoji {
            var name: String? = null
            var id: String? = null

            var animated: Boolean? = null
            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> name = decodeStringElement(descriptor, index)
                    1 -> id = decodeStringElement(descriptor, index)
                    2 -> animated = decodeBooleanElement(descriptor, index)
                }
            }

            return ActivityEmoji(name!!, id, animated)
        }

        override fun serialize(encoder: Encoder, value: ActivityEmoji) {
            encoder.beginStructure(descriptor).run {
                encodeStringElement(descriptor, 0, value.name)
                value.id?.let { encodeStringElement(descriptor, 1, value.id) }
                value.animated?.let { encodeBooleanElement(descriptor, 1, value.animated) }

                endStructure(descriptor)
            }
        }
    }
}

@Serializable(with = ActivityParty.Serializer::class)
class ActivityParty(
    val id: String? = null,
    val size: Array<Int>? = null
) {
    object Serializer: KSerializer<ActivityParty> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("ActivityParty") {
                element<String>("id", isOptional = true)
                element<Array<Int>>("size", isOptional = true)
            }

        override fun deserialize(decoder: Decoder): ActivityParty {
            var id: String? = null
            var size: Array<Int>? = null

            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> id = decodeStringElement(descriptor, index)
                    1 -> size = decodeSerializableElement(descriptor, index, Int.serializer().arraySerializer())
                }
            }

            return ActivityParty(id, size)
        }

        override fun serialize(encoder: Encoder, value: ActivityParty) {
            encoder.beginStructure(descriptor).run {
                value.id?.let { encodeStringElement(descriptor, 0, value.id) }
                value.size?.let { encodeSerializableElement(descriptor, 1, Int.serializer().arraySerializer(), value.size) }
            }
        }
    }
}

@Serializable(with = ActivityAssets.Serializer::class)
class ActivityAssets(
    val largeImage: String? = null, val largeText: String? = null,
    val smallImage: String? = null, val smallText: String? = null
) {
    object Serializer: KSerializer<ActivityAssets> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("ActivityAsset") {
                element<String>("large_image", isOptional = true)
                element<String>("large_text", isOptional = true)
                element<String>("small_image", isOptional = true)
                element<String>("small_text", isOptional = true)
            }

        override fun deserialize(decoder: Decoder): ActivityAssets {
            var largeImage: String? = null; var largeText: String? = null
            var smallImage: String? = null; var smallText: String? = null

            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> largeImage = decodeStringElement(descriptor, index)
                    1 -> largeText = decodeStringElement(descriptor, index)
                    2 -> smallImage = decodeStringElement(descriptor, index)
                    3 -> smallText = decodeStringElement(descriptor, index)
                }
            }

            return ActivityAssets(largeImage, largeText, smallImage, smallText)
        }

        override fun serialize(encoder: Encoder, value: ActivityAssets) {
            encoder.beginStructure(descriptor).run {
                value.largeImage?.let { encodeStringElement(descriptor, 0, value.largeImage) }
                value.largeText?.let { encodeStringElement(descriptor, 1, value.largeText) }
                value.smallImage?.let { encodeStringElement(descriptor, 2, value.smallImage) }
                value.smallText?.let { encodeStringElement(descriptor, 3, value.smallText) }

                endStructure(descriptor)
            }
        }
    }
}

@Serializable(with = ActivitySecrets.Serializer::class)
class ActivitySecrets(val join: String? = null, val spectate: String? = null, val match: String? = null) {
    object Serializer: KSerializer<ActivitySecrets> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("ActivityAsset") {
                element<String>("join", isOptional = true)
                element<String>("spectate", isOptional = true)
                element<String>("match", isOptional = true)
            }

        override fun deserialize(decoder: Decoder): ActivitySecrets {
            var join: String? = null
            var spectate: String? = null
            var match: String? = null

            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> join = decodeStringElement(descriptor, index)
                    1 -> spectate = decodeStringElement(descriptor, index)
                    2 -> match = decodeStringElement(descriptor, index)
                }
            }

            return ActivitySecrets(join, spectate, match)
        }

        override fun serialize(encoder: Encoder, value: ActivitySecrets) {
            encoder.beginStructure(descriptor).run {
                value.join?.let { encodeStringElement(descriptor, 0, value.join) }
                value.spectate?.let { encodeStringElement(descriptor, 1, value.spectate) }
                value.match?.let { encodeStringElement(descriptor, 2, value.match) }

                endStructure(descriptor)
            }
        }
    }
}

@Serializable
data class ActivityButtons(val label: String, val url: String)

/**
 * https://discord.com/developers/docs/topics/gateway-events#activity-object-activity-structure
 */
@Serializable(with = DiscordActivity.Serializer::class)
class DiscordActivity(
    val name: String,
    val type: ActivityType,
    val url: String? = null,
    val createdAt: Int,
    val timestamps: ActivityTimestamps? = null,
    val appID: String? = null,
    val details: String? = null,
    val state: String? = null,
    val emoji: ActivityEmoji? = null,
    val party: ActivityParty? = null,
    val assets: ActivityAssets? = null,
    val secrets: ActivitySecrets? = null,
    val instance: Boolean? = null,
    val flags: Set<ActivityFlags>? = null,
    val buttons: Array<ActivityButtons>? = null
) {
    object Serializer: KSerializer<DiscordActivity> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("DiscordActivity") {
                element<String>("name")
                element<Int>("type")
                element<String?>("url", isOptional = true)
                element<Int>("created_at")
                element<ActivityTimestamps>("timestamps", isOptional = true)
                element<String>("application_id", isOptional = true)
                element<String?>("details", isOptional = true)
                element<String?>("state", isOptional = true)
                element<ActivityEmoji?>("emoji", isOptional = true)
                element<ActivityParty>("party", isOptional = true)
                element<ActivityAssets>("assets", isOptional = true)
                element<ActivitySecrets>("secrets", isOptional = true)
                element<Boolean>("instance", isOptional = true)
                element<Int>("flags", isOptional = true)
                element<Array<ActivityButtons>>("buttons", isOptional = true)
            }

        @OptIn(ExperimentalSerializationApi::class)
        override fun deserialize(decoder: Decoder): DiscordActivity {
            var name: String? = null
            var type: ActivityType? = null
            var url: String? = null
            var createdAt: Int? = null
            var timestamps: ActivityTimestamps? = null
            var appID: String? = null
            var details: String? = null
            var state: String? = null
            var emoji: ActivityEmoji? = null
            var party: ActivityParty? = null
            var assets: ActivityAssets? = null
            var secrets: ActivitySecrets? = null
            var instance: Boolean? = null
            var flags: Set<ActivityFlags>? = null
            var buttons: Array<ActivityButtons>? = null

            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> name = decodeStringElement(descriptor, index)
                    1 -> type = decodeSerializableElement(descriptor, index, ActivityType.Serializer)
                    2 -> url = decodeNullableString(descriptor, index)
                    3 -> createdAt = decodeIntElement(descriptor, index)
                    4 -> timestamps = decodeSerializableElement(descriptor, index, ActivityTimestamps.Serializer)
                    5 -> appID = decodeStringElement(descriptor, index)
                    6 -> details = decodeNullableString(descriptor, index)
                    7 -> state = decodeNullableString(descriptor, index)
                    8 -> emoji = decodeNullableSerializableElement(descriptor, index, ActivityEmoji.Serializer)
                    9 -> party = decodeSerializableElement(descriptor, index, ActivityParty.Serializer)
                    10 -> secrets = decodeSerializableElement(descriptor, index, ActivitySecrets.Serializer)
                    11 -> instance = decodeBooleanElement(descriptor, index)
                    12 -> flags = decodeSerializableElement(descriptor, index, ActivityFlags.SetSerializer)
                    13 -> buttons =
                        decodeSerializableElement(descriptor, index, ActivityButtons.serializer().arraySerializer())
                }
            }

            return DiscordActivity(
                name!!, type!!, url, createdAt!!, timestamps, appID,
                details, state, emoji, party, assets, secrets, instance, flags, buttons
            )
        }

        override fun serialize(encoder: Encoder, value: DiscordActivity) {
            encoder.beginStructure(descriptor).run {
                encodeStringElement(descriptor, 0, value.name)
                encodeSerializableElement(descriptor, 1, ActivityType.Serializer, value.type)
                value.url?.let { encodeStringElement(descriptor, 2, value.url) }
                encodeIntElement(descriptor, 3, value.createdAt)
                value.timestamps?.let { encodeSerializableElement(descriptor, 4, ActivityTimestamps.Serializer, value.timestamps) }
                value.appID?.let { encodeStringElement(descriptor, 5, value.appID) }
                value.details?.let { encodeStringElement(descriptor, 6, value.details) }
                value.state?.let { encodeStringElement(descriptor, 7, value.state) }
                value.emoji?.let { encodeSerializableElement(descriptor, 8, ActivityEmoji.Serializer, value.emoji) }
                value.party?.let { encodeSerializableElement(descriptor, 9, ActivityParty.Serializer, value.party) }
                value.assets?.let { encodeSerializableElement(descriptor, 10, ActivityAssets.Serializer, value.assets) }
                value.secrets?.let { encodeSerializableElement(descriptor, 11, ActivitySecrets.Serializer, value.secrets) }
                value.instance?.let { encodeBooleanElement(descriptor, 12, value.instance) }
                value.flags?.let { encodeSerializableElement(descriptor, 13, ActivityFlags.SetSerializer, value.flags) }
                value.buttons?.let {
                    encodeSerializableElement(descriptor, 14, ActivityButtons.serializer().arraySerializer(), value.buttons)
                }

                endStructure(descriptor)
            }
        }
    }
}