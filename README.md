# ChronoMaster: Smart Android Date & Time Formatting Library

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

ChronoMaster is an expert-level Android library in Kotlin designed to handle date and time formatting with intelligence and resilience. Built on the modern `java.time` (JSR-310) API, its core feature is the ability to parse various date string formats automatically, without requiring the developer to specify the input pattern. This makes your app robust against backend date format changes.

## Features

- **Modern `java.time` API**: Uses the thread-safe and immutable `java.time` classes (`ZonedDateTime`, `Instant`, `DateTimeFormatter`) for all operations.
- **Automatic Input Format Detection**: Parses date strings like `2025-10-31T10:30:00Z`, `31/10/2025`, and even Epoch timestamps (`1730389200`) without you defining the input format.
- **Advanced Localization (I18n)**: Format dates and times according to the user's `Locale` and preferred `FormatStyle`, ensuring a native experience.
- **Custom Parser Registration**: Teach ChronoMaster new, non-standard date formats at runtime to make your app truly future-proof.
- **Robust Time Zone Management**: Set a global default for input and output timezones (`ZoneId`), and easily override them for specific cases.
- **'True Time' Synchronization**: Fetch the real current time as an `Instant` from an NTP server to protect against user-manipulated device clocks.
- **Safe Error Handling**: Uses a `Result` wrapper (`ChronoResult`) to prevent crashes from parsing or network errors, providing clear error messages instead.

## Setup

### 1. Enable Java 8+ API Desugaring

To use the `java.time` API on all supported Android versions, you must enable core library desugaring in your module's `build.gradle.kts` file.

```kotlin
// build.gradle.kts (Module)

android {
    // ...
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        // Enable desugaring
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    // ...
    // Add the desugaring dependency
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
}
```

### 2. Add ChronoMaster Dependency

_To be added: Instructions for adding the library via Gradle._

```groovy
// build.gradle (Module)
dependencies {
    // implementation 'com.chronomaster:library:1.0.0'
}
```

## Usage

### 1. Initialization

It's highly recommended to initialize ChronoMaster once in your `Application` class. This sets up the default timezones and any custom parsers.

```kotlin
import com.chronomaster.library.ChronoMaster
import android.app.Application

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ChronoMaster.initialize(
            inputTimeZoneId = "UTC", // Default timezone for input strings
            outputTimeZoneId = "America/New_York", // Default for all formatted output
            customParsers = listOf("yyyy/MM/dd HH:mm:ss.SSS") // Teach ChronoMaster a new format
        )
    }
}
```

### 2. Smart Date Formatting

#### Using a Custom Format Pattern

Provide the date string and your desired output pattern. ChronoMaster handles the rest.

```kotlin
val isoDate = "2025-10-31T12:30:00Z"
val result = ChronoMaster.formatDate(isoDate, "dd MMM, yyyy 'at' hh:mm a")

// Handle the result safely
when (result) {
    is ChronoResult.Success -> textView.text = result.data // "31 Oct, 2025 at 08:30 AM" (in EST)
    is ChronoResult.Error -> showToast(result.message)
}
```

#### Using a Custom Parser

Because we registered `"yyyy/MM/dd HH:mm:ss.SSS"` during initialization, ChronoMaster can now parse it automatically.

```kotlin
val customDate = "2025/10/31 10:00:00.123"
val result = ChronoMaster.formatDate(customDate, "HH:mm:ss") // Success: "06:00:00" (in EST)
```

### 3. Localization (I18n)

Format a date using a standard style for a specific `Locale`. This is the recommended approach for displaying dates to users.

```kotlin
import java.time.format.FormatStyle
import java.util.Locale

val date = "2025-10-31T12:30:00Z"

// Format for a user in France
val result = ChronoMaster.formatDate(
    dateString = date,
    formatStyle = FormatStyle.FULL,
    locale = Locale.FRANCE
)

// result.data will be "vendredi 31 octobre 2025 à 08:30:00 EDT"
```

### 4. Getting True Network Time

Fetch a trustworthy `Instant` asynchronously. This is a `suspend` function and should be called from a coroutine.

```kotlin
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import java.time.format.DateTimeFormatter

lifecycleScope.launch {
    when (val result = ChronoMaster.getTrueTime()) {
        is ChronoResult.Success -> {
            val trueInstant = result.data // A java.time.Instant object
            // You can format it directly
            val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.systemDefault())
            val formattedTime = formatter.format(trueInstant)
        }
        is ChronoResult.Error -> {
            showToast(result.message)
        }
    }
}
```

### 5. Utility Functions

The utility functions now use `java.time` objects.

```kotlin
import java.time.ZonedDateTime

// First, get a ZonedDateTime object
val parseResult = ChronoMaster.parseDate("2025-10-31T12:30:00Z")

if (parseResult is ChronoResult.Success) {
    val today = parseResult.data
    val fiveDaysLater = ChronoMaster.plusDays(today, 5)
    val tenDaysAgo = ChronoMaster.minusDays(today, 10)
}
```

## Error Handling

ChronoMaster functions never throw exceptions directly. They return a `ChronoResult` object. Always check if the result is `Success` or `Error`.

```kotlin
val badDate = "this is not a date"
val result = ChronoMaster.formatDate(badDate, "dd/MM/yyyy")

when (result) {
    is ChronoResult.Success -> { /* Will not be executed */ }
    is ChronoResult.Error -> {
        Log.e("ChronoMaster", result.message)
        // e.g., "Failed to parse date string: 'this is not a date'. None of the supported formats matched."
    }
}
```

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License

[Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)
