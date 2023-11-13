package io.github.forceload.discordkt.command

import io.github.forceload.discordkt.command.argument.Argument
import io.github.forceload.discordkt.command.argument.ArgumentType
import io.github.forceload.discordkt.command.internal.DiscordCommand
import io.github.forceload.discordkt.command.internal.type.ApplicationCommandOptionType
import io.github.forceload.discordkt.exception.InvalidArgumentTypeException
import io.github.forceload.discordkt.type.DiscordInteger
import io.github.forceload.discordkt.type.DiscordString
import io.github.forceload.discordkt.type.URLFile
import io.github.forceload.discordkt.type.commands.DiscordAttachmentType
import io.github.forceload.discordkt.type.gateway.event.dispatch.DiscordInteraction
import io.github.forceload.discordkt.type.gateway.event.dispatch.InteractionCallbackType
import io.github.forceload.discordkt.type.gateway.event.dispatch.interaction.ApplicationCommandData
import io.github.forceload.discordkt.type.gateway.event.dispatch.interaction.callback.InteractionMessageCallback
import io.github.forceload.discordkt.util.CoroutineScopes
import io.github.forceload.discordkt.util.DiscordConstants
import io.github.forceload.discordkt.util.logger.DebugLogger
import kotlinx.coroutines.*
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

class CommandNode(var name: String, private val token: String) {
    var description: String = DiscordConstants.defaultDescription
        set(value) { field = value.ifEmpty { DiscordConstants.defaultDescription } }

    private var codes = ArrayList<CommandContext.() -> Unit>()
    private val argumentMap = HashMap<String, Pair<Argument, ArgumentType<*>>>()

    @Suppress("PropertyName")
    val Attachment = URLFile

    fun arguments(vararg args: Pair<Any, Any>) {
        for (argument in args) {
            val newArgument = when (argument.first) {
                is String -> Argument(argument.first as String, "")
                is Argument -> argument.first as Argument

                else -> throw InvalidArgumentTypeException(argument.first::class.qualifiedName)
            }

            argumentMap[newArgument.name] = when (argument.second) {
                is ArgumentType<*> -> Pair(newArgument, argument.second as ArgumentType<*>)
                else -> Pair(newArgument, Argument.identifyType(argument.second) as ArgumentType<*>)
            }
        }

        // DebugLogger.log(argumentMap)
    }

    fun execute(reaction: CommandContext.() -> Unit) {
        codes.add(reaction)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun run(interaction: DiscordInteraction) {
        val arguments = HashMap<String, Any>()
        interaction.data as ApplicationCommandData

        for (option in interaction.data.options) {
            val primitive = option.value!!.jsonPrimitive
            arguments[option.name] = when (option.type) {
                ApplicationCommandOptionType.STRING -> primitive.content
                ApplicationCommandOptionType.INTEGER -> primitive.int
                ApplicationCommandOptionType.ATTACHMENT -> {
                    val attachment = interaction.data.resolved!!.attachments[primitive.content]!!
                    URLFile(attachment.url, attachment.proxyURL)
                }

                else -> { }
            }
        }

        if (arguments.isNotEmpty()) DebugLogger.log(arguments)

        val context = CommandContext(arguments, interaction, token)
        CoroutineScopes.commandScope.launch {
            val asyncCallList = ArrayList<Deferred<Unit>>()
            for (code in codes) { asyncCallList.add(async { code(context) }) }

            for (call in asyncCallList) { call.await() }
            if (context.reactionTimestamp == -1L && context.autoResponse) context.response(
                InteractionCallbackType.CHANNEL_MESSAGE_WITH_SOURCE,
                InteractionMessageCallback(content = "")
            )
        }
    }

    fun generateCommand(): DiscordCommand {
        val result = DiscordCommand(null, null, name, description)
        argumentMap.onEachIndexed { _, entry ->
            val argument = entry.value
            val option = DiscordCommand.ApplicationCommandOption(
                when (argument.second) {
                    is DiscordString -> ApplicationCommandOptionType.STRING
                    is DiscordInteger -> ApplicationCommandOptionType.INTEGER
                    is DiscordAttachmentType -> ApplicationCommandOptionType.ATTACHMENT
                    else -> throw InvalidArgumentTypeException("Argument Type is Invalid")
                },
                entry.key, argument.first.description, argument.second.required
            )

            option.nameLocalizations.putAll(argument.first.nameLocalizations)
            option.descriptionLocalizations.putAll(argument.first.descriptionLocalizations)
            option.choices.addAll(argument.first.choice)

            result.options.add(option)
        }

        return result
    }
}
