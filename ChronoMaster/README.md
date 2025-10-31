# ChronoMaster: Smart Android Date & Time Formatting Library

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

ChronoMaster is an expert-level Android library in Kotlin designed to handle date and time formatting with intelligence and resilience. Its core feature is the ability to parse various date string formats automatically, without requiring the developer to specify the input pattern. This makes your app robust against backend date format changes.

## Features

- **Automatic Input Format Detection**: Parses date strings like `2025-10-31T10:30:00Z`, `31/10/2025`, and even Epoch timestamps (`1730389200`) without you defining the input format.
- **Robust Time Zone Management**: Set a global default for input and output timezones, and easily override them for specific cases.
- **'True Time' Synchronization**: Fetch the real current time from an NTP server to protect against user-manipulated device clocks.
- **Safe Error Handling**: Uses a `Result` wrapper (`ChronoResult`) to prevent crashes from parsing or network errors, providing clear error messages instead.
- **Handy Utilities**: Includes functions for relative time formatting (e.g., "5 minutes ago") and simple date manipulation.

## Setup

_To be added: Instructions for adding the library as a dependency via Gradle._

```groovy
// build.gradle (Module)
dependencies {
    // implementation 'com.chronomaster:library:1.0.0'
}
```

## Usage

### 1. Initialization

It's highly recommended to initialize ChronoMaster once in your `Application` class. This sets up the default timezones for all formatting operations.

```kotlin
import com.chronomaster.library.ChronoMaster
import android.app.Application

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Set the default input timezone (e.g., from your backend) and output timezone.
        ChronoMaster.initialize(
            inputTimeZoneId = "UTC",
            outputTimeZoneId = "Asia/Kolkata" // Example: Indian Standard Time
        )
    }
}
```

### 2. Smart Date Formatting

The magic of ChronoMaster. Just provide the date string and your desired output format.

```kotlin
val isoDate = "2025-10-31T12:30:00Z"
val slashDate = "31/10/2025"
val epochSeconds = "1730389800"

// Using global defaults set during initialization
val result1 = ChronoMaster.formatDate(isoDate, "dd MMM, yyyy 'at' hh:mm a")
val result2 = ChronoMaster.formatDate(slashDate, "EEEE, MMMM d, yyyy")
val result3 = ChronoMaster.formatDate(epochSeconds, "dd/MM/yyyy")

// Handle the result safely
when (result1) {
    is ChronoResult.Success -> textView.text = result1.data // "31 Oct, 2025 at 06:00 PM"
    is ChronoResult.Error -> showToast(result1.message)
}
```

### 3. Custom Timezone Formatting

You can easily override the global defaults for specific cases.

```kotlin
val pstDate = "2025-10-31T10:00:00" // A date from a server in Los Angeles

val result = ChronoMaster.formatDate(
    dateString = pstDate,
    outputFormat = "hh:mm a",
    inputTimeZoneId = "America/Los_Angeles", // PST
    outputTimeZoneId = "Asia/Jerusalem"     // IDT
)

// Handle the result
if (result is ChronoResult.Success) {
    // result.data will be "08:00 PM"
}
```

### 4. Getting True Network Time

Fetch a trustworthy timestamp asynchronously. This is a `suspend` function and should be called from a coroutine.

```kotlin
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

// In an Activity or ViewModel
lifecycleScope.launch {
    when (val result = ChronoMaster.getTrueTime()) {
        is ChronoResult.Success -> {
            val trueTimestamp = result.data
            // Now you have a reliable timestamp to work with.
        }
        is ChronoResult.Error -> {
            // Handle network error, device might be offline.
            showToast(result.message)
        }
    }
}
```

### 5. Utility Functions

#### Relative Time Formatting

```kotlin
// Assuming you have an epoch timestamp in milliseconds
val fiveMinutesAgo = System.currentTimeMillis() - (5 * 60 * 1000)
val twoWeeksFromNow = System.currentTimeMillis() + (14 * 24 * 60 * 60 * 1000)

val relativeTime1 = ChronoMaster.toRelativeTime(fiveMinutesAgo) // "5 minutes ago"
val relativeTime2 = ChronoMaster.toRelativeTime(twoWeeksFromNow) // "In 2 weeks"
```

#### Date Manipulation

```kotlin
import java.util.Date

val today = Date()
val fiveDaysLater = ChronoMaster.plusDays(today, 5)
val tenDaysAgo = ChronoMaster.minusDays(today, 10)
```

## Error Handling

ChronoMaster functions never throw exceptions directly. They return a `ChronoResult` object. Always check if the result is `Success` or `Error` to handle outcomes gracefully and avoid crashing your app.

```kotlin
val badDate = "this is not a date"
val result = ChronoMaster.formatDate(badDate, "dd/MM/yyyy")

when (result) {
    is ChronoResult.Success -> {
        // This block will not be executed
    }
    is ChronoResult.Error -> {
        // The error message provides details on why parsing failed.
        Log.e("ChronoMaster", result.message)
        // e.g., "Failed to parse date string: 'this is not a date'. None of the supported formats matched."
    }
}
```

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License

[Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)
