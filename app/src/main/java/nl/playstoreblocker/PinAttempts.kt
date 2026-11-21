package nl.playstoreblocker

import android.os.SystemClock

/**
 * Houdt foute pogingen bij: na 5 keer fout een wachttijd die per volgende fout verdubbelt
 * (30 s, 60 s, ... maximaal 10 minuten). Staat alleen in het geheugen van het proces.
 */
object PinAttempts {
    private const val FREE_ATTEMPTS = 5
    private const val BASE_LOCK_MS = 30_000L
    private const val MAX_LOCK_MS = 10 * 60_000L

    private var failures = 0
    private var lockedUntil = 0L

    @Synchronized
    fun lockedForMs(): Long = (lockedUntil - SystemClock.elapsedRealtime()).coerceAtLeast(0L)

    @Synchronized
    fun recordFailure() {
        failures++
        if (failures >= FREE_ATTEMPTS) {
            val step = (failures - FREE_ATTEMPTS).coerceAtMost(10)
            val lockMs = (BASE_LOCK_MS shl step).coerceAtMost(MAX_LOCK_MS)
            lockedUntil = SystemClock.elapsedRealtime() + lockMs
        }
    }

    @Synchronized
    fun reset() {
        failures = 0
        lockedUntil = 0L
    }
}
