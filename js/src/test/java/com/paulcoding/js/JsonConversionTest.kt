package com.paulcoding.js

import com.google.gson.JsonArray
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mozilla.javascript.Context
import org.mozilla.javascript.NativeObject

/**
 * Tests for toJsonElement conversion function.
 */
class JsonConversionTest {

    @Before
    fun setup() {
        Context.enter()
    }

    @Test
    fun testToJsonElement_withString() {
        val result = "test string".toJsonElement()
        assertTrue(result is JsonPrimitive)
        assertEquals("test string", result.asString)
    }

    @Test
    fun testToJsonElement_withNumber() {
        val result = 42.toJsonElement()
        assertTrue(result is JsonPrimitive)
        assertEquals(42, result.asInt)
    }

    @Test
    fun testToJsonElement_withDouble() {
        val result = 3.14.toJsonElement()
        assertTrue(result is JsonPrimitive)
        assertEquals(3.14, result.asDouble, 0.001)
    }

    @Test
    fun testToJsonElement_withBoolean() {
        val resultTrue = true.toJsonElement()
        assertTrue(resultTrue is JsonPrimitive)
        assertEquals(true, resultTrue.asBoolean)

        val resultFalse = false.toJsonElement()
        assertTrue(resultFalse is JsonPrimitive)
        assertEquals(false, resultFalse.asBoolean)
    }

    @Test
    fun testToJsonElement_withNativeObject() {
        val context = Context.getCurrentContext()
        val scope = context.initStandardObjects()

        val nativeObj = NativeObject()
        nativeObj.put("name", nativeObj, "John")
        nativeObj.put("age", nativeObj, 30)
        nativeObj.put("active", nativeObj, true)

        val result = nativeObj.toJsonElement()

        assertTrue(result is JsonObject)
        val jsonObj = result as JsonObject
        assertEquals("John", jsonObj["name"]?.asString)
        assertEquals(30, jsonObj["age"]?.asInt)
        assertEquals(true, jsonObj["active"]?.asBoolean)
    }

    @Test
    fun testToJsonElement_withNativeArray() {
        val context = Context.getCurrentContext()
        val scope = context.initStandardObjects()

        val elements = arrayOf<Any?>(42, "foo", true)

        val nativeArray = context.newArray(scope, elements)

        val result = nativeArray.toJsonElement()

        assertTrue(result is JsonArray)
        val jsonArray = result as JsonArray
        assertEquals(3, jsonArray.size())
        assertEquals(42, jsonArray[0].asInt)
        assertEquals("foo", jsonArray[1].asString)
        assertEquals(true, jsonArray[2].asBoolean)
    }

    @Test
    fun testToJsonElement_withNestedNativeObject() {
        val context = Context.getCurrentContext()
        val scope = context.initStandardObjects()

        val addressObj = NativeObject()
        addressObj.put("street", addressObj, "123 Main St")
        addressObj.put("city", addressObj, "Springfield")

        val userObj = NativeObject()
        userObj.put("name", userObj, "Alice")
        userObj.put("address", userObj, addressObj)

        val result = userObj.toJsonElement()

        assertTrue(result is JsonObject)
        val jsonObj = result as JsonObject
        assertEquals("Alice", jsonObj["name"]?.asString)

        val address = jsonObj["address"]?.asJsonObject
        assertNotNull(address)
        assertEquals("123 Main St", address?.get("street")?.asString)
        assertEquals("Springfield", address?.get("city")?.asString)
    }

    @Test
    fun testToJsonElement_withMixedArray() {
        val context = Context.getCurrentContext()
        val scope = context.initStandardObjects()

        val array = arrayOf("text", 42, true, null)
        val nativeArray = context.newArray(scope, array)

        val result = nativeArray.toJsonElement()

        assertTrue(result is JsonArray)
        val jsonArray = result as JsonArray
        assertEquals(4, jsonArray.size())
        assertEquals("text", jsonArray[0].asString)
        assertEquals(42, jsonArray[1].asInt)
        assertEquals(true, jsonArray[2].asBoolean)
        assertTrue(jsonArray[3] is JsonNull)
    }

    @Test
    fun testToJsonElement_withComplexStructure() {
        val context = Context.getCurrentContext()
        val scope = context.initStandardObjects()

        // Create array of objects
        val item1 = NativeObject()
        item1.put("id", item1, 1)
        item1.put("name", item1, "Item 1")

        val item2 = NativeObject()
        item2.put("id", item2, 2)
        item2.put("name", item2, "Item 2")

        val itemsArray = context.newArray(scope, arrayOf<Any?>(item1, item2))

        val rootObj = NativeObject()
        rootObj.put("title", rootObj, "Shopping List")
        rootObj.put("items", rootObj, itemsArray)
        rootObj.put("count", rootObj, 2)

        val result = rootObj.toJsonElement()

        assertTrue(result is JsonObject)
        val jsonObj = result as JsonObject
        assertEquals("Shopping List", jsonObj["title"]?.asString)
        assertEquals(2, jsonObj["count"]?.asInt)

        val items = jsonObj["items"]?.asJsonArray
        assertNotNull(items)
        assertEquals(2, items?.size())
        assertEquals(1, items?.get(0)?.asJsonObject?.get("id")?.asInt)
        assertEquals("Item 2", items?.get(1)?.asJsonObject?.get("name")?.asString)
    }

    @Test
    fun testToJsonElement_withEmptyObject() {
        val nativeObj = NativeObject()
        val result = nativeObj.toJsonElement()

        assertTrue(result is JsonObject)
        val jsonObj = result as JsonObject
        assertTrue(jsonObj.isEmpty())
    }

    @Test
    fun testToJsonElement_withEmptyArray() {
        val context = Context.getCurrentContext()
        val scope = context.initStandardObjects()

        val nativeArray = context.newArray(scope, arrayOf<Any>())
        val result = nativeArray.toJsonElement()

        assertTrue(result is JsonArray)
        val jsonArray = result as JsonArray
        assertTrue(jsonArray.isEmpty())
    }

    @Test
    fun testToJsonElement_withCustomObject() {
        data class CustomClass(val value: String)

        val custom = CustomClass("test")

        val result = custom.toJsonElement()

        // Custom objects fall back to toString()
        assertTrue(result is JsonObject)
        assertEquals("test", result.asJsonObject.get("value").asString)
    }
}
