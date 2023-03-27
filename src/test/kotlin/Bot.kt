import io.github.discordkt.discordkt.discord.DiscordClient
import io.github.discordkt.discordkt.discord.api.DiscordFlags
import io.github.discordkt.discordkt.discord.collections.DiscordChoiceMap
import io.github.discordkt.discordkt.discord.wrapper.CommandArgument

fun main() {
    val bot = TestBot()
    bot.activate()
}

class TestBot: DiscordClient() {
    override fun activate(): Boolean {
        bot(debug = true) {
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

                command("attachment_test", args = mapOf(
                    "attachment" to CommandArgument(
                        DiscordFlags.CommandArgumentType.ATTACHMENT,
                        description = "Attachment"
                    )
                ), description = "Send DM to the user itself") {
                    this.context.interaction.reply("DM이 전송되었습니다", DiscordFlags.MessageFlag.EPHEMERAL)
                    this.context.user.directMessage(this.args["attachment"])
                }

                command("mentionable_test", args = mapOf(
                    "role" to CommandArgument(
                        DiscordFlags.CommandArgumentType.MENTIONABLE,
                        description = "Role"
                    )
                )) {
                    this.context.interaction.reply(this.args["role"], DiscordFlags.MessageFlag.EPHEMERAL)
                }
            }
        }

        return true
    }
}
