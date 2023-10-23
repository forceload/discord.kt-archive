# discord.kt
Unofficial Discord Kotlin API

## Example
It Works!
```kotlin
fun main() {
    bot {
        command("ping") {
            execute { reply("Pong") }
        }
    }
}
```