package io.github.discordkt.discordkt.discord

import io.github.discordkt.discordkt.client.http.RequestUtil
import io.github.discordkt.discordkt.discord.wrapper.WrapperStorage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

object APIRequester {

    fun getRequest(url: String) = RequestUtil.request(
        "https://discord.com/api/v10/${url}", header = WrapperStorage.discordBot.authHeader
    )

    fun postRequest(url: String, params: Map<*, *>): JsonObject = RequestUtil.request(
        "https://discord.com/api/v10/${url}", header = WrapperStorage.discordBot.authHeader, method = "POST",
        contentType = "application/json", additionalParams = WrapperStorage.gson.toJson(params)
    )

    fun deleteRequest(url: String) = RequestUtil.request(
        "https://discord.com/api/v10/${url}", header = WrapperStorage.discordBot.authHeader, method = "DELETE"
    )

}
