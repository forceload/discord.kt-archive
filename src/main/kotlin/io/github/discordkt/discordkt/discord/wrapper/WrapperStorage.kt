package io.github.discordkt.discordkt.discord.wrapper

import com.google.gson.Gson
import io.github.discordkt.discordkt.discord.internal.DiscordBot

object WrapperStorage {
    val gson = Gson()
    lateinit var discordBot: DiscordBot

    val userChannel: MutableMap<String, DiscordChannel> = HashMap()
}
