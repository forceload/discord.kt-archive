package io.github.forceload.discordkt

import io.github.forceload.discordkt.command.argument.with
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
                    "amount" with "Amount of message" to Int.require,
                    "message" with "Message to send" to String.require
                )

                description = "Send Direct Message to User"
                execute {
                    println(arguments["message"])
                }
            }
        }.run()
    }
}