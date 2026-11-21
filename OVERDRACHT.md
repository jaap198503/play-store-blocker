# Overdracht: Play Store-blokkade voor kindertablet

## Doel
Een Android-app die de Google Play Store **altijd blokkeert**, behalve na invoer van een pincode. Na de pincode is de Play Store een korte tijd toegankelijk (bijv. 10 minuten), daarna weer dicht.

## Besluiten (al genomen)
- **Route 2**: eigen Android-app (Kotlin) met een `AccessibilityService`. Geen Device Owner/MDM.
- Play Store is **altijd** dicht, dus geen tijdlimiet-logica nodig.
- Toegang alleen via pincode. Alleen een **hash** (met salt) in de app bewaren, geen platte tekst. Dit is afschrikking, geen harde beveiliging: een handige tiener kan een APK uitlezen.
- Na een juiste pincode: tijdelijk venster (voorstel 10 min, configureerbaar in code), daarna weer blokkeren.
- Er is nog **niets gebouwd**. Er is geen repo en er is geen code.

## Apparaat
- Samsung Galaxy Tab A9.
- Kinderen hebben een eigen kinderaccount met Google Family Link.
- Family Link kan de Play Store niet blokkeren en geeft de tijdlimiet niet door aan andere apps. Daarom blokkeren we zelf.

## Voorbereiding op de tablet (door de ouder)
1. Ontwikkelaarsopties aan (7x tikken op Buildnummer) en USB-foutopsporing aan.
2. **Let op**: het Family Link-kinderaccount kan ontwikkelaarsopties of foutopsporing blokkeren. Dan tijdelijk toestaan, of onder het ouderprofiel doen.
3. Samsung **Auto Blocker** uitzetten (Instellingen > Beveiliging en privacy) tijdens installeren. Die blokkeert apps van buiten de Play Store.
4. Batterij-optimalisatie voor onze app op "Onbeperkt" zetten, anders legt Samsung de service stil.
5. Na installatie: toegankelijkheidsservice handmatig inschakelen (Instellingen > Toegankelijkheid > Geïnstalleerde apps).
6. Controleer met `adb devices` dat de tablet zichtbaar is.

## Ontwerp
- `AccessibilityService` luistert naar `TYPE_WINDOW_STATE_CHANGED`. Bij package `com.android.vending` en geen actief ontgrendelvenster: `performGlobalAction(GLOBAL_ACTION_HOME)` en blokscherm tonen.
- Blokscherm: eigen Activity met pincode-invoer. Juiste pincode: ontgrendeltijdstip opslaan (bijv. in SharedPreferences) en Play Store starten.
- Ontgrendelvenster is gebaseerd op `SystemClock.elapsedRealtime()`, zodat het niet te omzeilen is door de klok te verzetten.
- Beschermen tegen verwijderen: onderzoeken of Device Admin (verwijderen lastiger) en/of Family Link-afscherming van toegankelijkheidsinstellingen werkt.
- Open vraag: kan het kind de toegankelijkheidsservice uitzetten? Testen met het echte kinderaccount.

## Lokale werkwijze
- Sessie op de laptop met Android Studio of alleen command-line SDK (JDK 17, `adb`).
- Tablet via USB of draadloos debuggen verbonden.
- Bouwen en installeren met `./gradlew installDebug`. Controleren met `adb logcat`.
- Er is geen ander pad voor testen, dus de tablet moet aan de laptop hangen.

## Startprompt voor de lokale sessie
Kopieer en plak dit in de nieuwe sessie (en zet OVERDRACHT.md in de projectmap):

> Lees OVERDRACHT.md. Zet een nieuw Android-project op (Kotlin, minSdk passend bij Tab A9) met de AccessibilityService-blokkade voor com.android.vending zoals beschreven. Controleer eerst met `adb devices` dat de tablet verbonden is. Bouw en installeer met `./gradlew installDebug` en test via `adb logcat`. Vraag mij de pincode en de ontgrendelduur voordat je ze vastlegt. Begin met een minimale werkende versie en laat die op de tablet zien voordat je beveiliging (Device Admin) toevoegt.
