package io.github.teamcrez.discordkt.discord

import io.github.teamcrez.discordkt.discord.internal.DiscordBot
import io.github.teamcrez.discordkt.discord.internal.gateway.GatewayListener
import io.github.teamcrez.discordkt.discord.internal.gateway.GatewayStorage
import io.github.teamcrez.discordkt.discord.wrapper.WrapperStorage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

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
            val gatewayListener = GatewayListener(clientSession!!)
            while (!gatewayListener.isDisabled) {
                gatewayListener.run()
                while (gatewayListener.isRunning && isActive) { delay(1) }
            }
        }
    }

    open fun activate(): Boolean = true

    var debug: Boolean = false
    fun bot(debug: Boolean = false, init: DiscordBot.() -> Unit) {
        this.debug = debug
        GatewayStorage.gatewayDebug = debug

        discordBot = DiscordBot(this)
        WrapperStorage.discordBot = discordBot

        discordBot.init()
    }
}
