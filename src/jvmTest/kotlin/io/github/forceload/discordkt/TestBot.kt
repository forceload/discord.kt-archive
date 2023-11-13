package io.github.forceload.discordkt

import io.github.forceload.discordkt.command.argument.desc
import io.github.forceload.discordkt.command.argument.localDesc
import io.github.forceload.discordkt.command.argument.localName
import io.github.forceload.discordkt.type.DiscordLocale
import io.github.forceload.discordkt.type.URLFile
import io.github.forceload.discordkt.type.channel.DiscordChannelType
import io.github.forceload.discordkt.type.channel.MessageFlag.*
import io.github.forceload.discordkt.type.commands.require
import io.github.forceload.discordkt.type.gateway.PresenceStatus
import io.github.forceload.discordkt.type.require
import io.github.forceload.discordkt.util.CoroutineUtil.delay
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.test.Test

val ADMIN_IDs = arrayOf("control_delta")

class TestBot {
    val defaultTestBot
        get() = bot(debug = true) {
            id = System.getenv("DISCORD_KT_TEST_USERID")
            token = System.getenv("DISCORD_KT_TEST_TOKEN")
            status = PresenceStatus.ONLINE

            command("direct_message") {
                arguments(
                    ("message" desc "Message to Send" to String.require)
                        .localName(DiscordLocale.ko_KR to "메세지")
                        .localDesc(DiscordLocale.ko_KR to "보낼 메세지")
                )

                description = "Send Direct Message to User"
                execute {
                    if (this.channel?.type?.isNotDM() == true) this.reply("DM이 전송되었습니다", EPHEMERAL)
                    user?.directMessage(arguments["message"] as String)
                }
            }

            command("attachment_test") {
                arguments(
                    ("attachment" desc "Attachment to Test" to Attachment.require)
                        .localName(DiscordLocale.ko_KR to "첨부파일")
                        .localDesc(DiscordLocale.ko_KR to "테스트할 첨부파일")
                )

                description = ""
                execute {
                    val urlFile = arguments["attachment"] as URLFile

                    reply("첨부 파일이 전송되었습니다", EPHEMERAL)
                    val content = urlFile.download()

                    val file = File("downloaded.${urlFile.extension}")
                    if (!file.exists()) file.createNewFile()
                    file.writeBytes(content)

                    println("첨부 파일 다운로드 완료")
                }
            }

            command("shutdown") {
                execute {
                    val isGuildTextChannel = this.channel?.type == DiscordChannelType.GUILD_TEXT

                    if (isGuildTextChannel && ADMIN_IDs.contains(this.user?.username)) {
                        reply("Discord.kt Test 봇이 종료됩니다...", async = false)
                        this@bot.stop("강제 종료")
                    } else {
                        this.reply(
                            """# 이 명령어를 실행할 수 없습니다
                            |명령어를 실행할 수 없는 이유는 다음 중 하나입니다
                            |- 이 명령어를 실행할 권한이 없음
                            |- 이 명령어를 실행하기에 올바른 채널 유형이 아님
                        """.trimMargin(), if (this.channel?.type?.isDM() == true)
                                setOf() else setOf(EPHEMERAL), false
                        )
                    }
                }
            }

            command("switch_status") {
                execute {
                    if (ADMIN_IDs.contains(this.user?.username)) {
                        this.reply("봇의 상태를 전환시킵니다...")
                        status =
                            if (status == PresenceStatus.ONLINE) PresenceStatus.DO_NOT_DISTURB else PresenceStatus.ONLINE
                    }
                }
            }
        }

    @Test
    fun runBot() {
        val bot = defaultTestBot
        bot.runBlocking()
    }

    @Test
    fun botPair() = runBlocking {
        val firstBot = defaultTestBot
        val secondBot = bot(debug = true) {
            id = System.getenv("MAILBOT_ID")
            token = System.getenv("MAILBOT_TOKEN")

            command("send_dm") {
                arguments("message" to String.require)

                execute {
                    this.user!!.directMessage(arguments["message"] as String)
                }
            }

            command("shutdown") {
                execute {
                    val isGuildTextChannel = this.channel?.type == DiscordChannelType.GUILD_TEXT

                    if (isGuildTextChannel && ADMIN_IDs.contains(this.user?.username)) {
                        this.reply("편지봇이 종료됩니다...", async = false)
                        this@bot.stop("강제 종료")
                    } else {
                        this.reply(
                            """# 이 명령어를 실행할 수 없습니다
                            |명령어를 실행할 수 없는 이유는 다음 중 하나입니다
                            |- 이 명령어를 실행할 권한이 없음
                            |- 이 명령어를 실행하기에 올바른 채널 유형이 아님
                        """.trimMargin(), if (this.channel?.type?.isDM() == true)
                                setOf() else setOf(EPHEMERAL), false
                        )
                    }
                }
            }
        }

        firstBot.run()
        secondBot.run()

        while (firstBot.running || secondBot.running) delay(100)
    }
}