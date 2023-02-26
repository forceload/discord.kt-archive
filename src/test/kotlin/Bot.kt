import io.github.teamcrez.discordkt.discord.DiscordClient
import io.github.teamcrez.discordkt.discord.api.DiscordFlags
import io.github.teamcrez.discordkt.discord.collections.DiscordChoiceMap
import io.github.teamcrez.discordkt.discord.wrapper.CommandArgument

fun main() {
    val bot = TestBot()
    bot.activate()
}

class TestBot: DiscordClient() {
    override fun activate(): Boolean {
        bot(debug = false) {
            id = System.getenv("DISCORD_KT_TEST_USERID")
            token = System.getenv("DISCORD_KT_TEST_TOKEN")
            intentFlag = 8

            commands {
                command("direct_message", args = mapOf(
                    "message" to CommandArgument(
                        DiscordFlags.CommandArgumentType.STRING,
                        description = "Message"
                    )
                ), description = "Send DM to the user itself") {
                    this.context.interaction.reply("DM이 전송되었습니다", DiscordFlags.MessageFlag.EPHEMERAL)
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
                    this.context.interaction.reply("DM이 전송되었습니다", DiscordFlags.MessageFlag.EPHEMERAL)
                    this.context.user.directMessage(this.args["message"])
                }
            }
        }

        return true
    }
}
