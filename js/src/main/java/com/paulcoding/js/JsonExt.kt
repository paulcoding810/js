package com.paulcoding.js

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import org.mozilla.javascript.NativeArray
import org.mozilla.javascript.NativeObject

fun Any?.toJsonElement(gson: Gson = Gson()): JsonElement {
    return when (this) {
        is NativeObject -> {
            val map = entries.associate { it.key.toString() to it.value }
            gson.toJsonTree(map)
        }

        is NativeArray -> {
            val list = toArray().toList()
            gson.toJsonTree(list)
        }

        null -> JsonNull.INSTANCE
        is String -> JsonPrimitive(this)
        is Number -> JsonPrimitive(this)
        is Boolean -> JsonPrimitive(this)
        is Map<*, *> -> gson.toJsonTree(this)
        is Collection<*> -> gson.toJsonTree(this)

        else -> try {
            JsonParser.parseString(gson.toJson(this))
        } catch (e: Exception) {
            println("Error converting to JsonElement: $this")
            e.printStackTrace()
            gson.toJsonTree(this)
        } catch (e: Exception) {
            println("Error converting to JsonElement: $this")
            e.printStackTrace()
            JsonNull.INSTANCE
        }
    }
}