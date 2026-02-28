package com.paulcoding.js

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkTest {
    @Test
    fun testFetchJson() = runBlocking {
        val js = JS()

        val result = js.evaluateString<String>(
            """
            let data = fetch("https://httpbin.org/json")
            let json = data.json()
            JSON.stringify(json)
            """
        )

        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }


    @Test
    fun testFetchWithHeaders() = runBlocking {
        val js = JS()

        val result = js.evaluateString<String>(
            """
            let data = fetch("https://httpbin.org/headers", {
                method: "GET",
                headers: {
                    "X-Custom-Header": "test-value"
                }
            })
            let json = data.json()
            json.headers["X-Custom-Header"]
            """
        )

        assertTrue(result.isSuccess)
        assertEquals("test-value", result.getOrNull())
    }

    @Test
    fun testFetch404() = runBlocking {
        val js = JS()

        val result = js.evaluateString<String>(
            """
            let data = fetch("https://httpbin.org/status/404")
            data.text()
            """
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun testJsoupSelectText() = runBlocking {
        val js = JS()

        val result = js.evaluateString<String>(
            """
            let data = fetch("https://en.wikipedia.org/")
            let html = data.html()
            let text = html.select("h1").first().text()
            text
            """
        ).onFailure { it.printStackTrace() }

        assertTrue(result.isSuccess)
        assertEquals("Main Page", result.getOrNull())
    }

    @Test
    fun testJsoupMultipleSelect() = runBlocking {
        val js = JS()

        val result = js.evaluateString<Int>(
            """
            let data = fetch("https://en.wikipedia.org/")
            let html = data.html()
            let links = html.select("a[href^='/wiki/']")
            links.length
            """
        ).onFailure { it.printStackTrace() }

        assertTrue(result.isSuccess)
        assertTrue((result.getOrNull() ?: 0) >= 3)
    }

    @Test
    fun testJsoupAttr() = runBlocking {
        val js = JS()

        val result = js.evaluateString<String>(
            """
            let data = fetch("https://en.wikipedia.org/")
            let html = data.html()
            let el = html.select("#Welcome_to_Wikipedia").first()
            el.attr("id")
            """
        )

        assertTrue(result.isSuccess)
        assertEquals("Welcome_to_Wikipedia", result.getOrNull())
    }
}