package io.github.discordkt.discordkt.discord.wrapper

import com.google.gson.Gson
import io.github.discordkt.discordkt.discord.internal.DiscordBot
import kotlinx.serialization.json.Json

object WrapperStorage {
    val gson = Gson()
    val json = Json { encodeDefaults = true }
    lateinit var discordBot: DiscordBot

    val userChannel: MutableMap<String, DiscordChannel> = HashMap()
}
