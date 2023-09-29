package io.github.forceload.discordkt.command.argument

import io.github.forceload.discordkt.command.internal.DiscordCommand.ApplicationCommandOption.ApplicationCommandOptionChoice
import io.github.forceload.discordkt.command.internal.type.ValueType
import io.github.forceload.discordkt.type.DiscordLocale
import io.github.forceload.discordkt.type.LocalizationMap
import io.github.forceload.discordkt.util.CollectionUtil.add

data class Argument(
    val name: String, val description: String = "",
    val choice: ArrayList<ApplicationCommandOptionChoice> = ArrayList(),
    val nameLocalizations: LocalizationMap = HashMap(),
    val descriptionLocalizations: LocalizationMap = HashMap(),
) {
    constructor(other: Argument) : this(other.name, other.description, other.choice) // Copy of other argument

    fun addChoice(name: String, value: Any): ApplicationCommandOptionChoice {
        val choice = ApplicationCommandOptionChoice(name, value = ValueType(value))
        this.choice.add(choice)
        return choice
    }
}

// Argument Description
inline infix fun String.desc(other: String) = this description other
inline infix fun String.description(other: String) = Argument(this, other)

// Argument Property (like Choices...)
inline infix fun <A> Pair<Argument, ArgumentType<A>>.prop(function: Argument.() -> Unit): Pair<Argument, ArgumentType<A>> =
    this property function

inline infix fun <A> Pair<Argument, ArgumentType<A>>.property(function: Argument.() -> Unit): Pair<Argument, ArgumentType<A>> {
    val newArgument = Argument(this.first).also(function)
    return Pair(newArgument, this.second)
}

private typealias localizationPair = Pair<DiscordLocale, String>

// Argument(Command Option) Localizations for Pair<Argument, ArgumentType>
inline fun <A> Pair<Argument, ArgumentType<A>>.localName(vararg args: localizationPair): Pair<Argument, ArgumentType<A>> =
    localizeName(*args)
inline fun <A> Pair<Argument, ArgumentType<A>>.localDesc(vararg args: localizationPair): Pair<Argument, ArgumentType<A>> =
    localDescription(*args)
inline fun <A> Pair<Argument, ArgumentType<A>>.localDescription(vararg args: localizationPair): Pair<Argument, ArgumentType<A>> =
    localizeDescription(*args)

fun <A> Pair<Argument, ArgumentType<A>>.localizeName(vararg args: localizationPair): Pair<Argument, ArgumentType<A>> {
    args.forEach { pair -> this.first.nameLocalizations.add(pair) }
    return this
}

fun <A> Pair<Argument, ArgumentType<A>>.localizeDescription(vararg args: localizationPair): Pair<Argument, ArgumentType<A>> {
    args.forEach { pair -> this.first.descriptionLocalizations.add(pair) }
    return this
}

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