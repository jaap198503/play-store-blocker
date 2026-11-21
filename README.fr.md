# Verrou Play Store

[English](README.md) · [Nederlands](README.nl.md) · [Deutsch](README.de.md) · **Français** · [Español](README.es.md)

Une application Android qui **bloque toujours** le Google Play Store sur la tablette d'un enfant. Ce n'est qu'après la saisie d'un code PIN par un parent que le Play Store s'ouvre temporairement (10 minutes par défaut, ou le nombre de minutes choisi par le parent), puis il se referme.

Conçue pour une Samsung Galaxy Tab A9 avec un compte enfant Google Family Link. Family Link ne peut pas bloquer le Play Store lui-même, c'est donc cette application qui s'en charge.

> Il s'agit d'**une mesure dissuasive, pas d'une sécurité absolue**. Un adolescent débrouillard peut désactiver le service ou désinstaller l'application. Voir [Limitations](#limitations).

## Téléchargement

**[⬇ Télécharger l'APK (play-store-lock.apk)](https://github.com/jaap198503/play-store-blocker/releases/latest/download/play-store-lock.apk)** · [toutes les versions et sommes de contrôle](https://github.com/jaap198503/play-store-blocker/releases)

Nécessite Android 13 ou une version plus récente. L'APK est signé avec la clé propre au projet et n'est pas distribué via le Play Store. Pour vérifier le téléchargement, compare sa somme SHA-256 à celle de la page de la version :

```powershell
Get-FileHash .\play-store-lock.apk -Algorithm SHA256
```

## Installer sur la tablette

Fais-le en tant que parent, sur la tablette de l'enfant. Aucun ordinateur n'est nécessaire. (Tu préfères un ordinateur ? Voir [Installation par USB](#installation-par-usb-adb).)

1. **Désactive Auto Blocker.** Va dans Paramètres > Sécurité et confidentialité > Auto Blocker et désactive-le. Il bloque les installations venant de l'extérieur du Play Store. (Tu le réactiveras à l'étape 7.)
2. **Télécharge et installe l'APK.** Ouvre le lien de téléchargement ci-dessus dans le navigateur de la tablette. Ouvre le fichier téléchargé. Si on te le demande, autorise le navigateur à **installer des applications inconnues** et touche **Installer**. Si Play Protect signale un développeur inconnu, choisis **Plus de détails > Installer quand même**.
3. **Définis le code PIN.** Ouvre l'application **Verrou Play Store** (icône de cadenas). La première fois, tu arrives automatiquement dans **Paramètres**. Choisis un code PIN de 4 à 12 chiffres, répète-le, modifie éventuellement le nombre de minutes par défaut et touche **Enregistrer**. Rien n'est bloqué tant qu'aucun code PIN n'est défini.
4. **Active le service d'accessibilité.** Dans l'application, touche **Ouvrir les paramètres d'accessibilité**. Va dans Applications installées > **Verrou Play Store** et active le service. Confirme l'avertissement.
   - Grisé, ou message **Paramètre restreint** ? Va dans Paramètres > Applications > **Verrou Play Store**, touche ⋮ en haut à droite et choisis **Autoriser les paramètres restreints**. Réessaie ensuite. (Cela apparaît parce que l'application provient de l'extérieur du Play Store.)
5. **Mets la batterie sur sans restriction.** Dans l'application, touche **Paramètres de l'appli**, ouvre Batterie et choisis **Sans restriction**. Sinon Samsung arrête le service au bout d'un moment. En option : ajoute l'application dans Paramètres > Batterie > Limites d'utilisation en arrière-plan > Applications jamais en veille.
6. **Teste.**
   1. Ouvre le **Play Store**. Tu reviens directement à l'écran d'accueil et l'écran « Le Play Store est fermé » apparaît.
   2. Saisis un code PIN erroné et touche **Ouvrir**. Tu vois « Code PIN incorrect ».
   3. Vérifie le champ **Nombre de minutes** (10 par défaut). Prends 1 ou 2 pour un test rapide.
   4. Saisis le bon code PIN. Le Play Store s'ouvre.
   5. Après le nombre de minutes choisi, il doit se fermer tout seul et l'écran du code PIN doit réapparaître.
   6. En option : saisis 5 fois un code PIN erroné. Tu obtiens « Trop de tentatives. Patiente 30 secondes. »
7. **Terminer.** Réactive Auto Blocker. Après un redémarrage de la tablette, vérifie dans l'application que le code PIN et le service sont toujours indiqués comme activés.

### Comment un enfant demande du temps

L'enfant ouvre le Play Store et voit l'écran du code PIN. Il demande un certain nombre de minutes, le parent saisit ce nombre dans le champ **Nombre de minutes** et entre le code PIN. Le Play Store reste alors ouvert exactement ce temps-là. L'inverse est possible aussi : le parent ouvre lui-même l'application **Verrou Play Store**. Son écran principal est le même écran (**Donner un accès temporaire**), ce qui permet d'accorder du temps sans que l'enfant ouvre d'abord le Play Store.

### Modifier le code PIN ou les minutes par défaut

Ouvre l'application et touche **Paramètres**. Elle y demande le code PIN actuel : seul le parent peut donc modifier quelque chose. Saisis deux fois un nouveau code PIN pour le changer, ou laisse le nouveau code PIN vide pour ne modifier que le nombre de minutes par défaut.

## Installation par USB (adb)

Une alternative à l'installation directement sur la tablette, utile pour plusieurs tablettes.

1. Prépare l'ordinateur avec `adb` (Android SDK platform-tools).
2. Sur la tablette, active les Options pour les développeurs (Paramètres > À propos de la tablette > Informations sur le logiciel, touche 7 fois **Numéro de build**) et le **Débogage USB** (Paramètres > Options pour les développeurs). Si le commutateur revient en arrière, un compte enfant Family Link le bloque peut-être : fais-le depuis le profil parent ou autorise-le temporairement dans Family Link.
3. Branche la tablette avec un câble USB qui transmet aussi les données. Choisis **Transfert de fichiers** sur la tablette et touche **Autoriser** à la question « Autoriser le débogage USB ? » (choisis « Toujours autoriser depuis cet ordinateur »).
4. Vérifie que la tablette apparaît avec l'état `device` :
   ```powershell
   adb devices -l
   ```
   Liste vide : essaie un autre câble ou port et installe le pilote USB Samsung. État `unauthorized` : confirme la demande sur la tablette. Plusieurs appareils : ajoute `-s <numéro de série>` à chaque commande `adb`.
5. Installe l'APK téléchargé :
   ```powershell
   adb install -r .\play-store-lock.apk
   ```
6. Poursuis avec les étapes 3 à 7 ci-dessus (définir le code PIN, activer le service, batterie, tester, terminer). Désactive ensuite le débogage USB.

Les chemins de menu de ce README concernent Samsung One UI. Les noms peuvent légèrement varier selon la version et la langue de la tablette.

## Fonctionnement

- Un `AccessibilityService` écoute `TYPE_WINDOW_STATE_CHANGED`. Quand `com.android.vending` passe au premier plan sans fenêtre de déverrouillage active, la tablette revient à l'écran d'accueil et un écran de code PIN apparaît.
- Bon code PIN : le Play Store démarre et la fenêtre de déverrouillage dure le nombre de minutes saisi (1 à 180). La fenêtre utilise `SystemClock.elapsedRealtime()` plus le compteur de démarrages ; changer l'heure ou redémarrer ne la prolonge donc pas.
- À la fin de la fenêtre, le service vérifie ce qui est au premier plan et ferme le Play Store si nécessaire.
- Après 5 tentatives erronées, un délai d'attente s'applique et double à chaque fois (30 s jusqu'à 10 min maximum).
- Le code PIN se définit sur l'appareil et n'est stocké nulle part de façon lisible : seulement un hachage PBKDF2 salé (SHA-256, 120 000 itérations) dans le stockage privé de l'application. L'APK lui-même ne contient aucun code PIN.
- Tant qu'aucun code PIN n'est défini, rien n'est bloqué (personne ne pourrait déverrouiller).
- L'application suit automatiquement la langue de la tablette. La langue par défaut est l'anglais. Traductions : néerlandais, allemand, français et espagnol.

## Pour les développeurs

```
app/src/main/java/nl/playstoreblocker/
  MainActivity.kt                  écran principal : accès temporaire (PIN + minutes)
  SettingsActivity.kt              paramètres : état, définir/modifier le code PIN
  BlockerAccessibilityService.kt   bloque et surveille la fenêtre de déverrouillage
  BlockActivity.kt                 écran du code PIN
  PinStore.kt                      hachage PBKDF2 salé et minutes par défaut (sur l'appareil)
  UnlockState.kt                   fenêtre de déverrouillage (elapsedRealtime + compteur de démarrages)
  PinAttempts.kt                   délai d'attente après des tentatives erronées
  Config.kt                        paquets bloqués, limites
tools/env.ps1                      place JDK, SDK, adb et Gradle dans ta session PowerShell
local.properties                   local, PAS dans git : chemin du SDK Android
keystore.properties                local, PAS dans git : clé de signature de la version release
```

### Préparer l'environnement de build (une fois)

Il te faut le **JDK 17**, le **SDK Android** (platform-tools avec `adb`, `platforms;android-35`, `build-tools;34.0.0`) et Gradle (le wrapper `gradlew.bat` télécharge lui-même Gradle 8.9).

1. Installe le JDK 17 (par exemple Temurin) et les outils en ligne de commande du SDK Android, ou Android Studio.
2. Accepte les licences du SDK et installe les paquets :
   ```powershell
   sdkmanager --licenses
   sdkmanager "platform-tools" "platforms;android-35" "build-tools;34.0.0"
   ```
3. Crée `local.properties` dans le dossier du projet :
   ```
   sdk.dir=C\:\\Chemin\\vers\\sdk
   ```
4. Adapte si besoin les chemins dans `tools/env.ps1` et charge-le dans chaque nouvelle session PowerShell (attention au point et à l'espace au début) :
   ```powershell
   . .\tools\env.ps1
   ```

### Compiler

Version debug, installée directement sur les tablettes branchées :

```powershell
.\gradlew.bat installDebug
```

> **Remarque :** sur l'ordinateur de développement d'origine, Gradle ne fonctionnait pas depuis le shell propre de Claude Code (les sélecteurs Java échouaient avec « Unable to establish loopback connection »), mais fonctionnait dans une fenêtre PowerShell ou Terminal normale. Si tu rencontres cette erreur, compile dans une fenêtre de terminal normale.

Consulter les journaux (tag `PSBlocker`) :

```powershell
adb logcat -s PSBlocker:V
```

### Version release

L'APK release doit être signé. Crée une clé une seule fois et garde-la précieusement (si tu la perds, les installations existantes ne pourront plus être mises à jour) :

```powershell
keytool -genkeypair -keystore play-store-lock.jks -alias play-store-lock -keyalg RSA -keysize 4096 -validity 10000 -dname "CN=Play Store Lock"
```

Crée ensuite `keystore.properties` dans le dossier du projet (il figure dans `.gitignore`) :

```
storeFile=C\:\\Chemin\\vers\\play-store-lock.jks
storePassword=...
keyAlias=play-store-lock
keyPassword=...
```

```powershell
.\gradlew.bat assembleRelease
```

L'APK se trouve ensuite dans `app\build\outputs\apk\release\app-release.apk`. Les versions debug et release utilisent des clés différentes : désinstalle donc l'une avant d'installer l'autre.

### Ajouter une langue

Tous les textes se trouvent dans `app/src/main/res/values/strings.xml` (anglais, la langue par défaut). Copie ce fichier vers `app/src/main/res/values-<code de langue>/strings.xml` (par exemple `values-it` pour l'italien), traduis les valeurs et laisse les noms inchangés. Garde `%1$d` et `\'` tels quels. Android choisit lui-même la langue de la tablette et revient à l'anglais à défaut. Pour traduire aussi ce README, copie-le vers `README.<code>.md` et ajoute un lien à la ligne des langues en haut.

## Limitations

- **Le service peut être désactivé.** Un enfant peut ouvrir Paramètres > Accessibilité et désactiver le service, forcer l'arrêt de l'application (Android désactive alors aussi le service), ou désinstaller l'application. Il n'a pas été testé si Family Link l'en empêche.
- **Pas de Device Admin.** La désinstallation n'est donc pas rendue plus difficile.
- Le **Galaxy Store** (`com.sec.android.app.samsungapps`) est une deuxième voie pour installer des applications et n'est **pas** bloqué. Ajoute-le à `BLOCKED_PACKAGES` dans `Config.kt` si tu le souhaites.
- Un code PIN numérique court peut être deviné par force brute par quiconque peut lire le stockage privé de l'application (possible uniquement sur un appareil rooté). Utilise un code PIN plus long si cela compte pour toi.
- **Testé jusqu'ici** sur une Samsung Galaxy Tab A9 (Android 16) : blocage, saisie d'un bon et d'un mauvais code PIN, ouverture du Play Store, nouvelle fermeture à la fin de la fenêtre, délai d'attente après 5 tentatives erronées, modification du code PIN et des minutes par défaut, les cinq langues et un redémarrage de la tablette (le service se reconnecte seul et le Play Store est de nouveau fermé). Non testé : le premier démarrage sans code PIN, et si Family Link empêche un enfant de désactiver le service.
