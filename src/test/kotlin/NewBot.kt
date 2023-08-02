import io.github.forceload.discordkt.bot.*
import io.github.forceload.discordkt.type.argument.StringArgument

fun main() {
    bot(debug = true) {
        id = System.getenv("DISCORD_KT_TEST_USERID")
        token = System.getenv("DISCORD_KT_TEST_TOKEN")

        command("ping") {
            description = "Ping!"
            arguments (
                "message" to StringArgument(),
            )

            execute {

            }
        }
    }.run()
}