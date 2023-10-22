package io.github.forceload.discordkt.command

import io.github.forceload.discordkt.type.DiscordChannel
import io.github.forceload.discordkt.type.channel.DiscordMessage

class CommandContext(
    val arguments: HashMap<String, Any>,
    val channel: DiscordChannel?,
    val message: DiscordMessage?
) {

}