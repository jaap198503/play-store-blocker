# Play Store Lock

**English** · [Nederlands](README.nl.md) · [Deutsch](README.de.md) · [Français](README.fr.md) · [Español](README.es.md)

An Android app that **always blocks** the Google Play Store on a child's tablet. Only after a parent enters a PIN code does the Play Store open temporarily (10 minutes by default, or the number of minutes the parent chooses), after which it closes again.

Built for a Samsung Galaxy Tab A9 with a Google Family Link child account. Family Link cannot block the Play Store itself, so this app does it.

> This is **a deterrent, not hard security**. A resourceful teenager can switch off the service or uninstall the app. See [Limitations](#limitations).

## Download

**[⬇ Download the APK (play-store-lock.apk)](https://github.com/jaap198503/play-store-blocker/releases/latest/download/play-store-lock.apk)** · [all releases and checksums](https://github.com/jaap198503/play-store-blocker/releases)

Requires Android 13 or newer. The APK is signed with the project's own key and is not distributed through the Play Store. To verify the download, compare its SHA-256 with the one on the release page:

```powershell
Get-FileHash .\play-store-lock.apk -Algorithm SHA256
```

## Install on the tablet

Do this as the parent, on the child's tablet. No laptop is needed. (Prefer a laptop? See [Install via USB](#install-via-usb-adb).)

1. **Turn off Auto Blocker.** Go to Settings > Security and privacy > Auto Blocker and switch it off. It blocks installs from outside the Play Store. (You can switch it back on in step 7.)
2. **Download and install the APK.** Open the download link above in the tablet's browser. Open the downloaded file. If asked, allow the browser to **install unknown apps** and tap **Install**. If Play Protect warns about an unknown developer, choose **More details > Install anyway**.
3. **Set the PIN.** Open the **Play Store Lock** app (lock icon). The first time you land in **Settings** automatically. Choose a PIN of 4 to 12 digits, repeat it, optionally change the default number of minutes, and tap **Save**. Nothing is blocked until a PIN is set.
4. **Turn on the accessibility service.** In the app tap **Open accessibility settings**. Go to Installed apps > **Play Store Lock** and switch the service on. Confirm the warning.
   - Greyed out, or a **Restricted setting** message? Go to Settings > Apps > **Play Store Lock**, tap ⋮ at the top right and choose **Allow restricted settings**. Then try again. (This appears because the app was installed from outside the Play Store.)
5. **Set the battery to unrestricted.** In the app tap **App settings**, open Battery and choose **Unrestricted**. Otherwise Samsung stops the service after a while. Optionally add the app under Settings > Battery > Background usage limits > Never sleeping apps.
6. **Test it.**
   1. Open the **Play Store**. You go straight to the home screen and the screen "The Play Store is closed" appears.
   2. Type a wrong PIN and tap **Open**. You see "Incorrect PIN code".
   3. Check the **Number of minutes** field (default 10). Use 1 or 2 for a quick test.
   4. Type the correct PIN. The Play Store opens.
   5. After the chosen number of minutes it should close by itself and the PIN screen should appear again.
   6. Optional: type a wrong PIN 5 times. You get "Too many attempts. Wait 30 seconds."
7. **Finish.** Switch Auto Blocker back on. After a restart of the tablet, check in the app that the PIN and the service are still shown as on.

### How a child asks for time

The child opens the Play Store and sees the PIN screen. They ask for a number of minutes, the parent types that number in the **Number of minutes** field and enters the PIN. The Play Store then stays open for exactly that long. It also works the other way round: the parent opens the **Play Store Lock** app itself. Its main screen is the same screen (**Give temporary access**), so you can hand out time without the child opening the Play Store first.

### Changing the PIN or default minutes

Open the app and tap **Settings**. It asks for the current PIN, so only the parent can change anything. Fill in a new PIN twice to change the PIN, or leave the new PIN empty to change only the default number of minutes.

## Install via USB (adb)

An alternative to installing on the tablet itself, useful for rolling out to several tablets.

1. Prepare the laptop with `adb` (Android SDK platform-tools).
2. On the tablet turn on Developer options (Settings > About tablet > Software information, tap **Build number** 7 times) and **USB debugging** (Settings > Developer options). If the switch jumps back, a Family Link child account may be blocking it: do it under the parent profile or temporarily allow it in Family Link.
3. Connect the tablet with a data-capable USB cable. Choose **File transfer** on the tablet and tap **Allow** on the "Allow USB debugging?" prompt (choose "Always allow from this computer").
4. Check that the tablet shows as `device`:
   ```powershell
   adb devices -l
   ```
   Empty list: try another cable or port and install the Samsung USB driver. Status `unauthorized`: confirm the prompt on the tablet. Several devices: add `-s <serial number>` to every `adb` command.
5. Install the downloaded APK:
   ```powershell
   adb install -r .\play-store-lock.apk
   ```
6. Continue with steps 3 to 7 above (set the PIN, turn on the service, battery, test, finish). Switch USB debugging off again afterwards.

The menu paths in this README are for Samsung One UI. Names can differ slightly per version and per tablet language.

## How it works

- An `AccessibilityService` listens for `TYPE_WINDOW_STATE_CHANGED`. When `com.android.vending` comes to the front and no unlock window is active, the tablet goes to the home screen and a PIN screen appears.
- Correct PIN: the Play Store starts and the unlock window runs for the entered number of minutes (1 to 180). The window uses `SystemClock.elapsedRealtime()` plus the boot counter, so changing the clock or rebooting does not extend it.
- When the window ends, the service checks what is in the foreground and closes the Play Store if needed.
- After 5 wrong attempts a waiting time applies that doubles each time (30 s up to 10 min).
- The PIN is set on the device and stored nowhere in readable form: only a salted PBKDF2 hash (SHA-256, 120,000 iterations) in the app's private storage. The APK itself contains no PIN.
- Until a PIN is set, nothing is blocked (nobody could unlock it).
- The app follows the tablet's language automatically. The default is English. Translations: Dutch, German, French and Spanish.

## For developers

```
app/src/main/java/nl/playstoreblocker/
  MainActivity.kt                  launcher screen: give temporary access (PIN + minutes)
  SettingsActivity.kt              settings: status, set/change the PIN
  BlockerAccessibilityService.kt   blocks and watches the unlock window
  BlockActivity.kt                 PIN screen
  PinStore.kt                      salted PBKDF2 hash and default minutes (on the device)
  UnlockState.kt                   unlock window (elapsedRealtime + boot counter)
  PinAttempts.kt                   waiting time after wrong attempts
  Config.kt                        blocked packages, limits
tools/env.ps1                      puts JDK, SDK, adb and Gradle in your PowerShell session
local.properties                   local, NOT in git: path to the Android SDK
keystore.properties                local, NOT in git: release signing key
```

### Set up the build environment (once)

You need **JDK 17**, the **Android SDK** (platform-tools with `adb`, `platforms;android-35`, `build-tools;34.0.0`) and Gradle (the wrapper `gradlew.bat` fetches Gradle 8.9 itself).

1. Install JDK 17 (for example Temurin) and the Android SDK command-line tools, or Android Studio.
2. Accept the SDK licenses and install the packages:
   ```powershell
   sdkmanager --licenses
   sdkmanager "platform-tools" "platforms;android-35" "build-tools;34.0.0"
   ```
3. Create `local.properties` in the project folder:
   ```
   sdk.dir=C\:\\Path\\to\\sdk
   ```
4. Adjust the paths in `tools/env.ps1` if needed and load it in every new PowerShell session (note the dot and space at the start):
   ```powershell
   . .\tools\env.ps1
   ```

### Build

Debug build, installed straight onto connected tablets:

```powershell
.\gradlew.bat installDebug
```

> **Note:** on the original development laptop Gradle did not work from inside Claude Code's own shell (Java selectors failed with "Unable to establish loopback connection"), but it did in a normal PowerShell or Terminal window. If you get that error, build in a normal terminal window.

Watch the logs (tag `PSBlocker`):

```powershell
adb logcat -s PSBlocker:V
```

### Release build

The release APK must be signed. Create a key once and keep it safe (if you lose it, existing installs can no longer be updated):

```powershell
keytool -genkeypair -keystore play-store-lock.jks -alias play-store-lock -keyalg RSA -keysize 4096 -validity 10000 -dname "CN=Play Store Lock"
```

Then create `keystore.properties` in the project folder (it is in `.gitignore`):

```
storeFile=C\:\\Path\\to\\play-store-lock.jks
storePassword=...
keyAlias=play-store-lock
keyPassword=...
```

```powershell
.\gradlew.bat assembleRelease
```

The APK ends up in `app\build\outputs\apk\release\app-release.apk`. Debug and release builds use different keys, so uninstall one before installing the other.

### Adding a language

All texts are in `app/src/main/res/values/strings.xml` (English, the default). Copy that file to `app/src/main/res/values-<language code>/strings.xml` (for example `values-it` for Italian), translate the values and leave the names unchanged. Keep `%1$d` and `\'` as they are. Android picks the tablet's language itself and falls back to English. To translate this README too, copy it to `README.<code>.md` and add a link to the language line at the top.

## Limitations

- **The service can be switched off.** A child can open Settings > Accessibility and switch the service off, force-stop the app (Android then switches the service off too), or uninstall the app. Whether Family Link prevents that has not been tested.
- **No Device Admin.** Uninstalling is therefore not made any harder.
- **Galaxy Store** (`com.sec.android.app.samsungapps`) is a second way to install apps and is **not** blocked. Add it to `BLOCKED_PACKAGES` in `Config.kt` if you want that.
- A short numeric PIN can be guessed by brute force by someone who can read the app's private storage (only possible on a rooted device). Use a longer PIN if that matters to you.
- **Tested so far** on a Samsung Galaxy Tab A9 (Android 16): blocking, entering the correct and wrong PIN, opening the Play Store, closing it again when the window ends, the waiting time after 5 wrong attempts, changing the PIN and default minutes, all five languages, and a tablet restart (the service reconnects by itself and the Play Store is closed again). Not tested: the first start without a PIN, and whether Family Link stops a child from switching the service off.
