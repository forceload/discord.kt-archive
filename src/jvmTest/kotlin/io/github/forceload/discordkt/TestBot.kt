package io.github.forceload.discordkt

import io.github.forceload.discordkt.command.argument.desc
import io.github.forceload.discordkt.command.argument.localDesc
import io.github.forceload.discordkt.command.argument.localName
import io.github.forceload.discordkt.type.DiscordLocale
import io.github.forceload.discordkt.type.URLFile
import io.github.forceload.discordkt.type.channel.DiscordChannelType
import io.github.forceload.discordkt.type.channel.MessageFlag
import io.github.forceload.discordkt.type.gateway.PresenceStatus
import io.github.forceload.discordkt.type.require

val ADMIN_IDs = arrayOf("control_delta")

suspend fun main() {
    bot(debug = true) {
        id = System.getenv("DISCORD_KT_TEST_USERID")
        token = System.getenv("DISCORD_KT_TEST_TOKEN")
        status = PresenceStatus.ONLINE

        command("direct_message") {
            arguments(
                "message" desc "Message to Send" to String.require
            )

            description = "Send Direct Message to User"
            execute {
                if (this.channel?.type?.isNotDM() == true) this.reply("DM이 전송되었습니다", MessageFlag.EPHEMERAL)
                user?.directMessage(arguments["message"] as String)
            }
        }

        command("attachment_test") {
            arguments(
                ("attachment" desc "Attachment to Test" to Attachment)
                    .localName(DiscordLocale.ko_KR to "첨부파일")
                    .localDesc(DiscordLocale.ko_KR to "테스트할 첨부파일")
            )

            description = ""
            execute {
                println((arguments["attachment"] as URLFile).url)
            }
        }

        command("shutdown") {
            execute {
                val isGuildTextChannel = this.channel?.type == DiscordChannelType.GUILD_TEXT

                if (isGuildTextChannel && ADMIN_IDs.contains(this.user?.username)) {
                    this.reply("Discord.kt Test 봇이 종료됩니다...")
                    this@bot.stop("강제 종료")
                } else {
                    this.reply(
                        """# 이 명령어를 실행할 수 없습니다
                            |명령어를 실행할 수 없는 이유는 다음 중 하나입니다
                            |- 이 명령어를 실행할 권한이 없음
                            |- 이 명령어를 실행하기에 올바른 채널 유형이 아님
                        """.trimMargin(), if (this.channel?.type?.isDM() == true)
                            setOf() else setOf(MessageFlag.EPHEMERAL)
                    )
                }
            }
        }
    }.runBlocking()
}


