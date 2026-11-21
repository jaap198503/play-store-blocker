# Play-Store-Sperre

[English](README.md) · [Nederlands](README.nl.md) · **Deutsch** · [Français](README.fr.md) · [Español](README.es.md)

Eine Android-App, die den Google Play Store auf dem Tablet eines Kindes **immer sperrt**. Erst nachdem ein Elternteil einen PIN-Code eingibt, öffnet sich der Play Store vorübergehend (standardmäßig 10 Minuten oder die vom Elternteil gewählte Anzahl Minuten), danach schließt er sich wieder.

Entwickelt für ein Samsung Galaxy Tab A9 mit einem Google-Family-Link-Kinderkonto. Family Link kann den Play Store nicht selbst sperren, deshalb übernimmt diese App das.

> Dies ist **Abschreckung, keine harte Sicherheit**. Ein geschickter Jugendlicher kann den Dienst ausschalten oder die App deinstallieren. Siehe [Einschränkungen](#einschränkungen).

## Download

**[⬇ APK herunterladen (play-store-lock.apk)](https://github.com/jaap198503/play-store-blocker/releases/latest/download/play-store-lock.apk)** · [alle Releases und Prüfsummen](https://github.com/jaap198503/play-store-blocker/releases)

Erfordert Android 13 oder neuer. Das APK ist mit dem eigenen Schlüssel des Projekts signiert und wird nicht über den Play Store verteilt. Prüfe den Download, indem du die SHA-256-Summe mit der auf der Release-Seite vergleichst:

```powershell
Get-FileHash .\play-store-lock.apk -Algorithm SHA256
```

## Auf dem Tablet installieren

Mach das als Elternteil auf dem Tablet des Kindes. Ein Laptop ist nicht nötig. (Lieber mit Laptop? Siehe [Installation per USB](#installation-per-usb-adb).)

1. **Auto Blocker ausschalten.** Gehe zu Einstellungen > Sicherheit und Datenschutz > Auto Blocker und schalte ihn aus. Er blockiert Installationen von außerhalb des Play Stores. (In Schritt 7 schaltest du ihn wieder ein.)
2. **APK herunterladen und installieren.** Öffne den Download-Link oben im Browser des Tablets. Öffne die heruntergeladene Datei. Erlaube dem Browser auf Nachfrage, **unbekannte Apps zu installieren**, und tippe auf **Installieren**. Warnt Play Protect vor einem unbekannten Entwickler? Wähle **Weitere Details > Trotzdem installieren**.
3. **PIN-Code festlegen.** Öffne die App **Play-Store-Sperre** (Schloss-Symbol). Beim ersten Mal landest du automatisch in den **Einstellungen**. Wähle einen PIN-Code aus 4 bis 12 Ziffern, wiederhole ihn, ändere optional die Standardanzahl Minuten und tippe auf **Speichern**. Solange kein PIN-Code festgelegt ist, wird nichts gesperrt.
4. **Bedienungshilfen-Dienst einschalten.** Tippe in der App auf **Einstellungen für Bedienungshilfen öffnen**. Gehe zu Installierte Apps > **Play-Store-Sperre** und schalte den Dienst ein. Bestätige die Warnung.
   - Ausgegraut oder die Meldung **Eingeschränkte Einstellung**? Gehe zu Einstellungen > Apps > **Play-Store-Sperre**, tippe oben rechts auf ⋮ und wähle **Eingeschränkte Einstellungen zulassen**. Versuche es dann erneut. (Das erscheint, weil die App von außerhalb des Play Stores stammt.)
5. **Akku auf unbeschränkt stellen.** Tippe in der App auf **App-Einstellungen**, öffne Akku und wähle **Unbeschränkt**. Sonst legt Samsung den Dienst nach einer Weile still. Optional: Füge die App unter Einstellungen > Akku > Hintergrundnutzungsbeschränkungen > Nie schlafende Apps hinzu.
6. **Testen.**
   1. Öffne den **Play Store**. Du landest sofort auf dem Startbildschirm und der Bildschirm „Der Play Store ist gesperrt“ erscheint.
   2. Gib einen falschen PIN-Code ein und tippe auf **Öffnen**. Du siehst „Falscher PIN-Code“.
   3. Prüfe das Feld **Anzahl Minuten** (Standard 10). Nimm 1 oder 2 für einen schnellen Test.
   4. Gib den richtigen PIN-Code ein. Der Play Store öffnet sich.
   5. Nach der gewählten Anzahl Minuten sollte er sich selbst schließen und der PIN-Bildschirm wieder erscheinen.
   6. Optional: Gib 5-mal einen falschen PIN-Code ein. Du erhältst „Zu viele Versuche. Bitte 30 Sekunden warten.“
7. **Abschluss.** Schalte Auto Blocker wieder ein. Prüfe nach einem Neustart des Tablets in der App, dass PIN-Code und Dienst weiterhin als eingeschaltet angezeigt werden.

### Wie ein Kind um Zeit bittet

Das Kind öffnet den Play Store und sieht den PIN-Bildschirm. Es bittet um eine Anzahl Minuten, das Elternteil gibt diese Zahl im Feld **Anzahl Minuten** ein und tippt den PIN-Code. Der Play Store bleibt dann genau so lange offen. Andersherum geht es auch: Das Elternteil öffnet selbst die App **Play-Store-Sperre**. Deren Hauptbildschirm ist derselbe Bildschirm (**Vorübergehend Zugriff geben**), sodass du Zeit geben kannst, ohne dass das Kind zuerst den Play Store öffnet.

### PIN-Code oder Standardminuten ändern

Öffne die App und tippe auf **Einstellungen**. Dort fragt sie nach dem aktuellen PIN-Code, sodass nur das Elternteil etwas ändern kann. Gib einen neuen PIN-Code zweimal ein, um den PIN-Code zu ändern, oder lass den neuen PIN-Code leer, um nur die Standardanzahl Minuten zu ändern.

## Installation per USB (adb)

Eine Alternative zur Installation direkt auf dem Tablet, nützlich für mehrere Tablets.

1. Richte den Laptop mit `adb` ein (Android SDK Platform-Tools).
2. Schalte auf dem Tablet die Entwickleroptionen ein (Einstellungen > Über das Tablet > Softwareinformationen, 7-mal auf **Build-Nummer** tippen) und **USB-Debugging** (Einstellungen > Entwickleroptionen). Springt der Schalter zurück, blockiert womöglich ein Family-Link-Kinderkonto das: Mach es im Elternprofil oder erlaube es vorübergehend in Family Link.
3. Verbinde das Tablet mit einem USB-Kabel, das auch Daten überträgt. Wähle auf dem Tablet **Dateiübertragung** und tippe bei „USB-Debugging zulassen?“ auf **Zulassen** (wähle „Immer von diesem Computer zulassen“).
4. Prüfe, dass das Tablet als `device` erscheint:
   ```powershell
   adb devices -l
   ```
   Leere Liste: anderes Kabel oder anderen Anschluss probieren und den Samsung-USB-Treiber installieren. Status `unauthorized`: Bestätige die Meldung auf dem Tablet. Mehrere Geräte: Füge `-s <Seriennummer>` zu jedem `adb`-Befehl hinzu.
5. Installiere das heruntergeladene APK:
   ```powershell
   adb install -r .\play-store-lock.apk
   ```
6. Fahre mit den Schritten 3 bis 7 oben fort (PIN festlegen, Dienst einschalten, Akku, testen, Abschluss). Schalte USB-Debugging danach wieder aus.

Die Menüpfade in dieser README gelten für Samsung One UI. Bezeichnungen können je nach Version und Tablet-Sprache leicht abweichen.

## Funktionsweise

- Ein `AccessibilityService` hört auf `TYPE_WINDOW_STATE_CHANGED`. Kommt `com.android.vending` in den Vordergrund und ist kein Entsperrfenster aktiv, wechselt das Tablet zum Startbildschirm und ein PIN-Bildschirm erscheint.
- Richtiger PIN-Code: Der Play Store startet und das Entsperrfenster läuft für die eingegebene Anzahl Minuten (1 bis 180). Das Fenster nutzt `SystemClock.elapsedRealtime()` plus den Boot-Zähler; Uhr verstellen oder Neustart verlängert es also nicht.
- Nach Ablauf des Fensters prüft der Dienst, was im Vordergrund steht, und schließt den Play Store bei Bedarf.
- Nach 5 falschen Versuchen gilt eine Wartezeit, die sich jedes Mal verdoppelt (30 s bis maximal 10 min).
- Den PIN-Code legst du auf dem Gerät fest; er wird nirgends lesbar gespeichert: nur ein gesalzener PBKDF2-Hash (SHA-256, 120.000 Iterationen) im privaten Speicher der App. Im APK selbst steckt kein PIN-Code.
- Solange kein PIN-Code festgelegt ist, wird nichts gesperrt (niemand könnte entsperren).
- Die App folgt automatisch der Sprache des Tablets. Standard ist Englisch. Übersetzungen: Niederländisch, Deutsch, Französisch und Spanisch.

## Für Entwickler

```
app/src/main/java/nl/playstoreblocker/
  MainActivity.kt                  Hauptbildschirm: vorübergehend Zugriff geben (PIN + Minuten)
  SettingsActivity.kt              Einstellungen: Status, PIN festlegen oder ändern
  BlockerAccessibilityService.kt   sperrt und überwacht das Entsperrfenster
  BlockActivity.kt                 PIN-Bildschirm
  PinStore.kt                      gesalzener PBKDF2-Hash und Standardminuten (auf dem Gerät)
  UnlockState.kt                   Entsperrfenster (elapsedRealtime + Boot-Zähler)
  PinAttempts.kt                   Wartezeit nach falschen Versuchen
  Config.kt                        gesperrte Pakete, Grenzen
tools/env.ps1                      setzt JDK, SDK, adb und Gradle in deiner PowerShell-Sitzung
local.properties                   lokal, NICHT in git: Pfad zum Android SDK
keystore.properties                lokal, NICHT in git: Schlüssel für die Release-Signatur
```

### Build-Umgebung einrichten (einmalig)

Du brauchst **JDK 17**, das **Android SDK** (Platform-Tools mit `adb`, `platforms;android-35`, `build-tools;34.0.0`) und Gradle (der Wrapper `gradlew.bat` holt Gradle 8.9 selbst).

1. Installiere JDK 17 (z. B. Temurin) und die Android-SDK-Command-Line-Tools oder Android Studio.
2. Akzeptiere die SDK-Lizenzen und installiere die Pakete:
   ```powershell
   sdkmanager --licenses
   sdkmanager "platform-tools" "platforms;android-35" "build-tools;34.0.0"
   ```
3. Lege `local.properties` im Projektordner an:
   ```
   sdk.dir=C\:\\Pfad\\zum\\sdk
   ```
4. Passe bei Bedarf die Pfade in `tools/env.ps1` an und lade es in jeder neuen PowerShell-Sitzung (Punkt und Leerzeichen am Anfang beachten):
   ```powershell
   . .\tools\env.ps1
   ```

### Bauen

Debug-Build, direkt auf angeschlossene Tablets installiert:

```powershell
.\gradlew.bat installDebug
```

> **Hinweis:** Auf dem ursprünglichen Entwicklungslaptop funktionierte Gradle nicht in der eigenen Shell von Claude Code (Java-Selektoren scheiterten mit „Unable to establish loopback connection“), wohl aber in einem normalen PowerShell- oder Terminal-Fenster. Tritt der Fehler auf, baue in einem normalen Terminalfenster.

Logs ansehen (Tag `PSBlocker`):

```powershell
adb logcat -s PSBlocker:V
```

### Release-Build

Das Release-APK muss signiert sein. Erstelle einmalig einen Schlüssel und bewahre ihn gut auf (verlierst du ihn, lassen sich bestehende Installationen nicht mehr aktualisieren):

```powershell
keytool -genkeypair -keystore play-store-lock.jks -alias play-store-lock -keyalg RSA -keysize 4096 -validity 10000 -dname "CN=Play Store Lock"
```

Lege dann `keystore.properties` im Projektordner an (steht in `.gitignore`):

```
storeFile=C\:\\Pfad\\zu\\play-store-lock.jks
storePassword=...
keyAlias=play-store-lock
keyPassword=...
```

```powershell
.\gradlew.bat assembleRelease
```

Das APK liegt danach in `app\build\outputs\apk\release\app-release.apk`. Debug- und Release-Builds verwenden verschiedene Schlüssel; deinstalliere daher das eine, bevor du das andere installierst.

### Eine Sprache hinzufügen

Alle Texte stehen in `app/src/main/res/values/strings.xml` (Englisch, der Standard). Kopiere die Datei nach `app/src/main/res/values-<Sprachcode>/strings.xml` (z. B. `values-it` für Italienisch), übersetze die Werte und lass die Namen unverändert. Behalte `%1$d` und `\'` bei. Android wählt die Sprache des Tablets selbst und fällt auf Englisch zurück. Willst du auch diese README übersetzen, kopiere sie nach `README.<Code>.md` und füge der Sprachzeile oben einen Link hinzu.

## Einschränkungen

- **Der Dienst kann ausgeschaltet werden.** Ein Kind kann Einstellungen > Bedienungshilfen öffnen und den Dienst ausschalten oder die App deinstallieren. Ob Family Link das verhindert, wurde nicht getestet.
- **Kein Device Admin.** Das Deinstallieren wird daher nicht zusätzlich erschwert.
- Der **Galaxy Store** (`com.sec.android.app.samsungapps`) ist ein zweiter Weg, Apps zu installieren, und wird **nicht** gesperrt. Füge ihn zu `BLOCKED_PACKAGES` in `Config.kt` hinzu, wenn du das willst.
- Ein kurzer numerischer PIN-Code lässt sich per Brute-Force erraten, wenn jemand den privaten Speicher der App lesen kann (nur auf einem gerooteten Gerät möglich). Nimm einen längeren PIN-Code, wenn dir das wichtig ist.
- **Bisher getestet** auf einem Samsung Galaxy Tab A9 (Android 16): Sperren, Eingabe des richtigen PIN-Codes und Öffnen des Play Stores. Das Schließen nach Ablauf des Fensters, die Wartezeit nach 5 falschen Versuchen und die anderen Sprachen wurden noch nicht auf einem Gerät geprüft.
