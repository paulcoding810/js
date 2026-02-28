package com.paulcoding.js

import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mozilla.javascript.Context

/**
 * Tests for data class serialization/deserialization patterns used in MainActivity.
 */
class DataClassTest {
    lateinit var gson: Gson

    @Serializable
    data class Headlines(
        val title: String,
        val headlines: List<String>
    )

    @Serializable
    data class Post(
        val userId: Int,
        val id: Int,
        val title: String,
        val body: String
    )

    @Serializable
    data class WikipediaPage(
        val title: String,
        val url: String,
        val sections: List<String>
    )

    @Before
    fun setup() {
        Context.enter()
        gson = Gson()
    }

    @Test
    fun testHeadlinesFromJS() = runBlocking {
        val js = JS()

        val result = js.evaluateString<Headlines>(
            """
            let page = {
                title: "Wikipedia Today",
                headlines: [
                    "Science discovers new planet",
                    "Technology breakthrough announced",
                    "Sports team wins championship"
                ]
            }
            page
            """
        )

        assertTrue(result.isSuccess)
        val headlines = result.getOrNull()
        assertNotNull(headlines)
        assertEquals("Wikipedia Today", headlines?.title)
        assertEquals(3, headlines?.headlines?.size)
        assertTrue(headlines?.headlines?.get(0)?.contains("Science") == true)
    }

    @Test
    fun testPostDataClass() = runBlocking {
        val js = JS()

        val result = js.evaluateString<Post>(
            """
            let post = {
                userId: 1,
                id: 101,
                title: "Sample Post Title",
                body: "This is the post content"
            }
            post
            """
        )

        assertTrue(result.isSuccess)
        val post = result.getOrNull()
        assertNotNull(post)
        assertEquals(1, post?.userId)
        assertEquals(101, post?.id)
        assertEquals("Sample Post Title", post?.title)
        assertEquals("This is the post content", post?.body)
    }

    @Test
    fun testArrayOfPosts() = runBlocking {
        val js = JS()

        val result = js.evaluateString<List<Post>>(
            """
            let posts = [
                { userId: 1, id: 1, title: "Post 1", body: "Body 1" },
                { userId: 1, id: 2, title: "Post 2", body: "Body 2" },
                { userId: 2, id: 3, title: "Post 3", body: "Body 3" }
            ]
            posts
            """
        )

        assertTrue(result.isSuccess)
        val posts = result.getOrNull()
        assertNotNull(posts)
        assertEquals(3, posts?.size)
        assertEquals("Post 1", posts?.get(0)?.title)
        assertEquals(2, posts?.get(2)?.userId)
    }

    @Test
    fun testEmptyHeadlinesList() = runBlocking {
        val js = JS()

        val result = js.evaluateString<Headlines>(
            """
            let page = {
                title: "No News Today",
                headlines: []
            }
            page
            """
        )

        assertTrue(result.isSuccess)
        val headlines = result.getOrNull()
        assertNotNull(headlines)
        assertEquals("No News Today", headlines?.title)
        assertTrue(headlines?.headlines?.isEmpty() == true)
    }

    @Test
    fun testWikipediaPageStructure() = runBlocking {
        val js = JS()

        val result = js.evaluateString<WikipediaPage>(
            """
            let page = {
                title: "Wikipedia Main Page",
                url: "https://en.wikipedia.org/",
                sections: ["Featured article", "In the news", "Did you know"]
            }
            page
            """
        )

        assertTrue(result.isSuccess)
        val page = result.getOrNull()
        assertNotNull(page)
        assertEquals("Wikipedia Main Page", page?.title)
        assertEquals("https://en.wikipedia.org/", page?.url)
        assertEquals(3, page?.sections?.size)
    }

    @Test
    fun testPartialDataClass() = runBlocking {
        val js = JS()

        @Serializable
        data class PartialPost(
            val title: String,
            val body: String? = null
        )

        val result = js.evaluateString<PartialPost>(
            """
            let post = {
                title: "Title Only"
            }
            post
            """
        )

        assertTrue(result.isSuccess)
        val post = result.getOrNull()
        assertNotNull(post)
        assertEquals("Title Only", post?.title)
        assertNull(post?.body)
    }

    @Test
    fun testNestedDataClass() = runBlocking {
        val js = JS()

        @Serializable
        data class Author(val name: String, val email: String)

        @Serializable
        data class Article(
            val title: String,
            val content: String,
            val author: Author
        )

        val result = js.evaluateString<Article>(
            """
            let article = {
                title: "Great Article",
                content: "Article content here",
                author: {
                    name: "John Doe",
                    email: "john@example.com"
                }
            }
            article
            """
        )

        assertTrue(result.isSuccess)
        val article = result.getOrNull()
        assertNotNull(article)
        assertEquals("Great Article", article?.title)
        assertEquals("John Doe", article?.author?.name)
        assertEquals("john@example.com", article?.author?.email)
    }

    @Test
    fun testMapTransformation() = runBlocking {
        val js = JS()

        val result = js.evaluateString<Headlines>(
            """
            let rawHeadlines = ["Raw 1", "Raw 2", "Raw 3"]
            let processedHeadlines = rawHeadlines.map(h => h.toUpperCase())
            let data = {
                title: "Processed Headlines",
                headlines: processedHeadlines
            }
            data
            """
        )

        assertTrue(result.isSuccess)
        val headlines = result.getOrNull()
        assertNotNull(headlines)
        assertEquals("RAW 1", headlines?.headlines?.get(0))
        assertEquals("RAW 2", headlines?.headlines?.get(1))
        assertEquals("RAW 3", headlines?.headlines?.get(2))
    }

    @Test
    fun testFilterTransformation() = runBlocking {
        val js = JS()

        val result = js.evaluateString<Headlines>(
            """
            let allHeadlines = [
                "Important: Breaking News",
                "Regular Update",
                "Important: Critical Alert",
                "Normal Article"
            ]
            let filtered = allHeadlines.filter(h => h.startsWith("Important"))
            let data = {
                title: "Important Headlines Only",
                headlines: filtered
            }
            data
            """
        )

        assertTrue(result.isSuccess)
        val headlines = result.getOrNull()
        assertNotNull(headlines)
        assertEquals(2, headlines?.headlines?.size)
        assertTrue(headlines?.headlines?.all { it.startsWith("Important") } == true)
    }

    @Test
    fun testDataClassWithNumbers() = runBlocking {
        val js = JS()

        @Serializable
        data class Statistics(
            val views: Int,
            val likes: Int,
            val shares: Int,
            val rating: Double
        )

        val result = js.evaluateString<Statistics>(
            """
            let stats = {
                views: 1000,
                likes: 250,
                shares: 50,
                rating: 4.5
            }
            stats
            """
        )

        assertTrue(result.isSuccess)
        val stats = result.getOrNull()
        assertNotNull(stats)
        assertEquals(1000, stats?.views)
        assertEquals(250, stats?.likes)
        assertEquals(50, stats?.shares)
        assertEquals(4.5, stats?.rating ?: 0.0, 0.001)
    }
}
