package com.paulcoding.js

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging

val ktorClient
    get() = HttpClient(Android) {
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    log(message, "HTTP Client")
                }
            }
            level = if (BuildConfig.DEBUG) LogLevel.HEADERS else LogLevel.INFO
        }
    }
