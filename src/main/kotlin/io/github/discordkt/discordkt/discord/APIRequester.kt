package io.github.discordkt.discordkt.discord

import io.github.discordkt.discordkt.client.http.RequestUtil
import io.github.discordkt.discordkt.discord.wrapper.WrapperStorage
import io.github.discordkt.discordkt.serializer.AnySerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

object APIRequester {

    fun getRequest(url: String) = RequestUtil.request(
        "https://discord.com/api/v10/${url}", header = WrapperStorage.discordBot.authHeader
    )

    fun postRequest(url: String, params: Map<String, Any>): JsonObject {
        println(Json.encodeToString(AnySerializer, params))

        return RequestUtil.request(
            "https://discord.com/api/v10/${url}", header = WrapperStorage.discordBot.authHeader, method = "POST",
            contentType = "application/json", additionalParams = Json.encodeToString(AnySerializer, params)
        )

        /*return RequestUtil.request(
            "https://discord.com/api/v10/${url}", header = WrapperStorage.discordBot.authHeader, method = "POST",
            contentType = "application/json", additionalParams = WrapperStorage.gson.toJson(params)
        )*/
    }

    fun deleteRequest(url: String) = RequestUtil.request(
        "https://discord.com/api/v10/${url}", header = WrapperStorage.discordBot.authHeader, method = "DELETE"
    )

}
