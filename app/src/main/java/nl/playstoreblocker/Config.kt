package nl.playstoreblocker

object Config {
    const val TAG = "PSBlocker"

    const val PLAY_STORE_PACKAGE = "com.android.vending"

    /** Pakketten die alleen binnen een ontgrendelvenster open mogen. */
    val BLOCKED_PACKAGES: Set<String> = setOf(PLAY_STORE_PACKAGE)

    /** Standaardduur in minuten zolang de ouder er geen eigen waarde voor heeft ingesteld. */
    const val FALLBACK_UNLOCK_MINUTES = 10

    /** Maximaal aantal minuten dat de ouder kan invullen. */
    const val MAX_UNLOCK_MINUTES = 180
}
