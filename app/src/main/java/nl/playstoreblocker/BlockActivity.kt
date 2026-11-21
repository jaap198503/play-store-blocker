package nl.playstoreblocker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.util.concurrent.Executors
import kotlin.math.ceil

/** Pincodescherm dat verschijnt als iemand de Play Store probeert te openen. */
class BlockActivity : Activity() {

    private lateinit var pinInput: EditText
    private lateinit var minutesInput: EditText
    private lateinit var errorText: TextView
    private lateinit var unlockButton: Button

    // PBKDF2 duurt even; niet op de hoofdthread.
    private val worker = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block)

        pinInput = findViewById(R.id.pin_input)
        minutesInput = findViewById(R.id.minutes_input)
        errorText = findViewById(R.id.pin_error)
        unlockButton = findViewById(R.id.unlock_button)

        minutesInput.setText(Config.DEFAULT_UNLOCK_MINUTES.toString())
        unlockButton.setOnClickListener { submit() }
        findViewById<Button>(R.id.back_button).setOnClickListener { finish() }
        minutesInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submit()
                true
            } else {
                false
            }
        }
    }

    private fun submit() {
        val lockedMs = PinAttempts.lockedForMs()
        if (lockedMs > 0L) {
            showError(lockedOutMessage(lockedMs))
            return
        }
        val pin = pinInput.text.toString()
        if (pin.isEmpty()) return

        // Leeg of ongeldig veld valt terug op de standaardduur; te hoge waarden worden afgekapt.
        val minutes = (minutesInput.text.toString().toIntOrNull() ?: Config.DEFAULT_UNLOCK_MINUTES)
            .coerceIn(1, Config.MAX_UNLOCK_MINUTES)

        unlockButton.isEnabled = false
        worker.execute {
            val correct = PinVerifier.verify(pin)
            runOnUiThread {
                if (isFinishing || isDestroyed) return@runOnUiThread
                unlockButton.isEnabled = true
                if (correct) onCorrectPin(minutes) else onWrongPin()
            }
        }
    }

    private fun onCorrectPin(minutes: Int) {
        PinAttempts.reset()
        UnlockState.unlock(this, minutes)
        BlockerAccessibilityService.instance?.onUnlocked()
        Log.i(Config.TAG, "correct pin; play store open for $minutes min")

        val launch = packageManager.getLaunchIntentForPackage(Config.PLAY_STORE_PACKAGE)
        if (launch != null) {
            startActivity(launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } else {
            Log.w(Config.TAG, "no launch intent for ${Config.PLAY_STORE_PACKAGE}")
        }
        finish()
    }

    private fun onWrongPin() {
        PinAttempts.recordFailure()
        pinInput.text.clear()
        val lockedMs = PinAttempts.lockedForMs()
        Log.i(Config.TAG, "wrong pin; locked for ${lockedMs / 1000}s")
        showError(
            if (lockedMs > 0L) lockedOutMessage(lockedMs) else getString(R.string.block_wrong_pin)
        )
    }

    private fun lockedOutMessage(lockedMs: Long): String =
        getString(R.string.block_locked_out, ceil(lockedMs / 1000.0).toInt())

    private fun showError(message: String) {
        errorText.text = message
        errorText.visibility = TextView.VISIBLE
    }

    /** Verlaat de gebruiker het scherm (home, recents, scherm uit), dan sluiten we het ook. */
    override fun onStop() {
        super.onStop()
        if (!isFinishing) finish()
    }

    override fun onDestroy() {
        worker.shutdown()
        super.onDestroy()
    }
}
