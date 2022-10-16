package io.github.teamcrez.discordkt.discord

import io.github.teamcrez.discordkt.discord.internal.DiscordBot
import io.github.teamcrez.discordkt.discord.internal.gateway.GatewayListener
import io.github.teamcrez.discordkt.discord.internal.gateway.GatewayStorage
import io.github.teamcrez.discordkt.discord.internal.gateway.event.GatewayEvent
import io.github.teamcrez.discordkt.discord.wrapper.WrapperStorage
import kotlinx.coroutines.*

open class DiscordClient {
    private var clientSession: DiscordClient? = null
    lateinit var discordBot: DiscordBot

    init {
        processGateway()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun processGateway() {
        clientSession = this

        GlobalScope.launch(Dispatchers.Default) {
            GatewayListener(clientSession!!).run()
        }
    }

    open fun activate(): Boolean = true
    fun bot(init: DiscordBot.() -> Unit) {
        discordBot = DiscordBot(this)
        WrapperStorage.discordBot = discordBot

        discordBot.init()
    }
}