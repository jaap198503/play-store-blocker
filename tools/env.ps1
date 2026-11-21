<#
.SYNOPSIS
  Zet JDK 17, Android SDK, adb en Gradle in deze PowerShell-sessie.
  Gebruik:  . .\tools\env.ps1     (let op de punt + spatie vooraan)
#>
$toolsRoot = 'C:\Users\JaapScheper\android-tools'

$env:JAVA_HOME        = Join-Path $toolsRoot 'jdk17'
$env:ANDROID_HOME     = Join-Path $toolsRoot 'sdk'
$env:ANDROID_SDK_ROOT = $env:ANDROID_HOME

$env:Path = @(
    (Join-Path $env:JAVA_HOME 'bin'),
    (Join-Path $env:ANDROID_HOME 'platform-tools'),
    (Join-Path $env:ANDROID_HOME 'cmdline-tools\latest\bin'),
    (Join-Path $toolsRoot 'gradle-8.9\bin'),
    $env:Path
) -join ';'

Write-Host "JAVA_HOME=$env:JAVA_HOME"
Write-Host "ANDROID_HOME=$env:ANDROID_HOME"
