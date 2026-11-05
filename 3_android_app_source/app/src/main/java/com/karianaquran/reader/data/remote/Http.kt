package com.karianaquran.reader.data.remote

import java.net.HttpURLConnection
import java.net.URL

/** অতি-সরল HTTPS ক্লায়েন্ট (বাড়তি ডিপেন্ডেন্সি ছাড়া) */
object Http {
    data class Response(val code: Int, val body: String) {
        val ok: Boolean get() = code in 200..299
    }

    fun request(
        url: String,
        method: String = "GET",
        json: String? = null,
        headers: Map<String, String> = emptyMap(),
        timeoutMs: Int = 15_000,
    ): Response {
        require(url.startsWith("https://")) { "শুধু HTTPS অনুমোদিত" }
        val conn = URL(url).openConnection() as HttpURLConnection
        try {
            conn.requestMethod = method
            conn.connectTimeout = timeoutMs
            conn.readTimeout = timeoutMs
            conn.setRequestProperty("Accept", "application/json")
            headers.forEach { (k, v) -> conn.setRequestProperty(k, v) }
            if (json != null) {
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                conn.outputStream.use { it.write(json.toByteArray(Charsets.UTF_8)) }
            }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val body = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            return Response(code, body)
        } finally {
            conn.disconnect()
        }
    }
}
