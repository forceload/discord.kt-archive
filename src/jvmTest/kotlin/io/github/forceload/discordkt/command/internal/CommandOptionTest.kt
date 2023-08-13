package io.github.forceload.discordkt.command.internal

import io.github.forceload.discordkt.command.internal.type.ApplicationCommandOptionType
import io.github.forceload.discordkt.type.DiscordLocale
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CommandHashTest {
    @Test
    fun optionHash() {
        val option1 = DiscordCommand.ApplicationCommandOption(
            ApplicationCommandOptionType.STRING,
            "message", "Message", true
        )

        option1.nameLocalizations[DiscordLocale.ko_KR] = "메세지"
        option1.descriptionLocalizations[DiscordLocale.ko_KR] = "DM을 전송할 메시지입니다"

        val helloOption1 =
            DiscordCommand.ApplicationCommandOption.ApplicationCommandOptionChoice("Hello", "Hi")
        helloOption1.nameLocalizations[DiscordLocale.ko_KR] = "인사 메시지"
        val helloOption2 =
            DiscordCommand.ApplicationCommandOption.ApplicationCommandOptionChoice("WTF", "WTF")
        helloOption2.nameLocalizations[DiscordLocale.ko_KR] = "놀람"

        option1.choices.add(helloOption1)
        option1.choices.add(helloOption2)


        val option2 = DiscordCommand.ApplicationCommandOption(
            ApplicationCommandOptionType.STRING,
            "message", "Message", true
        )

        option2.nameLocalizations[DiscordLocale.ko_KR] = "메세지"
        option2.descriptionLocalizations[DiscordLocale.ko_KR] = "DM을 전송할 메시지입니다"

        option2.choices.add(helloOption1)
        option2.choices.add(helloOption2)

        assertEquals(option1.hashCode(), option2.hashCode())
        option2.nameLocalizations[DiscordLocale.en_US] = "Message"
        assertNotEquals(option1.hashCode(), option2.hashCode())

        val option3 = DiscordCommand.ApplicationCommandOption(
            ApplicationCommandOptionType.STRING,
            "message", "Message", true
        )

        option3.nameLocalizations[DiscordLocale.ko_KR] = "메세지"
        option3.descriptionLocalizations[DiscordLocale.ko_KR] = "DM을 전송할 메시지입니다"

        option3.choices.add(helloOption2)
        option3.choices.add(helloOption1)

        assertNotEquals(option1.hashCode(), option3.hashCode())
        assertNotEquals(option2.hashCode(), option3.hashCode())
    }

    @Test
    fun commandHashTest() {
        val pingCMD1 = DiscordCommand("1", "9999", "ping", "ping", "1")
        pingCMD1.options.add(DiscordCommand.ApplicationCommandOption(
            ApplicationCommandOptionType.STRING,
            "Ping Message", "Ping Message"
        ))

        pingCMD1.defaultMemberPermissions.add(DiscordPermission.USE_APPLICATION_COMMANDS)
        pingCMD1.defaultMemberPermissions.add(DiscordPermission.ADD_REACTIONS)

        val pingCMD2 = DiscordCommand("1", "9999", "ping", "ping", "1")
        pingCMD2.options.add(DiscordCommand.ApplicationCommandOption(
            ApplicationCommandOptionType.STRING,
            "Ping Message", "Ping Message"
        ))

        pingCMD2.defaultMemberPermissions.add(DiscordPermission.ADD_REACTIONS)
        pingCMD2.defaultMemberPermissions.add(DiscordPermission.USE_APPLICATION_COMMANDS)
        assert(pingCMD1 == pingCMD2)

        pingCMD2.defaultMemberPermissions.add(DiscordPermission.ADD_REACTIONS)
        assert(pingCMD1 == pingCMD2)

        pingCMD1.defaultMemberPermissions.add(DiscordPermission.ADMINISTRATOR)
        assert(pingCMD1 != pingCMD2)
    }
}