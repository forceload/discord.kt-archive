package io.github.forceload.discordkt

import io.github.forceload.discordkt.command.argument.*
import io.github.forceload.discordkt.type.DiscordLocale
import io.github.forceload.discordkt.type.require
import kotlin.test.Test

class TestBot {
    @Test
    fun runTest() {
        bot(debug = true) {
            id = System.getenv("DISCORD_KT_TEST_USERID")
            token = System.getenv("DISCORD_KT_TEST_TOKEN")

            command("direct_message") {
                arguments(
                    "amount" desc "Amount of message" to Int.require,
                    ("message" desc "Message to send" to String.require prop {
                        addChoice("Hello", "Hello").local(DiscordLocale.ko_KR to "인삿말")
                    }).localDescription(DiscordLocale.ko_KR to "보낼 메시지")
                    .localName(DiscordLocale.ko_KR to "메시지")
                )

                description = "Send Direct Message to User"
                execute {
                    println(arguments["message"])
                }
            }
        }.run()
    }
}


