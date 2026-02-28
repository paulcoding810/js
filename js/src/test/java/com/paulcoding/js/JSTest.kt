package com.paulcoding.js

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mozilla.javascript.Context

/**
 * Unit tests for JS engine functionality based on MainActivity usage patterns.
 */
class JSTest {

    @Before
    fun setup() {
        // Initialize Rhino context for each test
        Context.enter()
    }

    @Test
    fun testBasicStringEvaluation() = runBlocking {
        val js = JS()
        
        val result = js.evaluateString<String>(
            """
            let greeting = "Hello from JS"
            greeting
            """
        )
        
        assertTrue(result.isSuccess)
        assertEquals("Hello from JS", result.getOrNull())
    }

    @Test
    fun testNumberEvaluation() = runBlocking {
        val js = JS()
        
        val result = js.evaluateString<Int>(
            """
            let x = 42
            x
            """
        )
        
        assertTrue(result.isSuccess)
        assertEquals(42, result.getOrNull())
    }

    @Test
    fun testArrayEvaluation() = runBlocking {
        val js = JS()
        
        val result = js.evaluateString<List<String>>(
            """
            let arr = ["apple", "banana", "cherry"]
            arr
            """
        )
        
        assertTrue(result.isSuccess)
        assertEquals(listOf("apple", "banana", "cherry"), result.getOrNull())
    }

    @Test
    fun testObjectEvaluation() = runBlocking {
        val js = JS()
        
        @Serializable
        data class Person(val name: String, val age: Int)
        
        val result = js.evaluateString<Person>(
            """
            let person = {
                name: "John",
                age: 30
            }
            person
            """
        )
        
        assertTrue(result.isSuccess)
        val person = result.getOrNull()
        assertNotNull(person)
        assertEquals("John", person?.name)
        assertEquals(30, person?.age)
    }

    @Test
    fun testHeadlinesDataClass() = runBlocking {
        val js = JS()
        
        @Serializable
        data class Headlines(
            val title: String,
            val headlines: List<String>
        )
        
        val result = js.evaluateString<Headlines>(
            """
            let data = {
                title: "Latest News",
                headlines: ["Breaking News 1", "Breaking News 2", "Breaking News 3"]
            }
            data
            """
        )
        
        assertTrue(result.isSuccess)
        val headlines = result.getOrNull()
        assertNotNull(headlines)
        assertEquals("Latest News", headlines?.title)
        assertEquals(3, headlines?.headlines?.size)
        assertTrue(headlines?.headlines?.contains("Breaking News 1") == true)
    }

    @Test
    fun testCallFunction() = runBlocking {
        val js = JS()
        
        // First, define the function
        js.evaluateString<Unit>(
            """
            function add(a, b) {
                return a + b
            }
            """
        )
        
        // Then call it
        val result = js.callFunction<Int>("add", arrayOf(5, 3))
        
        assertTrue(result.isSuccess)
        assertEquals(8, result.getOrNull())
    }

    @Test
    fun testCallFunctionWithString() = runBlocking {
        val js = JS()
        
        js.evaluateString<Unit>(
            """
            function greet(name) {
                return "Hello, " + name + "!"
            }
            """
        )
        
        val result = js.callFunction<String>("greet", arrayOf("World"))
        
        assertTrue(result.isSuccess)
        assertEquals("Hello, World!", result.getOrNull())
    }

    @Test
    fun testUndefinedFunctionThrowsError() = runBlocking {
        val js = JS()
        
        val result = js.callFunction<String>("nonExistentFunction", arrayOf())
        
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("not found") == true)
    }

    @Test
    fun testJavaScriptError() = runBlocking {
        val js = JS()
        
        val result = js.evaluateString<String>(
            """
            throw new Error("Test error")
            """
        )
        
        assertTrue(result.isFailure)
    }

    @Test
    fun testComplexNestedObject() = runBlocking {
        val js = JS()
        
        @Serializable
        data class Address(val street: String, val city: String)
        
        @Serializable
        data class User(val name: String, val address: Address)
        
        val result = js.evaluateString<User>(
            """
            let user = {
                name: "Alice",
                address: {
                    street: "123 Main St",
                    city: "Springfield"
                }
            }
            user
            """
        )
        
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals("Alice", user?.name)
        assertEquals("123 Main St", user?.address?.street)
        assertEquals("Springfield", user?.address?.city)
    }

    @Test
    fun testMapEvaluation() = runBlocking {
        val js = JS()
        
        val result = js.evaluateString<Map<String, Any>>(
            """
            let config = {
                apiKey: "abc123",
                timeout: 5000,
                enabled: true
            }
            config
            """
        )
        
        assertTrue(result.isSuccess)
        val config = result.getOrNull()
        assertNotNull(config)
        assertEquals("abc123", config?.get("apiKey"))
    }

    @Test
    fun testBooleanEvaluation() = runBlocking {
        val js = JS()
        
        val result = js.evaluateString<Boolean>(
            """
            let isValid = true
            isValid
            """
        )
        
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
    }

    @Test
    fun testNullEvaluation() = runBlocking {
        val js = JS()
        
        val result = js.evaluateString<String?>(
            """
            let value = null
            value
            """
        )

        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun testArrayMap() = runBlocking {
        val js = JS()
        
        val result = js.evaluateString<List<Int>>(
            """
            let numbers = [1, 2, 3, 4, 5]
            let doubled = numbers.map(n => n * 2)
            doubled
            """
        )
        
        assertTrue(result.isSuccess)
        assertEquals(listOf(2, 4, 6, 8, 10), result.getOrNull())
    }

    @Test
    fun testArrayFilter() = runBlocking {
        val js = JS()
        
        val result = js.evaluateString<List<Int>>(
            """
            let numbers = [1, 2, 3, 4, 5, 6]
            let evens = numbers.filter(n => n % 2 === 0)
            evens
            """
        )
        
        assertTrue(result.isSuccess)
        assertEquals(listOf(2, 4, 6), result.getOrNull())
    }

    @Test
    fun testStringManipulation() = runBlocking {
        val js = JS()
        
        val result = js.evaluateString<String>(
            """
            let text = "hello world"
            let capitalized = text.toUpperCase()
            capitalized
            """
        )
        
        assertTrue(result.isSuccess)
        assertEquals("HELLO WORLD", result.getOrNull())
    }

    @Test
    fun testJSONStringify() = runBlocking {
        val js = JS()
        
        val result = js.evaluateString<String>(
            """
            let obj = { name: "Test", value: 42 }
            let json = JSON.stringify(obj)
            json
            """
        )
        
        assertTrue(result.isSuccess)
        val jsonString = result.getOrNull()
        assertNotNull(jsonString)
        assertTrue(jsonString?.contains("Test") == true)
        assertTrue(jsonString?.contains("42") == true)
    }

    @Test
    fun testMultipleEvaluations() = runBlocking {
        val js = JS()
        
        // First evaluation sets a variable
        js.evaluateString<Unit>(
            """
            var counter = 0
            """
        )
        
        // Second evaluation increments it
        js.evaluateString<Unit>(
            """
            counter++
            """
        )
        
        // Third evaluation retrieves the value
        val result = js.evaluateString<Int>(
            """
            counter
            """
        )
        
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull())
    }

    @Test
    fun testPropertiesInjection() = runBlocking {
        val properties = mapOf("apiUrl" to "https://api.example.com")
        val js = JS(properties = properties)
        
        val result = js.evaluateString<String>(
            """
            apiUrl
            """
        )
        
        assertTrue(result.isSuccess)
        assertEquals("https://api.example.com", result.getOrNull())
    }
}
