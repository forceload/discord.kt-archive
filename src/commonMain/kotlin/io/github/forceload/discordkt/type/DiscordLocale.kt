package io.github.forceload.discordkt.type

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * https://discord.com/developers/docs/reference#locales
 */
@Suppress("EnumEntryName")
@Serializable(with = DiscordLocale.Serializer::class)
enum class DiscordLocale(val localeID: String, name: String) {
    id_ID("id", "Indonesian"),
    da_DK("da", "Danish"),
    de_DE("de", "German"),
    en_GB("en-GB", "English (United Kingdom)"),
    en_US("en-US", "English (United States)"),
    es_ES("es-ES", "Spanish"),
    fr_FR("fr", "French"),
    hr_HR("hr", "Croatian"),
    it_IT("it", "Italian"),
    lt_LT("lt", "Lithuanian"),
    hu_HU("hu", "Hungarian"),
    nl_NL("nl", "Dutch"),
    no_NO("no", "Norwegian"),
    pl_PL("pl", "Polish"),
    pt_BR("pt-BR", "Portuguese (Brazilian)"),
    ro_RO("ro", "Romanian (Romania)"),
    fi_FI("fi", "Finnish"),
    sv_SE("sv-SE", "Swedish"),
    vi_VN("vi", "Vietnamese"),
    tr_TR("tr", "Turkish"),
    cs_CZ("cs", "Czech"),
    el_GR("el", "Greek"),
    bg_BG("bg", "Bulgarian"),
    ru_RU("ru", "Russian"),
    uk_UA("uk", "Ukrainian"),
    hi_IN("hi", "Hindi"),
    th_TH("th", "Thai"),
    zh_CN("zh-CN", "Chinese (China)"),
    ja_JP("ja", "Japanese"),
    zh_TW("zh-TW", "Chinese (Taiwan)"),
    ko_KR("ko", "Korean");

    companion object {
        fun fromID(id: String) = entries.first { it.localeID == id }
        val localizationSerializer = MapSerializer(Serializer, String.serializer())
    }

    object Serializer: KSerializer<DiscordLocale> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("DiscordLocale", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder) =
            DiscordLocale.fromID(decoder.decodeString())

        override fun serialize(encoder: Encoder, value: DiscordLocale) =
            encoder.encodeString(value.localeID)
    }
}

typealias LocalizationMap = HashMap<DiscordLocale, String>