import io.github.teamcrez.discordkt.discord.DiscordClient
import io.github.teamcrez.discordkt.discord.api.DiscordFlags
import io.github.teamcrez.discordkt.discord.collections.DiscordChoiceMap
import io.github.teamcrez.discordkt.discord.wrapper.CommandArgument

fun main() {
    TestBot().activate()
}

class TestBot: DiscordClient() {
    override fun activate(): Boolean {
        bot(debug = true) {
            id = System.getenv("DISCORD_KT_TEST_USERID")
            token = System.getenv("DISCORD_KT_TEST_TOKEN")
            intentFlag = 8

            commands {
                command("direct_message", args = mapOf(
                    "message" to CommandArgument(DiscordFlags.CommandArgumentType.STRING, description = "Message")
                ), description = "Send DM to the user itself") {
                    this.context.user.directMessage(this.args["message"])
                }

                command("direct_message_select", args = mapOf(
                    "message" to CommandArgument(
                        DiscordFlags.CommandArgumentType.STRING,
                        description = "Message", choices = DiscordChoiceMap<String>().applyMap(
                            mutableMapOf(
                                "hi" to "hello",
                                "hello" to "hi"
                            )
                        )
                    )
                ), description = "Send DM to the user itself") {
                    this.context.user.directMessage(this.args["message"])
                }
            }
        }

        return true
    }
}