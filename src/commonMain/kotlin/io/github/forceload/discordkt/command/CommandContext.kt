package io.github.forceload.discordkt.command

import io.github.forceload.discordkt.network.RequestUtil
import io.github.forceload.discordkt.type.DiscordChannel
import io.github.forceload.discordkt.type.DiscordUser
import io.github.forceload.discordkt.type.channel.DiscordMessage
import io.github.forceload.discordkt.type.channel.MessageFlag
import io.github.forceload.discordkt.type.gateway.event.dispatch.DiscordInteraction
import io.github.forceload.discordkt.type.gateway.event.dispatch.InteractionCallbackData
import io.github.forceload.discordkt.type.gateway.event.dispatch.InteractionCallbackType
import io.github.forceload.discordkt.type.gateway.event.dispatch.InteractionCallbackType.CHANNEL_MESSAGE_WITH_SOURCE
import io.github.forceload.discordkt.type.gateway.event.dispatch.InteractionResponse
import io.github.forceload.discordkt.type.gateway.event.dispatch.interaction.callback.InteractionMessageCallback
import io.github.forceload.discordkt.util.CoroutineScopes
import io.github.forceload.discordkt.util.SerializerUtil
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString

@Suppress("JoinDeclarationAndAssignment", "MemberVisibilityCanBePrivate", "CanBeParameter")
class CommandContext(
    val arguments: HashMap<String, Any>,
    private val interaction: DiscordInteraction,
    private val token: String,
) {
    val message: DiscordMessage?
    val channel: DiscordChannel?
    val user: DiscordUser?

    private val interactionCallback: String

    init {
        message = interaction.message
        channel = interaction.channel
        user = interaction.user ?: interaction.member?.user
        user?.auth = token

        interactionCallback = "interactions/${interaction.id}/${interaction.token}/callback"
    }

    var autoResponse = true
    var reactionTimestamp = -1L

    @Suppress("DeferredResultUnused")
    fun responseAsync(interactionResponse: InteractionResponse) {
        reactionTimestamp = Clock.System.now().toEpochMilliseconds()
        val message = SerializerUtil.jsonBuild.encodeToString<InteractionResponse>(interactionResponse)

        CoroutineScopes.httpScope.async { RequestUtil.post(interactionCallback, token, message) }
    }

    inline fun responseAsync(type: InteractionCallbackType, data: InteractionCallbackData? = null) =
        responseAsync(InteractionResponse(type, data))

    fun response(interactionResponse: InteractionResponse) = runBlocking {
        reactionTimestamp = Clock.System.now().toEpochMilliseconds()
        val message = SerializerUtil.jsonBuild.encodeToString<InteractionResponse>(interactionResponse)

        RequestUtil.post(interactionCallback, token, message)
    }

    fun response(type: InteractionCallbackType, data: InteractionCallbackData? = null) =
        response(InteractionResponse(type, data))

    fun reply(text: String, flags: Set<MessageFlag> = setOf(), async: Boolean = true) {
        if (async) responseAsync(CHANNEL_MESSAGE_WITH_SOURCE, InteractionMessageCallback(content = text, flags = flags))
        else response(CHANNEL_MESSAGE_WITH_SOURCE, InteractionMessageCallback(content = text, flags = flags))
    }

    fun reply(text: String, vararg flags: MessageFlag) = reply(text, flags.toSet(), false)

    @Suppress("DeferredResultUnused")
    fun async(code: () -> Unit) { CoroutineScopes.commandScope.async { code() } }
}