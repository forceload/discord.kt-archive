package io.github.forceload.discordkt.command

import io.github.forceload.discordkt.command.internal.CommandSerializer
import io.github.forceload.discordkt.command.internal.DiscordCommand
import io.github.forceload.discordkt.command.internal.type.ApplicationCommandOptionType
import io.github.forceload.discordkt.util.SerializerUtil
import org.junit.jupiter.api.Test
import kotlin.time.measureTime

object CommandTest {
    @Test
    @Suppress("DEPRECATION")
    fun commandSerializingTest() {
        val stringCommand = "{\"id\":\"111111\",\"application_id\":\"222222\",\"version\":\"333333\",\"default_member_permissions\":null,\"type\":1,\"name\":\"direct_message\",\"description\":\"Send DM to the user itself\",\"dm_permission\":true,\"contexts\":null,\"integration_types\":[0],\"options\":[{\"type\":3,\"name\":\"message\",\"description\":\"Message\",\"required\":true}],\"nsfw\":false}"
        val originalCommand = DiscordCommand("999999", "444444", "direct_message", "Send DM to the user itself", "123456")
        originalCommand.options.add(DiscordCommand.ApplicationCommandOption(
            ApplicationCommandOptionType.STRING, "message", "Message", true
        ))
        originalCommand.defaultPermission = true

        val parsedCommand: DiscordCommand

        CommandSerializer.descriptor // Preload CommandSerializer And Descriptor
        SerializerUtil.commandOptionMaxDepth // Preload SerializerUtil

        val duration = measureTime {
            parsedCommand = SerializerUtil.jsonBuild.decodeFromString<DiscordCommand>(stringCommand)
        }

        println("Command Parsing Duration: $duration")
        assert(parsedCommand == originalCommand) // Check Serialized Command
        assert(duration.inWholeMilliseconds <= 50) // Check Performance
    }
}