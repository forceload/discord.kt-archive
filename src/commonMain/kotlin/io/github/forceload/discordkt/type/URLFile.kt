package io.github.forceload.discordkt.type

import io.github.forceload.discordkt.network.RequestUtil

data class URLFile(val url: String, val proxyURL: String? = null) {
    companion object
    fun download() = RequestUtil.getRaw(url)
    val extension = url.split(".").last().takeWhile { it != '?' }
}