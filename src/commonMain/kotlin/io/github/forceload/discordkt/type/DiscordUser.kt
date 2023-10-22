package io.github.forceload.discordkt.type

import io.github.forceload.discordkt.network.RequestUtil
import io.github.forceload.discordkt.type.enums.DiscordUserFlags
import io.github.forceload.discordkt.type.enums.PremiumType
import io.github.forceload.discordkt.util.DiscordConstants
import io.github.forceload.discordkt.util.SerializerExtension.decodeNullableBoolean
import io.github.forceload.discordkt.util.SerializerExtension.decodeNullableInt
import io.github.forceload.discordkt.util.SerializerExtension.decodeNullableString
import io.github.forceload.discordkt.util.SerializerExtension.encodeNullableString
import io.github.forceload.discordkt.util.SerializerUtil
import io.github.forceload.discordkt.util.SerializerUtil.makeStructure
import io.github.forceload.discordkt.util.cache.DMCache
import io.github.forceload.discordkt.util.logger.DebugLogger
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = DiscordUser.Serializer::class)
class DiscordUser(
    val id: String, val username: String, val discriminator: String
) {
    var globalName: String? = null
    var avatar: String? = null

    var bot: Boolean = false
    var system: Boolean = false
    var mfaEnabled: Boolean? = null
    var banner: String? = null

    var accentColor: Int? = null
    var locale: DiscordLocale? = null

    var verified: Boolean = false
    var email: String? = null

    val flags = mutableSetOf<DiscordUserFlags>()
    val publicFlags = mutableSetOf<DiscordUserFlags>()
    var premiumType: PremiumType = PremiumType.NONE

    var avatarDecoration: String? = null

    /**
     * DM Channel
     *
     * https://discord.com/developers/docs/resources/user#create-dm
     */
    private var dmChannel: DiscordChannel? = null
        get() {
            if (!DMCache.checkCache(auth!!, id, DiscordConstants.Caches.DM_CACHE_ALIVE)) {
                DebugLogger.log("Creating DM Channel for user $id")

                val data = "{\"recipient_id\": \"${id}\"}"
                val channel = RequestUtil.post("users/@me/channels", auth!!, data)
                field = SerializerUtil.jsonBuild.decodeFromString<DiscordChannel>(channel)
                DMCache[auth!!, id, DiscordConstants.Caches.DM_CACHE_ALIVE] = field!!
            } else field = DMCache[auth!!, id]

            return field
        }

    internal var auth: String? = null
    fun directMessage(text: String) = dmChannel?.sendMessage(text, auth!!)
    object Serializer: KSerializer<DiscordUser> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("DiscordUser") {
                element<String>("id")
                element<String>("username")
                element<String>("discriminator")
                element<String?>("global_name")
                element<String?>("avatar")
                element<Boolean>("bot", isOptional = true)
                element<Boolean>("system", isOptional = true)
                element<Boolean>("mfa_enabled", isOptional = true)
                element<String?>("banner", isOptional = true)
                element<Int?>("accent_color", isOptional = true)
                element<String>("locale", isOptional = true)
                element<Boolean>("verified", isOptional = true)
                element<String?>("email", isOptional = true)
                element<Int>("flags", isOptional = true)
                element<Int>("premium_type", isOptional = true)
                element<Int>("public_flags", isOptional = true)
                element<String?>("avatar_decoration", isOptional = true)
            }

        override fun deserialize(decoder: Decoder): DiscordUser {
            var id: String? = null
            var username: String? = null
            var discriminator: String? = null

            var globalName: String? = null
            var avatar: String? = null

            var bot = false
            var system = false
            var mfaEnabled: Boolean? = null
            var banner: String? = null
            var accentColor: Int? = null
            var locale: DiscordLocale? = null
            var verified: Boolean? = null

            var email: String? = null
            var flags = setOf<DiscordUserFlags>()
            var publicFlags = setOf<DiscordUserFlags>()

            var premiumType: PremiumType = PremiumType.NONE
            var avatarDecoration: String? = null

            decoder.makeStructure(descriptor) { index ->
                when (index) {
                    0 -> id = decodeStringElement(descriptor, index)
                    1 -> username = decodeStringElement(descriptor, index)
                    2 -> discriminator = decodeStringElement(descriptor, index)
                    3 -> globalName = decodeNullableString(descriptor, index)
                    4 -> avatar = decodeNullableString(descriptor, index)
                    5 -> bot = decodeBooleanElement(descriptor, index)
                    6 -> system = decodeBooleanElement(descriptor, index)
                    7 -> mfaEnabled = decodeNullableBoolean(descriptor, index)
                    8 -> banner = decodeNullableString(descriptor, index)
                    9 -> accentColor = decodeNullableInt(descriptor, index)
                    10 -> locale = decodeSerializableElement(descriptor, index, DiscordLocale.Serializer)
                    11 -> verified = decodeBooleanElement(descriptor, index)
                    12 -> email = decodeNullableString(descriptor, index)
                    13 -> flags = decodeSerializableElement(descriptor, index, DiscordUserFlags.SetSerializer)
                    14 -> premiumType = decodeSerializableElement(descriptor, index, PremiumType.Serializer)
                    15 -> publicFlags = decodeSerializableElement(descriptor, index, DiscordUserFlags.SetSerializer)
                    16 -> avatarDecoration = decodeNullableString(descriptor, index)
                }
            }

            val result = DiscordUser(id!!, username!!, discriminator!!)
            result.apply {
                this.globalName = globalName; this.avatar = avatar
                this.bot = bot; this.system = system
                this.mfaEnabled = mfaEnabled
                this.banner = banner

                this.accentColor = accentColor
                this.locale = locale
                verified?.let { this.verified = verified!! }
                this.email = email

                this.flags.addAll(flags)
                this.publicFlags.addAll(publicFlags)

                this.premiumType = premiumType
                this.avatarDecoration = avatarDecoration
            }

            return result
        }

        override fun serialize(encoder: Encoder, value: DiscordUser) {
            encoder.beginStructure(descriptor).run {
                encodeStringElement(descriptor, 0, value.id)
                encodeStringElement(descriptor, 1, value.username)
                encodeStringElement(descriptor, 2, value.discriminator)
                encodeNullableString(descriptor, 3, value.globalName)
                encodeNullableString(descriptor, 4, value.avatar)
                encodeBooleanElement(descriptor, 5, value.bot)
                encodeBooleanElement(descriptor, 6, value.system)
                value.mfaEnabled?.let { encodeBooleanElement(descriptor, 7, value.mfaEnabled!!) }
                value.banner?.let { encodeStringElement(descriptor, 8, value.banner!!) }
                value.accentColor?.let { encodeIntElement(descriptor, 9, value.accentColor!!) }
                value.locale?.let { encodeSerializableElement(descriptor, 10, DiscordLocale.Serializer, value.locale!!) }
                encodeBooleanElement(descriptor, 11, value.verified)
                value.email?.let { encodeStringElement(descriptor, 12, value.email!!) }
                if (value.flags.isNotEmpty()) encodeSerializableElement(descriptor, 13, DiscordUserFlags.SetSerializer, value.flags)
                if (value.premiumType != PremiumType.NONE) encodeSerializableElement(descriptor, 14, PremiumType.Serializer, value.premiumType)
                if (value.publicFlags.isNotEmpty()) encodeSerializableElement(descriptor, 15, DiscordUserFlags.SetSerializer, value.publicFlags)
                value.avatarDecoration?.let { encodeStringElement(descriptor, 16, value.avatarDecoration!!) }

                endStructure(descriptor)
            }
        }
    }

    override fun toString(): String {
        return "DiscordUser(id='$id', username='$username', discriminator='$discriminator', globalName=$globalName, avatar=$avatar, bot=$bot, system=$system, mfaEnabled=$mfaEnabled, banner=$banner, accentColor=$accentColor, locale=$locale, verified=$verified, email=$email, flags=$flags, publicFlags=$publicFlags, premiumType=$premiumType, avatarDecoration=$avatarDecoration)"
    }
}