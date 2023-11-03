# discord.kt
Unofficial Discord Kotlin API

[Official Discord Server](https://discord.gg/B7NTjAjbYY)

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
