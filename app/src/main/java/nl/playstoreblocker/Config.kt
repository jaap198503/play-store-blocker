package nl.playstoreblocker

object Config {
    const val TAG = "PSBlocker"

    const val PLAY_STORE_PACKAGE = "com.android.vending"

    /** Pakketten die alleen binnen een ontgrendelvenster open mogen. */
    val BLOCKED_PACKAGES: Set<String> = setOf(PLAY_STORE_PACKAGE)

    /** Standaardduur in minuten; instelbaar via unlockMinutes in blocker.properties. */
    const val DEFAULT_UNLOCK_MINUTES = BuildConfig.UNLOCK_MINUTES

    /** Maximaal aantal minuten dat de ouder kan invullen op het pincodescherm. */
    const val MAX_UNLOCK_MINUTES = 180
}
