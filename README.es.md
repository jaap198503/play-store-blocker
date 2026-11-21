# Bloqueo de Play Store

[English](README.md) · [Nederlands](README.nl.md) · [Deutsch](README.de.md) · [Français](README.fr.md) · **Español**

Una aplicación Android que **bloquea siempre** Google Play Store en la tableta de un niño. Solo después de que un padre o una madre introduzca un código PIN se abre Play Store temporalmente (10 minutos por defecto, o el número de minutos que elija el adulto) y luego se vuelve a cerrar.

Creada para una Samsung Galaxy Tab A9 con una cuenta infantil de Google Family Link. Family Link no puede bloquear Play Store por sí mismo, así que lo hace esta aplicación.

> Esto es **disuasión, no seguridad absoluta**. Un adolescente hábil puede desactivar el servicio o desinstalar la aplicación. Consulta [Limitaciones](#limitaciones).

## Descarga

**[⬇ Descargar el APK (play-store-lock.apk)](https://github.com/jaap198503/play-store-blocker/releases/latest/download/play-store-lock.apk)** · [todas las versiones y sumas de verificación](https://github.com/jaap198503/play-store-blocker/releases)

Requiere Android 13 o posterior. El APK está firmado con la clave propia del proyecto y no se distribuye a través de Play Store. Para verificar la descarga, compara su SHA-256 con el de la página de la versión:

```powershell
Get-FileHash .\play-store-lock.apk -Algorithm SHA256
```

## Instalar en la tableta

Hazlo como padre o madre, en la tableta del niño. No hace falta ordenador. (¿Prefieres un ordenador? Consulta [Instalación por USB](#instalación-por-usb-adb).)

1. **Desactiva Auto Blocker.** Ve a Ajustes > Seguridad y privacidad > Auto Blocker y desactívalo. Bloquea las instalaciones desde fuera de Play Store. (Lo volverás a activar en el paso 7.)
2. **Descarga e instala el APK.** Abre el enlace de descarga de arriba en el navegador de la tableta. Abre el archivo descargado. Si se te pide, permite que el navegador **instale aplicaciones desconocidas** y toca **Instalar**. Si Play Protect avisa de un desarrollador desconocido, elige **Más detalles > Instalar de todos modos**.
3. **Establece el PIN.** Abre la aplicación **Bloqueo de Play Store** (icono de candado). La primera vez irás automáticamente a **Ajustes**. Elige un PIN de 4 a 12 dígitos, repítelo, cambia si quieres el número de minutos predeterminado y toca **Guardar**. No se bloquea nada mientras no haya un PIN establecido.
4. **Activa el servicio de accesibilidad.** En la aplicación toca **Abrir ajustes de accesibilidad**. Ve a Aplicaciones instaladas > **Bloqueo de Play Store** y activa el servicio. Confirma la advertencia.
   - ¿Aparece atenuado o el mensaje **Ajuste restringido**? Ve a Ajustes > Aplicaciones > **Bloqueo de Play Store**, toca ⋮ arriba a la derecha y elige **Permitir ajustes restringidos**. Inténtalo de nuevo. (Aparece porque la aplicación se instaló desde fuera de Play Store.)
5. **Pon la batería en sin restricciones.** En la aplicación toca **Ajustes de la app**, abre Batería y elige **Sin restricciones**. Si no, Samsung detiene el servicio pasado un tiempo. Opcional: añade la aplicación en Ajustes > Batería > Límites de uso en segundo plano > Apps que nunca se suspenden.
6. **Pruébalo.**
   1. Abre **Play Store**. Vas directamente a la pantalla de inicio y aparece la pantalla «Play Store está cerrada».
   2. Escribe un PIN incorrecto y toca **Abrir**. Verás «Código PIN incorrecto».
   3. Comprueba el campo **Número de minutos** (10 por defecto). Usa 1 o 2 para una prueba rápida.
   4. Escribe el PIN correcto. Play Store se abre.
   5. Pasado el número de minutos elegido, debería cerrarse solo y volver a aparecer la pantalla del PIN.
   6. Opcional: escribe un PIN incorrecto 5 veces. Verás «Demasiados intentos. Espera 30 segundos.»
7. **Terminar.** Vuelve a activar Auto Blocker. Tras reiniciar la tableta, comprueba en la aplicación que el PIN y el servicio siguen indicados como activados.

### Cómo pide tiempo un niño

El niño abre Play Store y ve la pantalla del PIN. Pide un número de minutos, el adulto escribe ese número en el campo **Número de minutos** e introduce el PIN. Play Store permanece abierta exactamente ese tiempo. También se puede al revés: el adulto abre la propia aplicación **Bloqueo de Play Store**. Su pantalla principal es la misma pantalla (**Dar acceso temporal**), así que puedes dar tiempo sin que el niño abra antes Play Store.

### Cambiar el PIN o los minutos predeterminados

Abre la aplicación y toca **Ajustes**. Allí pide el PIN actual, así que solo el adulto puede cambiar algo. Escribe un PIN nuevo dos veces para cambiarlo, o deja el PIN nuevo vacío para cambiar solo el número de minutos predeterminado.

## Instalación por USB (adb)

Una alternativa a instalar en la propia tableta, útil para varias tabletas.

1. Prepara el ordenador con `adb` (Android SDK platform-tools).
2. En la tableta activa las Opciones de desarrollador (Ajustes > Acerca de la tablet > Información del software, toca 7 veces **Número de compilación**) y la **Depuración por USB** (Ajustes > Opciones de desarrollador). Si el interruptor vuelve atrás, quizá lo bloquea una cuenta infantil de Family Link: hazlo desde el perfil del adulto o permítelo temporalmente en Family Link.
3. Conecta la tableta con un cable USB que también transmita datos. Elige **Transferencia de archivos** en la tableta y toca **Permitir** en «¿Permitir la depuración por USB?» (elige «Permitir siempre desde este ordenador»).
4. Comprueba que la tableta aparece con el estado `device`:
   ```powershell
   adb devices -l
   ```
   Lista vacía: prueba otro cable o puerto e instala el controlador USB de Samsung. Estado `unauthorized`: confirma el aviso en la tableta. Varios dispositivos: añade `-s <número de serie>` a cada comando `adb`.
5. Instala el APK descargado:
   ```powershell
   adb install -r .\play-store-lock.apk
   ```
6. Continúa con los pasos 3 a 7 de arriba (establecer el PIN, activar el servicio, batería, probar, terminar). Después desactiva la depuración por USB.

Las rutas de menú de este README son de Samsung One UI. Los nombres pueden variar ligeramente según la versión y el idioma de la tableta.

## Cómo funciona

- Un `AccessibilityService` escucha `TYPE_WINDOW_STATE_CHANGED`. Cuando `com.android.vending` pasa a primer plano sin una ventana de desbloqueo activa, la tableta vuelve a la pantalla de inicio y aparece una pantalla de PIN.
- PIN correcto: Play Store se inicia y la ventana de desbloqueo dura el número de minutos introducido (1 a 180). La ventana usa `SystemClock.elapsedRealtime()` más el contador de arranques, así que cambiar la hora o reiniciar no la alarga.
- Al terminar la ventana, el servicio comprueba qué hay en primer plano y cierra Play Store si hace falta.
- Tras 5 intentos fallidos se aplica un tiempo de espera que se duplica cada vez (de 30 s hasta un máximo de 10 min).
- El PIN se establece en el dispositivo y no se guarda en ningún sitio de forma legible: solo un hash PBKDF2 con sal (SHA-256, 120 000 iteraciones) en el almacenamiento privado de la aplicación. El APK en sí no contiene ningún PIN.
- Mientras no haya un PIN establecido, no se bloquea nada (nadie podría desbloquear).
- La aplicación sigue automáticamente el idioma de la tableta. El predeterminado es el inglés. Traducciones: neerlandés, alemán, francés y español.

## Para desarrolladores

```
app/src/main/java/nl/playstoreblocker/
  MainActivity.kt                  pantalla principal: dar acceso temporal (PIN + minutos)
  SettingsActivity.kt              ajustes: estado, establecer o cambiar el PIN
  BlockerAccessibilityService.kt   bloquea y vigila la ventana de desbloqueo
  BlockActivity.kt                 pantalla del PIN
  PinStore.kt                      hash PBKDF2 con sal y minutos predeterminados (en el dispositivo)
  UnlockState.kt                   ventana de desbloqueo (elapsedRealtime + contador de arranques)
  PinAttempts.kt                   tiempo de espera tras intentos fallidos
  Config.kt                        paquetes bloqueados, límites
tools/env.ps1                      pone JDK, SDK, adb y Gradle en tu sesión de PowerShell
local.properties                   local, NO en git: ruta al SDK de Android
keystore.properties                local, NO en git: clave de firma de la versión release
```

### Preparar el entorno de compilación (una vez)

Necesitas el **JDK 17**, el **SDK de Android** (platform-tools con `adb`, `platforms;android-35`, `build-tools;34.0.0`) y Gradle (el wrapper `gradlew.bat` descarga Gradle 8.9 por sí mismo).

1. Instala el JDK 17 (por ejemplo Temurin) y las herramientas de línea de comandos del SDK de Android, o Android Studio.
2. Acepta las licencias del SDK e instala los paquetes:
   ```powershell
   sdkmanager --licenses
   sdkmanager "platform-tools" "platforms;android-35" "build-tools;34.0.0"
   ```
3. Crea `local.properties` en la carpeta del proyecto:
   ```
   sdk.dir=C\:\\Ruta\\al\\sdk
   ```
4. Ajusta si hace falta las rutas de `tools/env.ps1` y cárgalo en cada sesión nueva de PowerShell (fíjate en el punto y el espacio del principio):
   ```powershell
   . .\tools\env.ps1
   ```

### Compilar

Versión debug, instalada directamente en las tabletas conectadas:

```powershell
.\gradlew.bat installDebug
```

> **Nota:** en el ordenador de desarrollo original, Gradle no funcionaba desde el propio shell de Claude Code (los selectores de Java fallaban con «Unable to establish loopback connection»), pero sí en una ventana normal de PowerShell o Terminal. Si te aparece ese error, compila en una ventana de terminal normal.

Ver los registros (etiqueta `PSBlocker`):

```powershell
adb logcat -s PSBlocker:V
```

### Versión release

El APK release debe estar firmado. Crea una clave una sola vez y guárdala bien (si la pierdes, las instalaciones existentes ya no se podrán actualizar):

```powershell
keytool -genkeypair -keystore play-store-lock.jks -alias play-store-lock -keyalg RSA -keysize 4096 -validity 10000 -dname "CN=Play Store Lock"
```

Luego crea `keystore.properties` en la carpeta del proyecto (está en `.gitignore`):

```
storeFile=C\:\\Ruta\\a\\play-store-lock.jks
storePassword=...
keyAlias=play-store-lock
keyPassword=...
```

```powershell
.\gradlew.bat assembleRelease
```

El APK queda en `app\build\outputs\apk\release\app-release.apk`. Las versiones debug y release usan claves distintas, así que desinstala una antes de instalar la otra.

### Añadir un idioma

Todos los textos están en `app/src/main/res/values/strings.xml` (inglés, el predeterminado). Copia ese archivo a `app/src/main/res/values-<código de idioma>/strings.xml` (por ejemplo `values-it` para italiano), traduce los valores y deja los nombres sin cambiar. Mantén `%1$d` y `\'` tal cual. Android elige por sí mismo el idioma de la tableta y recurre al inglés si falta. Para traducir también este README, cópialo a `README.<código>.md` y añade un enlace a la línea de idiomas de arriba.

## Limitaciones

- **El servicio se puede desactivar.** Un niño puede abrir Ajustes > Accesibilidad y desactivar el servicio, forzar la detención de la aplicación (Android también desactiva entonces el servicio) o desinstalar la aplicación. No se ha probado si Family Link lo impide.
- **Sin Device Admin.** Por tanto, desinstalar no se hace más difícil.
- **Galaxy Store** (`com.sec.android.app.samsungapps`) es una segunda vía para instalar aplicaciones y **no** está bloqueada. Añádela a `BLOCKED_PACKAGES` en `Config.kt` si lo deseas.
- Un PIN numérico corto se puede adivinar por fuerza bruta si alguien puede leer el almacenamiento privado de la aplicación (solo posible en un dispositivo con root). Usa un PIN más largo si eso te importa.
- **Probado hasta ahora** en una Samsung Galaxy Tab A9 (Android 16): bloqueo, introducción de un PIN correcto y de uno incorrecto, apertura de Play Store, cierre de nuevo al terminar la ventana, tiempo de espera tras 5 intentos fallidos, cambio del PIN y de los minutos predeterminados, los cinco idiomas y un reinicio de la tableta (el servicio se reconecta solo y Play Store vuelve a estar cerrada). No probado: el primer inicio sin PIN y si Family Link impide que un niño desactive el servicio.
