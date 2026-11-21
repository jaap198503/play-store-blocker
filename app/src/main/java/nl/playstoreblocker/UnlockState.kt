package nl.playstoreblocker

import android.content.Context
import android.os.SystemClock
import android.provider.Settings

/**
 * Bewaart wanneer de Play Store voor het laatst is ontgrendeld.
 *
 * Het venster loopt op SystemClock.elapsedRealtime(): die telt ook diepe slaap mee en
 * verandert niet als iemand de klok verzet. elapsedRealtime begint na een herstart weer bij 0,
 * daarom bewaren we ook de boot-teller. Een ontgrendeling van vóór een herstart telt dan niet meer
 * (de blokkade valt dus altijd terug op "dicht").
 */
object UnlockState {
    private const val PREFS = "unlock_state"
    private const val KEY_UNLOCKED_AT = "unlocked_at_elapsed"
    private const val KEY_BOOT_COUNT = "boot_count"

    fun unlock(context: Context) {
        prefs(context).edit()
            .putLong(KEY_UNLOCKED_AT, SystemClock.elapsedRealtime())
            .putInt(KEY_BOOT_COUNT, bootCount(context))
            .commit()
    }

    /** Resterende milliseconden van het ontgrendelvenster; 0 als de Play Store dicht hoort te zijn. */
    fun remainingMs(context: Context): Long {
        val prefs = prefs(context)
        val unlockedAt = prefs.getLong(KEY_UNLOCKED_AT, -1L)
        if (unlockedAt < 0L) return 0L
        if (prefs.getInt(KEY_BOOT_COUNT, -1) != bootCount(context)) return 0L

        val elapsed = SystemClock.elapsedRealtime() - unlockedAt
        if (elapsed < 0L) return 0L
        return (Config.UNLOCK_DURATION_MS - elapsed).coerceAtLeast(0L)
    }

    fun isUnlocked(context: Context): Boolean = remainingMs(context) > 0L

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun bootCount(context: Context): Int =
        Settings.Global.getInt(context.contentResolver, Settings.Global.BOOT_COUNT, -1)
}
