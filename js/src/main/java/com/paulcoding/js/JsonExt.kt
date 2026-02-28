package com.paulcoding.js

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.mozilla.javascript.NativeArray
import org.mozilla.javascript.NativeJavaObject
import org.mozilla.javascript.NativeObject

fun Any?.toJsonElement(gson: Gson = defaultGson): JsonElement {
    return when (this) {

        null -> JsonNull.INSTANCE

        is NativeObject -> {
            val jsonObject = JsonObject()
            for ((key, value) in entries) {
                jsonObject.add(key.toString(), value.toJsonElement(gson))
            }
            jsonObject
        }

        is NativeArray -> {
            val jsonArray = JsonArray()
            for (item in this) {
                jsonArray.add(item.toJsonElement(gson))
            }
            jsonArray
        }

        is NativeJavaObject -> {
            this.unwrap().toJsonElement(gson)
        }

        is String -> JsonPrimitive(this)
        is Number -> JsonPrimitive(this)
        is Boolean -> JsonPrimitive(this)

        is Map<*, *> -> gson.toJsonTree(this)
        is Collection<*> -> gson.toJsonTree(this)

        else -> try {
            gson.toJsonTree(this)
        } catch (e: Exception) {
            e.printStackTrace()
            JsonNull.INSTANCE
        }
    }
}

// Reuse one Gson instance
private val defaultGson: Gson by lazy { Gson() }