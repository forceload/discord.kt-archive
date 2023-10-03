package io.github.forceload.discordkt

import io.github.forceload.discordkt.command.argument.*
import io.github.forceload.discordkt.type.DiscordLocale
import io.github.forceload.discordkt.type.URLFile
import io.github.forceload.discordkt.type.gateway.PresenceStatus
import io.github.forceload.discordkt.type.require

suspend fun main() {
    bot(debug = true) {
        id = System.getenv("DISCORD_KT_TEST_USERID")
        token = System.getenv("DISCORD_KT_TEST_TOKEN")
        status = PresenceStatus.ONLINE

        command("direct_message") {
            arguments(
                ("message" desc "Message to Send" to String.require prop {
                    addChoice("Hello", "Hello").local(DiscordLocale.ko_KR to "인삿말")
                }).localDescription(DiscordLocale.ko_KR to "보낼 메시지")
                    .localName(DiscordLocale.ko_KR to "메시지")
            )

            description = "Send Direct Message to User"
            execute {
                println(arguments["message"])
            }
        }

        command("attachment_test") {
            arguments(
                ("attachment" desc "Attachment to Test" to URLFile)
                    .localName(DiscordLocale.ko_KR to "첨부파일")
                    .localDesc(DiscordLocale.ko_KR to "테스트할 첨부파일")
            )

            description = ""
        }

        command("shutdown") {
            this@bot.stop()
        }
    }.runBlocking()
}


