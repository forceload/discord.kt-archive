# discord.kt
Unofficial Discord Kotlin API

## Example
Doesn't Work Yet
```kotlin
fun main() {
    bot {
        command("ping") {
            execute {
                reply("Pong", mention = false)
            }
        }
    }
}
```