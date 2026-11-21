package nl.playstoreblocker

import android.content.Context
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Bewaart de pincode als salted PBKDF2-hash (SHA-256) in de privéopslag van de app, plus de
 * standaardduur van het ontgrendelvenster. De pincode zelf wordt nergens opgeslagen.
 *
 * De pincode wordt op het toestel ingesteld (SettingsActivity); er zit er dus geen in de APK.
 */
object PinStore {
    private const val PREFS = "pin_store"
    private const val KEY_SALT = "salt"
    private const val KEY_HASH = "hash"
    private const val KEY_ITERATIONS = "iterations"
    private const val KEY_DEFAULT_MINUTES = "default_minutes"

    private const val ITERATIONS = 120_000
    private const val SALT_BYTES = 16
    private const val HASH_BITS = 256

    fun isSet(context: Context): Boolean = prefs(context).contains(KEY_HASH)

    fun defaultMinutes(context: Context): Int =
        prefs(context).getInt(KEY_DEFAULT_MINUTES, Config.FALLBACK_UNLOCK_MINUTES)

    fun save(context: Context, pin: String, defaultMinutes: Int) {
        val salt = ByteArray(SALT_BYTES).also { SecureRandom().nextBytes(it) }
        val hash = derive(pin, salt, ITERATIONS)
        prefs(context).edit()
            .putString(KEY_SALT, toHex(salt))
            .putString(KEY_HASH, toHex(hash))
            .putInt(KEY_ITERATIONS, ITERATIONS)
            .putInt(KEY_DEFAULT_MINUTES, defaultMinutes.coerceIn(1, Config.MAX_UNLOCK_MINUTES))
            .commit()
    }

    /** Alleen de standaardduur wijzigen, zonder de pincode aan te raken. */
    fun saveDefaultMinutes(context: Context, defaultMinutes: Int) {
        prefs(context).edit()
            .putInt(KEY_DEFAULT_MINUTES, defaultMinutes.coerceIn(1, Config.MAX_UNLOCK_MINUTES))
            .commit()
    }

    /** Duurt even (PBKDF2); niet op de hoofdthread aanroepen. */
    fun matches(context: Context, pin: String): Boolean {
        val prefs = prefs(context)
        val saltHex = prefs.getString(KEY_SALT, null) ?: return false
        val hashHex = prefs.getString(KEY_HASH, null) ?: return false
        val iterations = prefs.getInt(KEY_ITERATIONS, ITERATIONS)
        val actual = derive(pin, fromHex(saltHex), iterations)
        return MessageDigest.isEqual(actual, fromHex(hashHex))
    }

    private fun derive(pin: String, salt: ByteArray, iterations: Int): ByteArray {
        val spec = PBEKeySpec(pin.toCharArray(), salt, iterations, HASH_BITS)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun toHex(bytes: ByteArray): String = bytes.joinToString("") { "%02x".format(it) }

    private fun fromHex(hex: String): ByteArray =
        ByteArray(hex.length / 2) { i -> hex.substring(2 * i, 2 * i + 2).toInt(16).toByte() }
}
