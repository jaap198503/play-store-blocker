# Play Store-slot

[English](README.md) · **Nederlands** · [Deutsch](README.de.md) · [Français](README.fr.md) · [Español](README.es.md)

Een Android-app die de Google Play Store op een kindertablet **altijd blokkeert**. Alleen nadat een ouder een pincode invoert, gaat de Play Store tijdelijk open (standaard 10 minuten, of het aantal minuten dat de ouder kiest), daarna sluit hij weer.

Gebouwd voor een Samsung Galaxy Tab A9 met een Google Family Link-kinderaccount. Family Link kan de Play Store zelf niet blokkeren, daarom doet deze app dat.

> Dit is **afschrikking, geen harde beveiliging**. Een handige tiener kan de service uitzetten of de app verwijderen. Zie [Beperkingen](#beperkingen).

## Downloaden

**[⬇ Download het APK (play-store-lock.apk)](https://github.com/jaap198503/play-store-blocker/releases/latest/download/play-store-lock.apk)** · [alle releases en controlesommen](https://github.com/jaap198503/play-store-blocker/releases)

Vereist Android 13 of nieuwer. Het APK is ondertekend met de eigen sleutel van het project en wordt niet via de Play Store verspreid. Controleer de download door de SHA-256 te vergelijken met die op de releasepagina:

```powershell
Get-FileHash .\play-store-lock.apk -Algorithm SHA256
```

## Installeren op de tablet

Doe dit als ouder, op de tablet van het kind. Een laptop is niet nodig. (Liever met een laptop? Zie [Installeren via USB](#installeren-via-usb-adb).)

1. **Zet Auto Blocker uit.** Ga naar Instellingen > Beveiliging en privacy > Auto Blocker en zet hem uit. Hij blokkeert installaties van buiten de Play Store. (In stap 7 zet je hem weer aan.)
2. **Download en installeer het APK.** Open de downloadlink hierboven in de browser van de tablet. Open het gedownloade bestand. Sta de browser desgevraagd toe om **onbekende apps te installeren** en tik op **Installeren**. Waarschuwt Play Protect voor een onbekende ontwikkelaar? Kies **Meer details > Toch installeren**.
3. **Stel de pincode in.** Open de app **Play Store-slot** (hangslot-icoon). De eerste keer kom je vanzelf in **Instellingen**. Kies een pincode van 4 tot 12 cijfers, herhaal hem, pas eventueel het standaard aantal minuten aan en tik op **Opslaan**. Er wordt niets geblokkeerd zolang er geen pincode is.
4. **Zet de toegankelijkheidsservice aan.** Tik in de app op **Toegankelijkheidsinstellingen openen**. Ga naar Geïnstalleerde apps > **Play Store-slot** en zet de service aan. Bevestig de waarschuwing.
   - Grijs, of de melding **Beperkte instelling**? Ga naar Instellingen > Apps > **Play Store-slot**, tik rechtsboven op ⋮ en kies **Beperkte instellingen toestaan**. Probeer het daarna opnieuw. (Dit verschijnt omdat de app van buiten de Play Store komt.)
5. **Zet de batterij op onbeperkt.** Tik in de app op **App-instellingen**, open Batterij en kies **Onbeperkt**. Anders legt Samsung de service na verloop van tijd stil. Optioneel: voeg de app toe onder Instellingen > Batterij > Achtergrondlimieten > Nooit slapende apps.
6. **Test het.**
   1. Open de **Play Store**. Je gaat meteen naar het startscherm en het scherm "De Play Store is dicht" verschijnt.
   2. Typ een foute pincode en tik op **Openen**. Je ziet "Onjuiste pincode".
   3. Controleer het veld **Aantal minuten** (standaard 10). Neem 1 of 2 voor een snelle test.
   4. Typ de juiste pincode. De Play Store gaat open.
   5. Na het gekozen aantal minuten hoort hij zichzelf te sluiten en verschijnt het pincodescherm weer.
   6. Optioneel: typ 5 keer een foute pincode. Je krijgt "Te vaak geprobeerd. Wacht 30 seconden."
7. **Afronden.** Zet Auto Blocker weer aan. Controleer na een herstart van de tablet in de app dat de pincode en de service nog als aan staan aangegeven.

### Hoe een kind tijd vraagt

Het kind opent de Play Store en ziet het pincodescherm. Het vraagt om een aantal minuten, de ouder typt dat aantal in het veld **Aantal minuten** en voert de pincode in. De Play Store blijft dan precies zo lang open. Het kan ook andersom: de ouder opent zelf de app **Play Store-slot**. Het hoofdscherm daarvan is hetzelfde scherm (**Tijdelijk toegang geven**), zodat je tijd kunt geven zonder dat het kind eerst de Play Store opent.

### Pincode of standaardminuten wijzigen

Open de app en tik op **Instellingen**. Daar vraagt hij om de huidige pincode, dus alleen de ouder kan iets wijzigen. Vul een nieuwe pincode twee keer in om de pincode te wijzigen, of laat de nieuwe pincode leeg om alleen het standaard aantal minuten te wijzigen.

## Installeren via USB (adb)

Een alternatief voor installeren op de tablet zelf, handig voor meerdere tablets.

1. Zet de laptop klaar met `adb` (Android SDK platform-tools).
2. Zet op de tablet de Ontwikkelaarsopties aan (Instellingen > Info over tablet > Softwaregegevens, tik 7 keer op **Buildnummer**) en **USB-foutopsporing** (Instellingen > Ontwikkelaarsopties). Springt de schakelaar terug, dan blokkeert een Family Link-kinderaccount dit mogelijk: doe het onder het ouderprofiel of sta het tijdelijk toe in Family Link.
3. Sluit de tablet aan met een USB-kabel die ook data doorgeeft. Kies **Bestanden overzetten** op de tablet en tik op **Toestaan** bij "USB-foutopsporing toestaan?" (kies "Altijd toestaan vanaf deze computer").
4. Controleer dat de tablet als `device` verschijnt:
   ```powershell
   adb devices -l
   ```
   Lege lijst: andere kabel of poort proberen en de Samsung USB-driver installeren. Status `unauthorized`: bevestig de melding op de tablet. Meerdere apparaten: voeg `-s <serienummer>` toe aan elk `adb`-commando.
5. Installeer het gedownloade APK:
   ```powershell
   adb install -r .\play-store-lock.apk
   ```
6. Ga verder met stap 3 tot en met 7 hierboven (pincode instellen, service aanzetten, batterij, testen, afronden). Zet USB-foutopsporing daarna weer uit.

De menupaden in deze README horen bij Samsung One UI. Namen kunnen per versie en per tabletaal iets verschillen.

## Hoe het werkt

- Een `AccessibilityService` luistert naar `TYPE_WINDOW_STATE_CHANGED`. Komt `com.android.vending` voorop en is er geen actief ontgrendelvenster, dan gaat de tablet naar het startscherm en verschijnt een pincodescherm.
- Pincode goed: de Play Store start en het ontgrendelvenster loopt voor het ingevulde aantal minuten (1 tot 180). Het venster gebruikt `SystemClock.elapsedRealtime()` plus de boot-teller, dus klok verzetten of herstarten helpt niet om het te verlengen.
- Na afloop van het venster controleert de service wat voorop staat en sluit de Play Store als dat nodig is.
- Na 5 foute pogingen volgt een wachttijd die steeds verdubbelt (30 s tot maximaal 10 min).
- De pincode stel je in op het toestel en staat nergens leesbaar: alleen een salted PBKDF2-hash (SHA-256, 120.000 iteraties) in de privéopslag van de app. In het APK zelf zit geen pincode.
- Zolang er geen pincode is ingesteld, wordt niets geblokkeerd (niemand zou kunnen ontgrendelen).
- De app volgt automatisch de taal van de tablet. De standaardtaal is Engels. Vertalingen: Nederlands, Duits, Frans en Spaans.

## Voor ontwikkelaars

```
app/src/main/java/nl/playstoreblocker/
  MainActivity.kt                  hoofdscherm: tijdelijk toegang geven (pincode + minuten)
  SettingsActivity.kt              instellingen: status, pincode instellen of wijzigen
  BlockerAccessibilityService.kt   blokkeert en bewaakt het ontgrendelvenster
  BlockActivity.kt                 pincodescherm
  PinStore.kt                      salted PBKDF2-hash en standaardminuten (op het toestel)
  UnlockState.kt                   ontgrendelvenster (elapsedRealtime + boot-teller)
  PinAttempts.kt                   wachttijd na foute pogingen
  Config.kt                        geblokkeerde pakketten, grenzen
tools/env.ps1                      zet JDK, SDK, adb en Gradle in je PowerShell-sessie
local.properties                   lokaal, NIET in git: pad naar de Android SDK
keystore.properties                lokaal, NIET in git: sleutel voor release-ondertekening
```

### Bouwomgeving klaarzetten (eenmalig)

Je hebt nodig: **JDK 17**, de **Android SDK** (platform-tools met `adb`, `platforms;android-35`, `build-tools;34.0.0`) en Gradle (de wrapper `gradlew.bat` haalt Gradle 8.9 zelf op).

1. Installeer JDK 17 (bijvoorbeeld Temurin) en de Android SDK command-line tools, of Android Studio.
2. Accepteer de SDK-licenties en installeer de pakketten:
   ```powershell
   sdkmanager --licenses
   sdkmanager "platform-tools" "platforms;android-35" "build-tools;34.0.0"
   ```
3. Maak `local.properties` in de projectmap:
   ```
   sdk.dir=C\:\\Pad\\naar\\sdk
   ```
4. Pas zo nodig de paden in `tools/env.ps1` aan en laad het in elke nieuwe PowerShell-sessie (let op de punt en spatie vooraan):
   ```powershell
   . .\tools\env.ps1
   ```

### Bouwen

Debug-build, direct op aangesloten tablets geïnstalleerd:

```powershell
.\gradlew.bat installDebug
```

> **Let op:** op de oorspronkelijke ontwikkellaptop werkte Gradle niet vanuit Claude Code's eigen shell (Java-selectors faalden met "Unable to establish loopback connection"), maar wel in een gewoon PowerShell- of Terminal-venster. Krijg je die fout, bouw dan in een gewoon terminalvenster.

Logs bekijken (tag `PSBlocker`):

```powershell
adb logcat -s PSBlocker:V
```

### Release-build

Het release-APK moet ondertekend zijn. Maak eenmalig een sleutel en bewaar die goed (raak je hem kwijt, dan kunnen bestaande installaties niet meer worden bijgewerkt):

```powershell
keytool -genkeypair -keystore play-store-lock.jks -alias play-store-lock -keyalg RSA -keysize 4096 -validity 10000 -dname "CN=Play Store Lock"
```

Maak daarna `keystore.properties` in de projectmap (staat in `.gitignore`):

```
storeFile=C\:\\Pad\\naar\\play-store-lock.jks
storePassword=...
keyAlias=play-store-lock
keyPassword=...
```

```powershell
.\gradlew.bat assembleRelease
```

Het APK staat daarna in `app\build\outputs\apk\release\app-release.apk`. Debug- en release-builds gebruiken verschillende sleutels, dus verwijder de ene eerst voordat je de andere installeert.

### Een taal toevoegen

Alle teksten staan in `app/src/main/res/values/strings.xml` (Engels, de standaard). Kopieer dat bestand naar `app/src/main/res/values-<taalcode>/strings.xml` (bijvoorbeeld `values-it` voor Italiaans), vertaal de waarden en laat de namen ongewijzigd. Houd `%1$d` en `\'` zoals ze staan. Android kiest zelf de taal van de tablet en valt terug op Engels. Wil je ook deze README vertalen, kopieer hem dan naar `README.<code>.md` en voeg een link toe aan de taalregel bovenaan.

## Beperkingen

- **De service kan worden uitgezet.** Een kind kan Instellingen > Toegankelijkheid openen en de service uitzetten, of de app verwijderen. Of Family Link dat tegenhoudt, is niet getest.
- **Geen Device Admin.** Verwijderen is daarom niet extra moeilijk gemaakt.
- **Galaxy Store** (`com.sec.android.app.samsungapps`) is een tweede route om apps te installeren en wordt **niet** geblokkeerd. Voeg hem toe aan `BLOCKED_PACKAGES` in `Config.kt` als je dat wilt.
- Een korte numerieke pincode is met brute kracht te raden door wie de privéopslag van de app kan lezen (alleen mogelijk op een gerootte telefoon). Gebruik een langere pincode als dat voor je telt.
- **Tot nu toe getest** op een Samsung Galaxy Tab A9 (Android 16): blokkeren, de juiste pincode invoeren en de Play Store openen. Het sluiten na afloop van het venster, de wachttijd na 5 foute pogingen en de andere talen zijn nog niet op een toestel gecontroleerd.
