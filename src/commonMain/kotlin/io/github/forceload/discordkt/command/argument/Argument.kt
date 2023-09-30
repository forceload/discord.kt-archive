package io.github.forceload.discordkt.command.argument

import io.github.forceload.discordkt.command.internal.DiscordCommand.ApplicationCommandOption.ApplicationCommandOptionChoice
import io.github.forceload.discordkt.command.internal.type.ValueType
import io.github.forceload.discordkt.exception.InvalidArgumentTypeException
import io.github.forceload.discordkt.type.*
import io.github.forceload.discordkt.util.CollectionUtil.add
import kotlin.jvm.JvmName

data class Argument(
    val name: String, val description: String = "Default Description",
    val choice: ArrayList<ApplicationCommandOptionChoice> = ArrayList(),
    val nameLocalizations: LocalizationMap = HashMap(),
    val descriptionLocalizations: LocalizationMap = HashMap(),
) {
    constructor(other: Argument): // Copy of other argument
        this(other.name, other.description, other.choice, other.nameLocalizations, other.descriptionLocalizations)

    fun addChoice(name: String, value: Any): ApplicationCommandOptionChoice {
        val choice = ApplicationCommandOptionChoice(name, value = ValueType(value))
        this.choice.add(choice)
        return choice
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <A> identifyType(type: A): ArgumentType<A> = when (type) {
            is String -> DiscordString(false)
            is String.Companion -> DiscordString(false)

            is Int -> DiscordInteger(false)
            is Int.Companion -> DiscordInteger(false)

            is URLFile -> DiscordAttachment(false)
            is URLFile.Companion -> DiscordAttachment(false)

            is ArgumentType<*> -> type
            else -> try { throw InvalidArgumentTypeException(type!!::class.simpleName) }
            catch (err: NullPointerException) { throw Exception("Type is null") }
        } as ArgumentType<A>
    }
}

// Argument Description
inline infix fun String.desc(other: String) = this description other
inline infix fun String.description(other: String) = Argument(this, other)

private typealias argumentPair<A> = Pair<Argument, ArgumentType<A>>
// Argument Property (like Choices...)
inline infix fun <A> Pair<Argument, ArgumentType<A>>.prop(function: Argument.() -> Unit): argumentPair<A> =
    this property function

inline infix fun <A> Pair<Argument, ArgumentType<A>>.property(function: Argument.() -> Unit): argumentPair<A> =
    Pair(Argument(this.first).also(function), this.second)

@JvmName("prop2")
inline infix fun <A> Pair<Argument, A>.prop(function: Argument.() -> Unit): argumentPair<A> =
    this property function

@Suppress("UNCHECKED_CAST") @JvmName("property2")
inline infix fun <A> Pair<Argument, A>.property(function: Argument.() -> Unit): argumentPair<A> =
    Pair(this.first, Argument.identifyType(this.second as Any) as ArgumentType<A>) prop function // Convert to Argument

@JvmName("prop3")
inline infix fun <A> Pair<String, A>.prop(function: Argument.() -> Unit): argumentPair<A> = this property function

@JvmName("property3")
inline infix fun <A> Pair<String, A>.property(function: Argument.() -> Unit): argumentPair<A> =
    Pair(Argument(this.first), this.second) prop function

private typealias localizationPair = Pair<DiscordLocale, String>

// Argument(Command Option) Name Localization for Pair<String, ArgumentType.Companion>
@JvmName("localName2")
inline fun <A> Pair<String, ArgumentType<A>>.localName(vararg args: localizationPair) =
    Pair(Argument(this.first), this.second).localName(*args)

// Argument(Command Option) Name Localization for Pair<String, Any>
@Suppress("UNCHECKED_CAST") @JvmName("localName3")
fun <A> Pair<String, A>.localName(vararg pair: Pair<DiscordLocale, String>): Pair<Argument, ArgumentType<A>> {
    val newArg = Argument(this.first).apply { localName(*pair) }
    return Pair(newArg, Argument.identifyType(this.second as Any) as ArgumentType<A>)
}

// Argument(Command Option) Localizations for Pair<Argument, ArgumentType>
inline fun <A> argumentPair<A>.localName(vararg args: localizationPair): argumentPair<A> =
    localizeName(*args)
inline fun <A> argumentPair<A>.localDesc(vararg args: localizationPair): argumentPair<A> =
    localDescription(*args)
inline fun <A> argumentPair<A>.localDescription(vararg args: localizationPair): argumentPair<A> =
    localizeDescription(*args)

inline fun <A> argumentPair<A>.localizeName(vararg args: localizationPair): argumentPair<A> =
    this.apply { this.first.localName(*args) }

inline fun <A> argumentPair<A>.localizeDescription(vararg args: localizationPair): argumentPair<A> =
    this.apply { this.first.localDesc(*args) }

// Argument(Command Option) Localizations for Argument
inline fun Argument.localName(vararg args: localizationPair) =
    localizeName(*args)
inline fun Argument.localDesc(vararg args: localizationPair) =
    localDescription(*args)
inline fun Argument.localDescription(vararg args: localizationPair) =
    localizeDescription(*args)

fun Argument.localizeName(vararg args: localizationPair): Argument {
    args.forEach { pair -> this.nameLocalizations.add(pair) }
    return this
}

fun Argument.localizeDescription(vararg args: localizationPair): Argument {
    args.forEach { pair -> this.descriptionLocalizations.add(pair) }
    return this
}

// Command Option Choice Localizations
inline fun ApplicationCommandOptionChoice.local(vararg args: Pair<DiscordLocale, String>) =
    this.localization(*args)

fun ApplicationCommandOptionChoice.localization(vararg args: Pair<DiscordLocale, String>): ApplicationCommandOptionChoice {
    args.forEach { pair -> this.nameLocalizations.add(pair) }
    return this
}