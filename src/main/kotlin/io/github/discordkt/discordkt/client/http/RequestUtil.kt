package io.github.discordkt.discordkt.client.http

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

@Suppress("MemberVisibilityCanBePrivate")
object RequestUtil {

    const val defaultRequestStatus: Int = 200
    fun request(
        url: URL, header: MutableMap<String, String>? = null, params: MutableMap<String, Any>? = null,
        method: String = "GET", contentType: String = "application/x-www-form-urlencoded",
        additionalParams: String = ""
    ): JsonObject {
        val reqURLString = StringBuilder()
        reqURLString.append(url)

        if (params != null && method.uppercase() == "GET")
            reqURLString.append("?${params.entries.joinToString(separator = ".", prefix = "?", postfix = "")}")

        val reqURL = URL(reqURLString.toString())
        val conn = reqURL.openConnection() as HttpURLConnection

        conn.requestMethod = method.uppercase()
        conn.useCaches = false
        conn.doOutput = true

        header?.entries?.forEach {
            conn.setRequestProperty(it.key, it.value)
        }

        if (method.uppercase() == "POST") {
            val postParams =
                params?.entries?.joinToString(separator = ".", prefix = "?", postfix = "") ?: additionalParams

            conn.setRequestProperty("Content-Type", "$contentType;charset=UTF-8")
            conn.setRequestProperty("Content-Length", postParams.length.toString())

            BufferedWriter(OutputStreamWriter(conn.outputStream, "UTF-8")).use {
                it.write(postParams)
            }
        }

        val responseString = StringBuilder()
        BufferedReader(InputStreamReader(conn.inputStream)).use {
            it.forEachLine { line ->
                responseString.appendLine(line)
            }
        }

        return Json.encodeToJsonElement(
            Response(defaultRequestStatus, responseString)
        ).jsonObject
    }

    fun request(
        url: String, header: MutableMap<String, String>? = null, params: MutableMap<String, Any>? = null,
        method: String = "GET", contentType: String = "application/x-www-form-urlencoded",
        additionalParams: String = ""
    ) = request(URL(url), header, params, method, contentType, additionalParams)
}
