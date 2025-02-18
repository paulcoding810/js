# Android JavaScript Library

[![Maven Central](https://img.shields.io/maven-central/v/com.paulcoding/js.svg)](https://central.sonatype.com/artifact/com.paulcoding/js)

A Kotlin-based Android library that provides seamless JavaScript execution and integration capabilities using Mozilla's Rhino engine.

## Features

- Execute JavaScript code from strings or files
- Built-in network functions with fetch
- HTML parsing with JSoup integration
- Base64 decoding support
- Console logging capabilities
- JSON parsing and conversion
- Coroutine support for asynchronous operations

## Installation

Add the following dependency to your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.paulcoding:js:1.0.2")
}
```

## Initialization

Initialize the library in your Application class:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        JS.initialize(applicationContext)
    }
}
```

## Usage

### Basic JavaScript Execution

```kotlin
// Create JS instance
val js = JS()

// Execute JavaScript string
suspend fun executeJs() {
    js.evaluateString<String>("'Hello, World!'")
        .onSuccess { result ->
            println(result) // Prints: Hello, World!
        }
        .onFailure { error ->
            error.printStackTrace()
        }
}

// Call JavaScript function
suspend fun callJsFunction() {
    js.callFunction<Int>("add", arrayOf(2, 3))
        .onSuccess { result ->
            println(result) // Prints: 5
        }
}
```

### Loading JS Object

```kotlin
// Load from app's files directory
val js = JS("scripts", "main.js")

// Or with absolute path
val js = JS(File("/path/to/script.js"))
```

```kotlin
// Pass custom properties to the JavaScript environment:
val properties = mapOf(
    "apiKey" to "your-api-key",
    "baseUrl" to "https://api.example.com"
)
val js = JS(properties = properties)
```

### Built-in JavaScript Functions

#### Network Requests (fetch)
```javascript
// Basic GET request
const response = fetch('https://api.example.com/data')
const jsonData = response.json()
// or
const htmlDoc = response.html()
// or
const textContent = response.text()

// Advanced request with options
const response = fetch('https://api.example.com/data', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json'
    },
    body: JSON.stringify({ key: 'value' })
})
```

#### Console Logging
```javascript
console.log('Debug message')
```

#### Base64 Decoding
```javascript
const decoded = atob('SGVsbG8gV29ybGQ=')
```

## Dependencies

- [Rhino](https://github.com/mozilla/rhino) - JavaScript engine
- [Ktor](https://ktor.io/) - HTTP client
- [Gson](https://github.com/google/gson) - JSON parsing
- [JSoup](https://jsoup.org/) - HTML parsing

## ProGuard Configuration

```proguard
-keepattributes Signature
-dontwarn org.mozilla.javascript.**
-keep class org.mozilla.javascript.** { *; }
-keep class org.jsoup.** { *; }
-dontwarn org.jspecify.annotations.NullMarked
```

## Requirements

- Minimum SDK: 24
- Kotlin: 1.8+
- Java: 11

## Credits

[VBook Extensions](https://github.com/Darkrai9x/vbook-extensions)

## License

See [License](LICENSE)