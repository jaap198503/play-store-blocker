package nl.playstoreblocker

/** Startscherm van de app: de ouder geeft hier het kind tijdelijk toegang tot de Play Store. */
class MainActivity : BlockActivity() {
    override val isMainScreen: Boolean get() = true
}
