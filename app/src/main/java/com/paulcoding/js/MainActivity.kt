package com.paulcoding.js

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paulcoding.js.ui.theme.JsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        JS.initialize(this)

        enableEdgeToEdge()
        setContent {
            JsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    JSView(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

data class Headlines(
    val title: String,
    val headlines: List<String>
)

@Composable
fun JSView(modifier: Modifier = Modifier) {
    var title by remember { mutableStateOf("") }
    var headlines by remember { mutableStateOf(emptyList<String>()) }

    LaunchedEffect(Unit) {
        // use JS
        val js = JS()
        js.evaluateString<Unit>(
            "console.log('Hello from js')"
        )

        js.evaluateString<String>(
            """
            let data = fetch("https://en.wikipedia.org/")
            let html = data.html()
            let title = html.title()
            console.log(title)
            console.log(typeof(title))
            let x = String(title)
            x
            """
        ).alsoLog("html")

        js.evaluateString<String>(
            """
            data = fetch('https://jsonplaceholder.typicode.com/posts', {
                  method: 'POST',
                  body: JSON.stringify({
                    title: 'foo',
                    body: 'bar',
                    userId: 1,
                  }),
                  headers: {
                    'Content-type': 'application/json; charset=UTF-8',
                    'X-Auth': 'my-auth-token',
                  },
                })
                .json()
            
            console.log("response=" + JSON.stringify(data))
            let title = data.title
            title
            """
        ).alsoLog("html")

//        js.evaluateString<Headlines>(
//            """
//            let doc = fetch("https://en.wikipedia.org/").html()
//            console.log(doc)
//            let title = doc.title()
//            let newsHeadlines = doc.select("#mp-itn b a")
//            let headlines = []
//            newsHeadlines.forEach(a => {
//                headlines.push(a.attr("title"))
//            })
//            let data = {
//                title: title,
//                headlines: headlines
//            }
//            data
//        """.trimIndent()
//        ).onSuccess {
//            title = it.title
//            headlines = it.headlines
//        }.onFailure {
//            title = it.message ?: "Unknown error"
//        }

        // use jsoup
//        launch(Dispatchers.IO) {
//            Jsoup.connect("https://en.wikipedia.org/").followRedirects(true).get().title()
//                .alsoLog("Jsoup")
//        }

        // use ktorClient
//        ktorClient.use { client ->
//            client.get("https://en.wikipedia.org/").alsoLog("ktorClient")
//        }

        // rest
    }

    Column(modifier = modifier.padding(16.dp)) {
        Text("Fetch data sample")
        Text(
            text = title
        )

        Spacer(modifier = Modifier.padding(8.dp))

        headlines.forEach {
            Text(text = it)
        }
    }

}
