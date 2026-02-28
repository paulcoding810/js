package com.paulcoding.js

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mozilla.javascript.Context

/**
 * Integration tests simulating real-world usage patterns from MainActivity.
 */
class IntegrationTest {

    @Before
    fun setup() {
        Context.enter()
    }

    @Test
    fun testConsoleLogSimulation() = runBlocking {
        val js = JS()
        
        // Console.log should not throw errors (though we can't capture output in tests)
        val result = js.evaluateString<Unit>(
            """
            console.log('Hello from JS')
            console.log('Multiple', 'arguments', 123)
            """
        )
        
        assertTrue(result.isSuccess)
    }

    @Test
    fun testTypeofOperator() = runBlocking {
        val js = JS()
        
        val result = js.evaluateString<String>(
            """
            let title = "Wikipedia"
            let type = typeof(title)
            type
            """
        )
        
        assertTrue(result.isSuccess)
        assertEquals("string", result.getOrNull())
    }

    @Test
    fun testStringConversion() = runBlocking {
        val js = JS()
        
        val result = js.evaluateString<String>(
            """
            let title = "Test Title"
            let converted = String(title)
            converted
            """
        )
        
        assertTrue(result.isSuccess)
        assertEquals("Test Title", result.getOrNull())
    }

    @Test
    fun testMultipleStatementsWithReturn() = runBlocking {
        val js = JS()
        
        val result = js.evaluateString<String>(
            """
            let data = "initial"
            data = data.toUpperCase()
            data = data + " MODIFIED"
            data
            """
        )
        
        assertTrue(result.isSuccess)
        assertEquals("INITIAL MODIFIED", result.getOrNull())
    }

    @Test
    fun testJSONParse() = runBlocking {
        val js = JS()
        
        @Serializable
        data class SimpleData(val message: String, val count: Int)
        
        val result = js.evaluateString<SimpleData>(
            """
            let jsonString = '{"message": "Hello", "count": 42}'
            let parsed = JSON.parse(jsonString)
            parsed
            """
        )
        
        assertTrue(result.isSuccess)
        val data = result.getOrNull()
        assertEquals("Hello", data?.message)
        assertEquals(42, data?.count)
    }

    @Test
    fun testJSONStringifyAndParse() = runBlocking {
        val js = JS()
        
        val result = js.evaluateString<String>(
            """
            let obj = {
                title: 'Test',
                body: 'Content',
                userId: 1
            }
            let jsonString = JSON.stringify(obj)
            jsonString
            """
        )
        
        assertTrue(result.isSuccess)
        val jsonString = result.getOrNull()
        assertTrue(jsonString?.contains("Test") == true)
        assertTrue(jsonString?.contains("Content") == true)
    }

    @Test
    fun testErrorHandlingWithTryCatch() = runBlocking {
        val js = JS()
        
        val result = js.evaluateString<String>(
            """
            try {
                let obj = JSON.parse("invalid json")
                "success"
            } catch(e) {
                "error caught"
            }
            """
        )
        
        assertTrue(result.isSuccess)
        assertEquals("error caught", result.getOrNull())
    }

    @Test
    fun testResultOnSuccess() = runBlocking {
        val js = JS()
        
        var successValue: String? = null
        
        js.evaluateString<String>(
            """
            "operation completed"
            """
        ).onSuccess {
            successValue = it
        }
        
        assertEquals("operation completed", successValue)
    }

    @Test
    fun testResultOnFailure() = runBlocking {
        val js = JS()
        
        var errorMessage: String? = null
        
        js.evaluateString<String>(
            """
            throw new Error("Test error")
            """
        ).onFailure {
            errorMessage = it.message
        }
        
        assertNotNull(errorMessage)
        assertTrue(errorMessage?.contains("error") == true)
    }

    @Test
    fun testChainedOperations() = runBlocking {
        val js = JS()
        
        @Serializable
        data class ProcessedData(
            val original: String,
            val processed: String,
            val length: Int
        )
        
        val result = js.evaluateString<ProcessedData>(
            """
            let original = "hello world"
            let processed = original
                .split(" ")
                .map(word => word.toUpperCase())
                .join("-")
            
            let data = {
                original: original,
                processed: processed,
                length: processed.length
            }
            data
            """
        )
        
        assertTrue(result.isSuccess)
        val data = result.getOrNull()
        assertEquals("hello world", data?.original)
        assertEquals("HELLO-WORLD", data?.processed)
        assertEquals(11, data?.length)
    }

    @Test
    fun testArrayMethods() = runBlocking {
        val js = JS()
        
        @Serializable
        data class ArrayResults(
            val filtered: List<Int>,
            val mapped: List<Int>,
            val total: Int
        )
        
        val result = js.evaluateString<ArrayResults>(
            """
            let numbers = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
            let filtered = numbers.filter(n => n > 5)
            let mapped = filtered.map(n => n * 2)
            let total = mapped.reduce((sum, n) => sum + n, 0)
            
            let results = {
                filtered: filtered,
                mapped: mapped,
                total: total
            }
            results
            """
        )
        
        assertTrue(result.isSuccess)
        val results = result.getOrNull()
        assertNotNull(results)
        assertEquals(listOf(6, 7, 8, 9, 10), results?.filtered)
        assertEquals(listOf(12, 14, 16, 18, 20), results?.mapped)
        assertEquals(80, results?.total)
    }

    @Test
    fun testConditionalLogic() = runBlocking {
        val js = JS()
        
        val result = js.evaluateString<String>(
            """
            let age = 25
            let category = age >= 18 ? "adult" : "minor"
            category
            """
        )
        
        assertTrue(result.isSuccess)
        assertEquals("adult", result.getOrNull())
    }

    @Test
    fun testObjectDestructuring() = runBlocking {
        val js = JS()
        
        val result = js.evaluateString<String>(
            """
            let person = { name: "John", age: 30, city: "NYC" }
            let { name, city } = person
            name + " from " + city
            """
        )
        
        assertTrue(result.isSuccess)
        assertEquals("John from NYC", result.getOrNull())
    }

    @Test
    fun testTemplateLiterals() = runBlocking {
        val js = JS()
        
        val result = js.evaluateString<String>(
            """
            let user = "Alice"
            let greeting = `Hello, ${'$'}{user}! Welcome back.`
            greeting
            """
        )
        
        assertTrue(result.isSuccess)
        assertEquals("Hello, Alice! Welcome back.", result.getOrNull())
    }

    @Test
    fun testArrowFunctions() = runBlocking {
        val js = JS()
        
        val result = js.evaluateString<List<Int>>(
            """
            let numbers = [1, 2, 3, 4, 5]
            let squares = numbers.map(x => x * x)
            squares
            """
        )
        
        assertTrue(result.isSuccess)
        assertEquals(listOf(1, 4, 9, 16, 25), result.getOrNull())
    }

    @Test
    fun testObjectSpread() = runBlocking {
        val js = JS()
        
        @Serializable
        data class User(
            val name: String,
            val age: Int,
            val city: String
        )
        
        val result = js.evaluateString<User>(
            """
            let base = { name: "Bob", age: 25 }
            let extended = { ...base, city: "Boston" }
            extended
            """
        )
        
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertEquals("Bob", user?.name)
        assertEquals(25, user?.age)
        assertEquals("Boston", user?.city)
    }

    @Test
    fun testArrayIncludes() = runBlocking {
        val js = JS()
        
        val result = js.evaluateString<Boolean>(
            """
            let fruits = ["apple", "banana", "cherry"]
            let hasBanana = fruits.includes("banana")
            hasBanana
            """
        )
        
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
    }

    @Test
    fun testStringMethods() = runBlocking {
        val js = JS()
        
        @Serializable
        data class StringOps(
            val upper: String,
            val lower: String,
            val trimmed: String,
            val replaced: String
        )
        
        val result = js.evaluateString<StringOps>(
            """
            let text = "  Hello World  "
            let ops = {
                upper: text.toUpperCase(),
                lower: text.toLowerCase(),
                trimmed: text.trim(),
                replaced: text.trim().replace("World", "JS")
            }
            ops
            """
        )
        
        assertTrue(result.isSuccess)
        val ops = result.getOrNull()
        assertEquals("  HELLO WORLD  ", ops?.upper)
        assertEquals("  hello world  ", ops?.lower)
        assertEquals("Hello World", ops?.trimmed)
        assertEquals("Hello JS", ops?.replaced)
    }

    @Test
    fun testMathOperations() = runBlocking {
        val js = JS()
        
        @Serializable
        data class MathResults(
            val max: Int,
            val min: Int,
            val rounded: Int,
            val absolute: Int
        )
        
        val result = js.evaluateString<MathResults>(
            """
            let results = {
                max: Math.max(10, 20, 30),
                min: Math.min(10, 20, 30),
                rounded: Math.round(4.7),
                absolute: Math.abs(-42)
            }
            results
            """
        )
        
        assertTrue(result.isSuccess)
        val math = result.getOrNull()
        assertEquals(30, math?.max)
        assertEquals(10, math?.min)
        assertEquals(5, math?.rounded)
        assertEquals(42, math?.absolute)
    }

    @Test
    fun testFunctionWithMultipleReturns() = runBlocking {
        val js = JS()
        
        js.evaluateString<Unit>(
            """
            function categorize(age) {
                if (age < 13) return "child"
                if (age < 20) return "teen"
                if (age < 65) return "adult"
                return "senior"
            }
            """
        )
        
        val result = js.callFunction<String>("categorize", arrayOf(30))
        
        assertTrue(result.isSuccess)
        assertEquals("adult", result.getOrNull())
    }

    @Test
    fun testMultipleJSInstances() = runBlocking {
        val js1 = JS()
        val js2 = JS()
        
        js1.evaluateString<Unit>("var x = 10")
        js2.evaluateString<Unit>("var x = 20")
        
        val result1 = js1.evaluateString<Int>("x")
        val result2 = js2.evaluateString<Int>("x")
        
        assertEquals(10, result1.getOrNull())
        assertEquals(20, result2.getOrNull())
    }

    @Test
    fun testPropertiesInjectionUsage() = runBlocking {
        val properties = mapOf(
            "apiKey" to "test-key-123",
            "environment" to "testing"
        )
        val js = JS(properties = properties)
        
        val result = js.evaluateString<String>(
            """
            let config = apiKey + "-" + environment
            config
            """
        )
        
        assertTrue(result.isSuccess)
        assertEquals("test-key-123-testing", result.getOrNull())
    }
}
