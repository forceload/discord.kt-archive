package io.github.forceload.discordkt

import kotlin.test.Test

class TestBot {
    @Test
    fun runTest() {
        bot(debug = true) {
            id = System.getenv("DISCORD_KT_TEST_USERID")
            token = System.getenv("DISCORD_KT_TEST_TOKEN")

            command("ping") {
                arguments("hello" to Int)
                description = "Just Ping"

                execute {
                    println(arguments["hello"])
                }
            }
        }.run()
    }
}
