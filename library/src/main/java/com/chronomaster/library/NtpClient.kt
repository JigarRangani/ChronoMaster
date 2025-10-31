package com.chronomaster.library

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.net.ntp.NTPUDPClient
import org.apache.commons.net.ntp.TimeInfo
import java.net.InetAddress
import java.time.Instant

/**
 * An object responsible for fetching the current "true" time from a Network Time Protocol (NTP) server.
 * This is useful for scenarios where the device's local clock may be inaccurate or tampered with.
 */
object NtpClient {

    private const val DEFAULT_NTP_POOL = "pool.ntp.org"
    private const val DEFAULT_TIMEOUT_MS = 10_000

    /**
     * Asynchronously fetches the current network time from an NTP server.
     *
     * @param ntpPool The address of the NTP server pool to use. Defaults to "pool.ntp.org".
     * @return A [ChronoResult] containing the current time as a `java.time.Instant` on success,
     *         or an error message if the request fails.
     */
    suspend fun getTrueTime(ntpPool: String = DEFAULT_NTP_POOL): ChronoResult<Instant> = withContext(Dispatchers.IO) {
        val client = NTPUDPClient()
        client.defaultTimeout = DEFAULT_TIMEOUT_MS

        return@withContext try {
            val inetAddress = InetAddress.getByName(ntpPool)
            val timeInfo: TimeInfo = client.getTime(inetAddress)
            timeInfo.computeDetails() // Computes the round trip delay and clock offset

            val offset = timeInfo.offset
            val currentTime = System.currentTimeMillis()
            val trueTimeMillis = currentTime + offset

            ChronoResult.Success(Instant.ofEpochMilli(trueTimeMillis))
        } catch (e: Exception) {
            ChronoResult.Error("Failed to retrieve NTP time. Device may be offline or NTP pool is unreachable.", e)
        }
    }
}
