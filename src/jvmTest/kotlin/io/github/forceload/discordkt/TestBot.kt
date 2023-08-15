package io.github.forceload.discordkt

import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class TestBot {
    @Test
    fun runTest() = runBlocking {
        bot(
            credentials = DiscordBotCredentials(
                id = System.getenv("DISCORD_KT_TEST_USERID"),
                token = System.getenv("DISCORD_KT_TEST_TOKEN")
            ),
            debug = true
        ) {
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
