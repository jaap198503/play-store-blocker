package nl.playstoreblocker

object Config {
    const val TAG = "PSBlocker"

    const val PLAY_STORE_PACKAGE = "com.android.vending"

    /** Pakketten die alleen binnen een ontgrendelvenster open mogen. */
    val BLOCKED_PACKAGES: Set<String> = setOf(PLAY_STORE_PACKAGE)

    /** Duur van het ontgrendelvenster; instelbaar via unlockMinutes in blocker.properties. */
    val UNLOCK_DURATION_MS: Long = BuildConfig.UNLOCK_MINUTES * 60_000L
}
