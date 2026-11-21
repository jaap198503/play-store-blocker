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

/**
 * Pincodescherm dat verschijnt als iemand de Play Store probeert te openen.
 * MainActivity erft hiervan en is hetzelfde scherm als startscherm van de ouder (met een knop
 * naar de instellingen): de standaardhandeling is tijdelijk toegang geven.
 */
open class BlockActivity : Activity() {

    /** True voor het startscherm van de app (MainActivity), false voor het blokkeerscherm. */
    protected open val isMainScreen: Boolean get() = false

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

        minutesInput.setText(PinStore.defaultMinutes(this).toString())
        unlockButton.setOnClickListener { submit() }
        findViewById<Button>(R.id.back_button).setOnClickListener { finish() }

        if (isMainScreen) {
            findViewById<TextView>(R.id.block_title).setText(R.string.main_title)
            findViewById<TextView>(R.id.block_subtitle).setText(R.string.main_subtitle)
            findViewById<Button>(R.id.settings_button).apply {
                visibility = Button.VISIBLE
                setOnClickListener { startActivity(Intent(this@BlockActivity, SettingsActivity::class.java)) }
            }
            // Eerste keer openen zonder pincode: ga naar de instellingen. Alleen bij een verse start,
            // anders zou je daar niet meer terug kunnen.
            if (savedInstanceState == null && !PinStore.isSet(this)) {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
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
        if (!PinStore.isSet(this)) {
            showError(getString(R.string.main_no_pin))
            return
        }
        val pin = pinInput.text.toString()
        Log.i(Config.TAG, "submit; pin length=${pin.length}")
        if (pin.isEmpty()) {
            showError(getString(R.string.block_pin_empty))
            return
        }

        // Leeg of ongeldig veld valt terug op de standaardduur; te hoge waarden worden afgekapt.
        val minutes = (minutesInput.text.toString().toIntOrNull() ?: PinStore.defaultMinutes(this))
            .coerceIn(1, Config.MAX_UNLOCK_MINUTES)

        unlockButton.isEnabled = false
        val appContext = applicationContext
        worker.execute {
            val correct = PinStore.matches(appContext, pin)
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

    /**
     * Verlaat de gebruiker het blokkeerscherm (home, recents, scherm uit), dan sluiten we het ook.
     * Het startscherm blijft staan, zodat je vanuit de instellingen weer terug kunt.
     */
    override fun onStop() {
        super.onStop()
        if (!isFinishing && !isMainScreen) finish()
    }

    override fun onDestroy() {
        worker.shutdown()
        super.onDestroy()
    }
}
