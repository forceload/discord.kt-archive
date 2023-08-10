package io.github.forceload.discordkt

import io.github.forceload.discordkt.util.EnvUtil
import kotlin.test.Test

class TestBot {
    @Test
    fun runTest() {
        bot {
            id = EnvUtil.getEnv("ID").toLong()

            command("ping") {
                arguments("hello" to Int)

                execute {
                    arguments["hello"]
                }
            }
        }
    }
}
