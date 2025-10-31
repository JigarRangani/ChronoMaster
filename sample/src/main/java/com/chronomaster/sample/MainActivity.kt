package com.chronomaster.sample

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.chronomaster.library.ChronoMaster
import com.chronomaster.library.ChronoResult
import com.chronomaster.sample.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ChronoMaster with a custom parser for a unique date format.
        ChronoMaster.initialize(
            inputTimeZoneId = "UTC",
            outputTimeZoneId = "America/New_York", // EST as default output
            customParsers = listOf("yyyy/MM/dd HH:mm:ss.SSS")
        )

        binding.runTestsButton.setOnClickListener {
            runAllTests()
        }
    }

    private fun runAllTests() {
        // 1. Test standard date formats with the new java.time API
        testStandardFormats()
        // 2. Test the new localization feature
        testLocalization()
        // 3. Test the custom parser we registered during initialization
        testCustomParser()
        // 4. Test NTP "True Time" which now returns an Instant
        fetchAndDisplayNtpTime()
        // 5. Test Relative Time
        testRelativeTime()
    }

    private fun testStandardFormats() {
        // Standard ISO format
        displayResult(binding.isoResultTextView) {
            ChronoMaster.formatDate("2025-10-31T12:30:00Z", "dd MMM, yyyy 'at' hh:mm a")
        }
        // Slash format
        displayResult(binding.slashResultTextView) {
            ChronoMaster.formatDate("31/10/2025", "EEEE, MMMM d, yyyy")
        }
        // Epoch in seconds
        displayResult(binding.epochResultTextView) {
            ChronoMaster.formatDate("1730389800", "dd/MM/yyyy")
        }
    }

    private fun testLocalization() {
        // Format a date using a standard style for a specific locale (French)
        displayResult(binding.localizedResultTextView) {
            ChronoMaster.formatDate(
                dateString = "2025-10-31T12:30:00Z",
                formatStyle = FormatStyle.FULL,
                locale = Locale.FRANCE
            )
        }
    }

    private fun testCustomParser() {
        // This format ("yyyy/MM/dd HH:mm:ss.SSS") would fail without our custom parser.
        displayResult(binding.customParserResultTextView) {
            ChronoMaster.formatDate("2025/10/31 10:00:00.123", "HH:mm:ss")
        }
    }

    private fun fetchAndDisplayNtpTime() {
        lifecycleScope.launch {
            // 1. Call the suspend function directly in the launch scope
            val result = ChronoMaster.getTrueTime()

            // 2. Now, pass the *result* to displayResult. The lambda
            //    will only handle formatting, not the network call.
            displayResult(binding.ntpTimeResultTextView) {
                when (result) { // Use the 'result' variable from above
                    is ChronoResult.Success -> {
                        // The result is an Instant, let's format it.
                        val instant = result.data
                        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss z")
                            .withZone(java.time.ZoneId.systemDefault()) // Format it in the device's timezone
                        ChronoResult.Success(formatter.format(instant))
                    }
                    is ChronoResult.Error -> result
                }
            }
        }
    }

    private fun testRelativeTime() {
        val fiveMinutesAgo = System.currentTimeMillis() - (5 * 60 * 1000)
        binding.relativeTimeResultTextView.text = "${binding.relativeTimeResultTextView.tag}: ${ChronoMaster.toRelativeTime(fiveMinutesAgo)}"
    }

    /**
     * A helper function to reduce boilerplate code for displaying results in TextViews.
     */
    private fun displayResult(textView: TextView, action: () -> ChronoResult<String>) {
        when (val result = action()) {
            is ChronoResult.Success -> textView.text = "${textView.tag} ${result.data}"
            is ChronoResult.Error -> textView.text = "${textView.tag} Error: ${result.message}"
        }
    }
}
