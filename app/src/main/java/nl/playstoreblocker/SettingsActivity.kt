package nl.playstoreblocker

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.util.concurrent.Executors
import kotlin.math.ceil

/**
 * Startscherm van de app (voor de ouder): status van pincode en service, snelkoppelingen naar de
 * juiste Android-instellingen, en het instellen of wijzigen van de pincode en standaardduur.
 * Wijzigen kan alleen met de huidige pincode.
 */
class SettingsActivity : Activity() {

    private lateinit var statusPin: TextView
    private lateinit var statusService: TextView
    private lateinit var sectionTitle: TextView
    private lateinit var currentPinInput: EditText
    private lateinit var newPinInput: EditText
    private lateinit var confirmPinInput: EditText
    private lateinit var minutesInput: EditText
    private lateinit var messageText: TextView
    private lateinit var saveButton: Button

    // PBKDF2 duurt even; niet op de hoofdthread.
    private val worker = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        statusPin = findViewById(R.id.status_pin)
        statusService = findViewById(R.id.status_service)
        sectionTitle = findViewById(R.id.section_title)
        currentPinInput = findViewById(R.id.current_pin_input)
        newPinInput = findViewById(R.id.new_pin_input)
        confirmPinInput = findViewById(R.id.confirm_pin_input)
        minutesInput = findViewById(R.id.default_minutes_input)
        messageText = findViewById(R.id.message)
        saveButton = findViewById(R.id.save_button)

        minutesInput.setText(PinStore.defaultMinutes(this).toString())

        findViewById<Button>(R.id.open_accessibility_button).setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        findViewById<Button>(R.id.open_app_settings_button).setOnClickListener {
            startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
            )
        }
        saveButton.setOnClickListener { save() }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    override fun onDestroy() {
        worker.shutdown()
        super.onDestroy()
    }

    private fun refresh() {
        val pinSet = PinStore.isSet(this)
        val serviceOn = isServiceEnabled()

        statusPin.setText(if (pinSet) R.string.setup_status_pin_set else R.string.setup_status_pin_missing)
        statusService.setText(if (serviceOn) R.string.setup_status_service_on else R.string.setup_status_service_off)
        statusPin.setTextColor(if (pinSet) OK_COLOR else WARN_COLOR)
        statusService.setTextColor(if (serviceOn) OK_COLOR else WARN_COLOR)

        sectionTitle.setText(if (pinSet) R.string.setup_section_change else R.string.setup_section_set)
        currentPinInput.visibility = if (pinSet) TextView.VISIBLE else TextView.GONE
    }

    private fun isServiceEnabled(): Boolean {
        val enabled = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            .orEmpty()
        val me = ComponentName(this, BlockerAccessibilityService::class.java)
        return enabled.split(':').any { ComponentName.unflattenFromString(it) == me }
    }

    private fun save() {
        val newPin = newPinInput.text.toString()
        val confirm = confirmPinInput.text.toString()
        val minutes = (minutesInput.text.toString().toIntOrNull() ?: Config.FALLBACK_UNLOCK_MINUTES)
            .coerceIn(1, Config.MAX_UNLOCK_MINUTES)
        val pinSet = PinStore.isSet(this)
        val changingPin = !pinSet || newPin.isNotEmpty() || confirm.isNotEmpty()

        if (changingPin) {
            if (!PIN_FORMAT.matches(newPin)) {
                showMessage(getString(R.string.setup_error_pin_format), isError = true)
                return
            }
            if (newPin != confirm) {
                showMessage(getString(R.string.setup_error_mismatch), isError = true)
                return
            }
        }

        val current = currentPinInput.text.toString()
        if (pinSet) {
            val lockedMs = PinAttempts.lockedForMs()
            if (lockedMs > 0L) {
                showMessage(lockedOutMessage(lockedMs), isError = true)
                return
            }
            if (current.isEmpty()) {
                showMessage(getString(R.string.setup_error_current_wrong), isError = true)
                return
            }
        }

        saveButton.isEnabled = false
        val appContext = applicationContext
        worker.execute {
            val authorized = !pinSet || PinStore.matches(appContext, current)
            if (authorized) {
                if (changingPin) {
                    PinStore.save(appContext, newPin, minutes)
                } else {
                    PinStore.saveDefaultMinutes(appContext, minutes)
                }
            }
            runOnUiThread {
                if (isFinishing || isDestroyed) return@runOnUiThread
                saveButton.isEnabled = true
                if (authorized) {
                    PinAttempts.reset()
                    currentPinInput.text.clear()
                    newPinInput.text.clear()
                    confirmPinInput.text.clear()
                    minutesInput.setText(minutes.toString())
                    showMessage(getString(R.string.setup_saved), isError = false)
                    refresh()
                } else {
                    PinAttempts.recordFailure()
                    currentPinInput.text.clear()
                    val lockedMs = PinAttempts.lockedForMs()
                    showMessage(
                        if (lockedMs > 0L) lockedOutMessage(lockedMs) else getString(R.string.setup_error_current_wrong),
                        isError = true,
                    )
                }
            }
        }
    }

    private fun lockedOutMessage(lockedMs: Long): String =
        getString(R.string.block_locked_out, ceil(lockedMs / 1000.0).toInt())

    private fun showMessage(text: String, isError: Boolean) {
        messageText.text = text
        messageText.setTextColor(if (isError) WARN_COLOR else OK_COLOR)
        messageText.visibility = TextView.VISIBLE
    }

    private companion object {
        val PIN_FORMAT = Regex("^\\d{4,12}$")
        val OK_COLOR = Color.parseColor("#2E7D32")
        val WARN_COLOR = Color.parseColor("#C62828")
    }
}
