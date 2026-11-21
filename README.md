# Play Store-slot

Android-app (Kotlin) die de Google Play Store op een kindertablet **altijd blokkeert**. Alleen na invoer van een pincode gaat de Play Store tijdelijk open (standaard 10 minuten), daarna sluit hij weer.

Gebouwd voor een Samsung Galaxy Tab A9 met een Family Link-kinderaccount. Family Link kan de Play Store zelf niet blokkeren, daarom doet deze app dat.

> Dit is **afschrikking, geen harde beveiliging**. Een handige tiener kan de service uitzetten of de app verwijderen. Zie [Beperkingen](#beperkingen).

## Hoe het werkt

- Een `AccessibilityService` luistert naar `TYPE_WINDOW_STATE_CHANGED`. Komt `com.android.vending` voorop en is er geen actief ontgrendelvenster, dan gaat de tablet naar het startscherm en verschijnt een pincodescherm.
- Pincode goed: de Play Store start en het ontgrendelvenster loopt. Het venster gebruikt `SystemClock.elapsedRealtime()` plus de boot-teller, dus klok verzetten of herstarten helpt niet om het te verlengen.
- Na afloop van het venster controleert de service wat voorop staat en sluit de Play Store als dat nog nodig is.
- Na 5 foute pogingen volgt een wachttijd die steeds verdubbelt (30 s tot maximaal 10 min).
- De pincode staat nergens. In de app zit alleen een salted PBKDF2-hash (SHA-256, 120.000 iteraties).
- De app heeft geen icoon in het appscherm. Je vindt hem onder Instellingen > Toegankelijkheid en onder Instellingen > Apps.

## Indeling

```
app/src/main/java/nl/playstoreblocker/
  BlockerAccessibilityService.kt   blokkeert en bewaakt het venster
  BlockActivity.kt                 pincodescherm
  UnlockState.kt                   ontgrendelvenster (elapsedRealtime + boot-teller)
  PinVerifier.kt                   PBKDF2-controle tegen BuildConfig
  PinAttempts.kt                   wachttijd na foute pogingen
  Config.kt                        geblokkeerde pakketten, duur
tools/New-PinHash.ps1              maakt blocker.properties (pincode-hash)
tools/env.ps1                      zet JDK, SDK, adb en Gradle in je PowerShell-sessie
blocker.properties                 lokaal, NIET in git: salt, hash, duur
local.properties                   lokaal, NIET in git: pad naar de Android SDK
OVERDRACHT.md                      oorspronkelijke ontwerpnotitie
```

## Eenmalig: de laptop klaarzetten

Je hebt nodig: **JDK 17**, de **Android SDK** (platform-tools met `adb`, `platforms;android-35`, `build-tools;34.0.0`) en Gradle. De wrapper (`gradlew.bat`) haalt Gradle 8.9 zelf op.

Op de ontwikkellaptop staat alles in `C:\Users\JaapScheper\android-tools` (`jdk17`, `sdk`, `gradle-8.9`). Op een andere laptop:

1. Installeer JDK 17 (bijvoorbeeld Temurin) en de Android SDK command-line tools, of Android Studio.
2. Accepteer de SDK-licenties en installeer de pakketten:
   ```powershell
   sdkmanager --licenses
   sdkmanager "platform-tools" "platforms;android-35" "build-tools;34.0.0"
   ```
3. Maak `local.properties` in de projectmap met het pad naar je SDK:
   ```
   sdk.dir=C\:\\Pad\\naar\\sdk
   ```
4. Pas de paden in `tools/env.ps1` aan en laad het in elke nieuwe PowerShell-sessie (let op de punt en spatie vooraan):
   ```powershell
   . .\tools\env.ps1
   ```
5. Maak `blocker.properties` met je pincode (zie hieronder).

## Pincode of duur wijzigen

Open PowerShell in de projectmap en draai:

```powershell
powershell -File tools/New-PinHash.ps1
```

Het script vraagt de pincode verborgen (4 tot 12 cijfers) en schrijft een nieuwe `blocker.properties`. Een andere duur zet je met `-UnlockMinutes`:

```powershell
powershell -File tools/New-PinHash.ps1 -UnlockMinutes 15
```

Daarna opnieuw bouwen en installeren (zie hieronder). Een nieuwe pincode of duur geldt pas na het opnieuw installeren.

## Bouwen en installeren

```powershell
. .\tools\env.ps1
.\gradlew.bat installDebug
```

`installDebug` installeert op alle aangesloten tablets. Het APK staat daarna in `app\build\outputs\apk\debug\app-debug.apk`.

> **Let op:** op de ontwikkellaptop werkte Gradle niet vanuit Claude Code's eigen shell (Java-selectors faalden met "Unable to establish loopback connection"), maar wel in een gewoon PowerShell- of Terminal-venster. Krijg je die fout, bouw dan in een gewoon terminalvenster.

Logs bekijken (tag `PSBlocker`):

```powershell
adb logcat -s PSBlocker:V
```

## Uitrol naar een andere tablet, van A tot Z

Alle tablets krijgen dezelfde pincode en duur, namelijk die uit `blocker.properties` op het moment van bouwen. Wil je per tablet een andere pincode, bouw dan per tablet opnieuw.

De menupaden hieronder horen bij Samsung One UI. Namen kunnen per versie iets verschillen.

### 1. Tablet voorbereiden

1. **Ontwikkelaarsopties aanzetten.** Ga naar Instellingen > Info over tablet > Softwaregegevens en tik 7 keer op **Buildnummer**. Voer zo nodig de schermvergrendeling in.
2. **USB-foutopsporing aanzetten.** Ga naar Instellingen > Ontwikkelaarsopties en zet **USB-foutopsporing** aan.
   - Springt de schakelaar terug of is hij grijs? Een Family Link-kinderaccount kan dit blokkeren. Doe de stappen dan onder het ouderprofiel, of sta het tijdelijk toe in Family Link.
3. **Auto Blocker uitzetten.** Ga naar Instellingen > Beveiliging en privacy > Auto Blocker en zet hem uit. Hij blokkeert installaties van buiten de Play Store.

### 2. Aansluiten via USB

1. Sluit de tablet met een USB-kabel aan op de laptop. Gebruik een kabel die ook data doorgeeft, niet een laadkabel.
2. Kies op de tablet bij de USB-melding **Bestanden overzetten**. Voor `adb` maakt de USB-modus niet uit, maar dit werkt betrouwbaar.
3. Er verschijnt **USB-foutopsporing toestaan?** Kies **Altijd toestaan vanaf deze computer** en tik op **Toestaan**.
4. Controleer op de laptop:
   ```powershell
   . .\tools\env.ps1
   adb devices -l
   ```
   De tablet moet met status **`device`** in de lijst staan.
   - Lege lijst: andere kabel of USB-poort proberen, USB-foutopsporing controleren en eventueel de Samsung USB-driver installeren. Kijk in Apparaatbeheer of de tablet een uitroepteken heeft.
   - Status `unauthorized`: bevestig de toestemmingsmelding op de tablet.
   - Meerdere apparaten aangesloten: gebruik `adb -s <serienummer>` bij elk `adb`-commando.

### 3. App bouwen en installeren

1. Zorg dat `blocker.properties` bestaat en de juiste pincode en duur bevat (zie [Pincode of duur wijzigen](#pincode-of-duur-wijzigen)).
2. Installeer:
   ```powershell
   .\gradlew.bat installDebug
   ```
   Je ziet `Installed on 1 device.`

   Alternatief voor meerdere tablets, zonder opnieuw te bouwen:
   ```powershell
   .\gradlew.bat assembleDebug
   adb -s <serienummer> install -r app\build\outputs\apk\debug\app-debug.apk
   ```

### 4. Toegankelijkheidsservice aanzetten

Dit moet je zelf op de tablet doen.

1. Ga naar Instellingen > Toegankelijkheid > Geïnstalleerde apps > **Play Store-slot**.
2. Zet de service aan en bevestig de waarschuwing.
3. Is de schakelaar grijs of zie je **Beperkte instelling**? Ga naar Instellingen > Apps > **Play Store-slot**, tik rechtsboven op ⋮ en kies **Beperkte instellingen toestaan**. Probeer stap 1 en 2 daarna opnieuw.

Controle vanaf de laptop:

```powershell
adb shell settings get secure enabled_accessibility_services
```

Daar moet `nl.playstoreblocker/nl.playstoreblocker.BlockerAccessibilityService` in staan.

### 5. Batterij op onbeperkt

Anders legt Samsung de service na verloop van tijd stil.

1. Ga naar Instellingen > Apps > **Play Store-slot** > Batterij en kies **Onbeperkt**.
2. Optioneel extra zeker: Instellingen > Batterij > Achtergrondlimieten > **Nooit slapende apps** en voeg Play Store-slot toe.

### 6. De pincode testen

1. Open de **Play Store** op de tablet. Je gaat meteen naar het startscherm en het pincodescherm "De Play Store is dicht" verschijnt.
2. Typ een **foute pincode** en tik op Openen. Je ziet "Onjuiste pincode".
3. Typ de **juiste pincode**. De Play Store gaat open.
4. Laat de Play Store **10 minuten** open (of de duur die je hebt ingesteld). Daarna hoort hij zichzelf te sluiten en verschijnt het pincodescherm weer.
5. Optioneel: controleer de wachttijd. Typ 5 keer een foute pincode. Je krijgt "Te vaak geprobeerd. Wacht 30 seconden."

Meekijken kan met `adb logcat -s PSBlocker:V`. Je ziet regels als `blocking: window event from com.android.vending` en `unlock window expired`.

> Tijdens de eerste uitrol is stap 1 tot en met 3 getest. Het sluiten na 10 minuten en de wachttijd na 5 foute pogingen zijn nog niet op de tablet gecontroleerd, dus neem stap 4 en 5 serieus mee.

### 7. Afronden

1. Zet **USB-foutopsporing** weer uit en eventueel de Ontwikkelaarsopties.
2. Zet **Auto Blocker** weer aan.
3. Koppel de tablet los.
4. Controleer na een herstart van de tablet dat de service nog aan staat (stap 4) en de Play Store nog dicht is.

## Beperkingen

- **De service kan worden uitgezet.** Een kind kan Instellingen > Toegankelijkheid openen en de service uitzetten, of de app verwijderen. Of Family Link dat tegenhoudt, is niet getest.
- **Geen Device Admin.** Verwijderen is daarom niet extra moeilijk gemaakt.
- **Galaxy Store** (`com.sec.android.app.samsungapps`) is een tweede route om apps te installeren en wordt **niet** geblokkeerd. Voeg hem toe aan `BLOCKED_PACKAGES` in `Config.kt` als je dat wilt.
- Het APK is met de debug-sleutel ondertekend. Prima voor eigen gebruik, niet bedoeld voor verspreiding.
- Het APK bevat de salted hash van de pincode. Een korte numerieke pincode is met veel moeite uit het APK te herleiden.
