package nl.playstoreblocker

import java.security.MessageDigest
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Controleert een pincode tegen de salted PBKDF2-hash uit BuildConfig (zie tools/New-PinHash.ps1).
 * De pincode zelf staat nergens in de app.
 */
object PinVerifier {

    fun verify(pin: String): Boolean {
        val salt = fromHex(BuildConfig.PIN_SALT)
        val expected = fromHex(BuildConfig.PIN_HASH)
        val spec = PBEKeySpec(pin.toCharArray(), salt, BuildConfig.PIN_ITERATIONS, expected.size * 8)
        return try {
            val actual = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec)
                .encoded
            MessageDigest.isEqual(actual, expected)
        } finally {
            spec.clearPassword()
        }
    }

    private fun fromHex(hex: String): ByteArray =
        ByteArray(hex.length / 2) { i -> hex.substring(2 * i, 2 * i + 2).toInt(16).toByte() }
}
