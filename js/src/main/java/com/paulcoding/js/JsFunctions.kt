package com.paulcoding.js

import androidx.annotation.Keep
import com.google.gson.Gson
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.http.HttpMethod
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.mozilla.javascript.BaseFunction
import org.mozilla.javascript.Context
import org.mozilla.javascript.NativeJSON
import org.mozilla.javascript.NativeObject
import org.mozilla.javascript.Scriptable
import java.io.File
import java.io.InputStream
import java.net.URL
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

data class FetchParams(
    val method: String = "GET",
    val headers: Map<String, String>? = null,
    val body: Any? = null,
)

@Keep
data class FetchResponse(
    val url: String,
    val cx: Context?,
    val scope: Scriptable?,
    val response: String,
    val inputStream: InputStream,
) {
    override fun toString(): String {
        return response
    }

    val baseUrl: String
        get() {
            return try {
                URL(url).let {
                    it.protocol + "://" + it.host
                }
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }

    fun html(): Document {
        try {
            return Jsoup.parse(inputStream, "UTF-8", baseUrl)
        } catch (e: Exception) {
            e.printStackTrace()
            return Document("")
        }
    }

    fun json(): Any {
        return NativeJSON.parse(
            cx, scope, response
        ) { _, _, _, args -> args[1] }
    }

    fun text(): String = response
}

@OptIn(InternalAPI::class)
val fetchFunction = object : BaseFunction() {
    override fun call(
        cx: Context?,
        scope: Scriptable?,
        thisObj: Scriptable?,
        args: Array<out Any?>
    ): Any {

        val url = args.getOrNull(0) as? String
        val params = (args.getOrNull(1) as? NativeObject)?.let {
            Gson().fromJson(Gson().toJson(it), FetchParams::class.java)
        } ?: FetchParams()

        if (url.isNullOrEmpty())
            throw IllegalArgumentException("URL is required")

        return runBlocking {
            ktorClient.use { client ->
                val data = client.request(url) {
                    method = HttpMethod(params.method)
                    headers {
                        params.headers?.forEach { (key, value) ->
                            append(key, value)
                        }
                    }
                    if (params.body != null) {
                        body = params.body
                    }
                }
                if (data.status.value >= 400) {
                    throw IllegalArgumentException("${data.status.value}: ${data.status.description}")
                }
                val inputStream = data.rawContent.toInputStream()
                val res: String = data.body()
                FetchResponse(url, cx, scope, res, inputStream)
            }
        }
    }
}

val xhrFunction = object : BaseFunction() {
    override fun call(
        cx: Context?,
        scope: Scriptable?,
        thisObj: Scriptable?,
        args: Array<out Any>
    ): Any {
        val url = args.getOrNull(0) as? String
        if (url.isNullOrEmpty())
            throw IllegalArgumentException("URL is required")

        return runBlocking {
            ktorClient.use { client ->
                val res: String = client.get(url).body()
                NativeJSON.parse(
                    cx, scope, res
                ) { cx, scope, thisObj, args -> args[1] }
            }
        }
    }
}

val logFunction = object : BaseFunction() {
    override fun call(
        cx: Context?,
        scope: Scriptable?,
        thisObj: Scriptable?,
        args: Array<out Any?>
    ): Any? {
        print("[JS LOG]: ")
        args.forEach { arg ->
            println(Context.toString(arg))
        }
        return Context.getUndefinedValue()
    }
}

val importFunction = object : BaseFunction() {
    override fun call(
        cx: Context?,
        scope: Scriptable?,
        thisObj: Scriptable?,
        args: Array<out Any?>
    ) {
        val fileName = args.getOrNull(0) as? String
            ?: throw IllegalArgumentException("File path is required")

        val file = File(JS.root, fileName)
        cx?.evaluateReader(scope, file.reader(), file.name, 1, null)
    }
}

@OptIn(ExperimentalEncodingApi::class)
val atobFunction = object : BaseFunction() {
    override fun call(
        cx: Context?,
        scope: Scriptable?,
        thisObj: Scriptable?,
        args: Array<out Any>
    ): Any {
        val encoded = args.getOrNull(0) as? String
            ?: throw IllegalArgumentException("Encoded string is required")
        val decoded = Base64.Default.decode(encoded)
        return decoded.decodeToString()
    }
}

@OptIn(ExperimentalEncodingApi::class)
val btoaFunction = object : BaseFunction() {
    override fun call(
        cx: Context?,
        scope: Scriptable?,
        thisObj: Scriptable?,
        args: Array<out Any>
    ): Any {
        val decoded = args.getOrNull(0) as? String
            ?: throw IllegalArgumentException("Decoded string is required")
        val encoded = Base64.Default.encode(decoded.encodeToByteArray())
        return encoded
    }
}
