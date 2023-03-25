package io.github.discordkt.discordkt.discord.types

import io.github.discordkt.discordkt.discord.api.DiscordFlags
import io.github.discordkt.discordkt.discord.wrapper.DiscordAttachment

class DiscordAttachmentType(override val coveredValue: DiscordAttachment): DiscordType<DiscordAttachment> {
    override val type = DiscordFlags.CommandArgumentType.ATTACHMENT
    override fun toString() = this.coveredValue.toString()
}
