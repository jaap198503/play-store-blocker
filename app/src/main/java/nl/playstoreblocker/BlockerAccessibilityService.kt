package nl.playstoreblocker

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import nl.playstoreblocker.Config.TAG

/**
 * Blokkeert de Play Store: zodra een venster van een geblokkeerd pakket voorop komt en er geen
 * actief ontgrendelvenster is, gaan we naar het startscherm en tonen we het pincodescherm.
 */
class BlockerAccessibilityService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())
    private val expiryCheck = Runnable { checkAtExpiry() }

    private var lastBlockAt = 0L
    private var expiryRetries = 0

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.i(TAG, "service connected; unlock window = ${BuildConfig.UNLOCK_MINUTES} min")
        // De service kan herstarten terwijl een ontgrendelvenster nog loopt.
        scheduleExpiryCheck()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = event.packageName?.toString() ?: return
        Log.d(TAG, "window: $pkg / ${event.className}")

        if (pkg in Config.BLOCKED_PACKAGES && !UnlockState.isUnlocked(this)) {
            block("window event from $pkg")
        }
    }

    override fun onInterrupt() = Unit

    override fun onUnbind(intent: Intent?): Boolean {
        Log.w(TAG, "service unbound")
        if (instance === this) instance = null
        handler.removeCallbacks(expiryCheck)
        return super.onUnbind(intent)
    }

    /** Aangeroepen door BlockActivity na een juiste pincode. */
    fun onUnlocked() {
        scheduleExpiryCheck()
    }

    private fun scheduleExpiryCheck() {
        handler.removeCallbacks(expiryCheck)
        val remaining = UnlockState.remainingMs(this)
        if (remaining > 0L) {
            expiryRetries = 0
            handler.postDelayed(expiryCheck, remaining + EXPIRY_MARGIN_MS)
            Log.i(TAG, "unlock window closes in ${remaining / 1000}s")
        }
    }

    /**
     * Het venster is verlopen. Staat de Play Store nog voorop, dan komt er geen venster-event
     * meer vanzelf, dus kijken we zelf welke app voorop staat.
     */
    private fun checkAtExpiry() {
        if (UnlockState.isUnlocked(this)) {
            scheduleExpiryCheck()
            return
        }
        val foreground = rootInActiveWindow?.packageName?.toString()
        Log.i(TAG, "unlock window expired; foreground=$foreground")
        when {
            foreground != null && foreground in Config.BLOCKED_PACKAGES -> block("unlock window expired")
            foreground == null && expiryRetries < MAX_EXPIRY_RETRIES -> {
                expiryRetries++
                handler.postDelayed(expiryCheck, EXPIRY_RETRY_MS)
            }
        }
    }

    private fun block(reason: String) {
        val now = SystemClock.elapsedRealtime()
        if (now - lastBlockAt < BLOCK_DEBOUNCE_MS) return
        lastBlockAt = now

        Log.i(TAG, "blocking: $reason")
        performGlobalAction(GLOBAL_ACTION_HOME)
        // Pas na de HOME-overgang starten, anders overdekt het startscherm het pincodescherm.
        handler.postDelayed({ showBlockScreen() }, SHOW_BLOCK_DELAY_MS)
    }

    private fun showBlockScreen() {
        try {
            startActivity(Intent(this, BlockActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (e: Exception) {
            Log.e(TAG, "could not start BlockActivity", e)
        }
    }

    companion object {
        private const val BLOCK_DEBOUNCE_MS = 400L
        private const val SHOW_BLOCK_DELAY_MS = 350L
        private const val EXPIRY_MARGIN_MS = 250L
        private const val EXPIRY_RETRY_MS = 1_000L
        private const val MAX_EXPIRY_RETRIES = 3

        @Volatile
        var instance: BlockerAccessibilityService? = null
            private set
    }
}
