package io.github.forceload.discordkt.command.internal

import io.github.forceload.discordkt.type.DiscordLocale

enum class ApplicationCommandType(val id: Int) {
    CHAT_INPUT(1), USER(2), MESSAGE(3);

    companion object {
        fun fromID(id: Int) = entries.first { it.id == id }
    }
}

/**
 * https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-structure
 */
class DiscordCommand(
    val id: String, val appId: String,
    val name: String, val description: String, val version: String
) {
    var type = ApplicationCommandType.CHAT_INPUT

    lateinit var guildID: String
    val nameLocalizations = HashMap<DiscordLocale, String>()
    val descriptionLocalizations = HashMap<DiscordLocale, String>()

    class ApplicationCommandOption

    /**
     * https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-option-structure
     */
    var options = ArrayList<ApplicationCommandOption>()
}