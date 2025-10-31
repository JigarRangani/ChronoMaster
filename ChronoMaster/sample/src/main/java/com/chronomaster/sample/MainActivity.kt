package com.chronomaster.sample

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.chronomaster.library.ChronoMaster
import com.chronomaster.library.ChronoResult
import com.chronomaster.sample.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.runTestsButton.setOnClickListener {
            runAllTests()
        }
    }

    private fun runAllTests() {
        // 1. Test standard date formats
        testFormat("2025-10-31T12:30:00Z", "dd MMM, yyyy 'at' hh:mm a", binding.isoResultTextView)
        testFormat("31/10/2025", "EEEE, MMMM d, yyyy", binding.slashResultTextView)
        testFormat("1730389800", "dd/MM/yyyy", binding.epochResultTextView) // Epoch in seconds

        // 2. Test custom timezone formatting
        testCustomTimezoneFormat()

        // 3. Test NTP "True Time"
        fetchAndDisplayNtpTime()

        // 4. Test Relative Time
        testRelativeTime()
    }

    private fun testFormat(dateString: String, outputFormat: String, resultView: TextView) {
        when (val result = ChronoMaster.formatDate(dateString, outputFormat)) {
            is ChronoResult.Success -> resultView.text = "${resultView.tag}: ${result.data}"
            is ChronoResult.Error -> resultView.text = "${resultView.tag}: Error: ${result.message}"
        }
    }

    private fun testCustomTimezoneFormat() {
        val resultView = binding.customTimezoneResultTextView
        val pstDate = "2025-10-31T10:00:00" // A date from a server in Los Angeles
        when (val result = ChronoMaster.formatDate(
            dateString = pstDate,
            outputFormat = "hh:mm a",
            inputTimeZoneId = "America/Los_Angeles", // PST
            outputTimeZoneId = "Asia/Jerusalem"     // IDT
        )) {
            is ChronoResult.Success -> resultView.text = "${resultView.tag}: ${result.data}"
            is ChronoResult.Error -> resultView.text = "${resultView.tag}: Error: ${result.message}"
        }
    }

    private fun fetchAndDisplayNtpTime() {
        val resultView = binding.ntpTimeResultTextView
        lifecycleScope.launch {
            when (val result = ChronoMaster.getTrueTime()) {
                is ChronoResult.Success -> {
                    // Display the timestamp formatted as a readable date
                    val formattedNtpTime = ChronoMaster.formatDate(result.data.toString(), "dd MMM yyyy HH:mm:ss")
                    if (formattedNtpTime is ChronoResult.Success) {
                        resultView.text = "${resultView.tag}: ${formattedNtpTime.data}"
                    } else {
                        resultView.text = "${resultView.tag}: Could not format NTP time."
                    }
                }
                is ChronoResult.Error -> resultView.text = "${resultView.tag}: Error: ${result.message}"
            }
        }
    }

    private fun testRelativeTime() {
        val resultView = binding.relativeTimeResultTextView
        val fiveMinutesAgo = System.currentTimeMillis() - (5 * 60 * 1000)
        val relativeTime = ChronoMaster.toRelativeTime(fiveMinutesAgo)
        resultView.text = "${resultView.tag}: (5 mins ago) -> $relativeTime"
    }
}
