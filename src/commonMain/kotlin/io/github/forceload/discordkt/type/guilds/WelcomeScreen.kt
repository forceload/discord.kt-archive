package io.github.forceload.discordkt.type.guilds

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class WelcomeScreen(
    val description: String?, @SerialName("welcome_channels") val welcomeChannels: Array<WelcomeScreenChannel>
)

@Serializable
class WelcomeScreenChannel(
    @SerialName("channel_id") val channelID: String, val description: String,
    @SerialName("emoji_id") val emojiID: String?, @SerialName("emoji_name") val emojiName: String?
)