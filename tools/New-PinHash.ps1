<#
.SYNOPSIS
  Maakt blocker.properties met een salted PBKDF2-hash van de pincode.

.DESCRIPTION
  De pincode zelf wordt nergens opgeslagen, alleen salt + hash.
  Zonder -Pin vraagt het script er (verborgen) om.
  Na wijzigen opnieuw bouwen en installeren: ./gradlew installDebug

.EXAMPLE
  powershell -File tools/New-PinHash.ps1
  powershell -File tools/New-PinHash.ps1 -UnlockMinutes 15
#>
[CmdletBinding()]
param(
    [string]$Pin,
    [int]$UnlockMinutes = 10,
    [string]$OutFile
)

$ErrorActionPreference = 'Stop'

if (-not $OutFile) {
    $scriptDir = if ($PSScriptRoot) { $PSScriptRoot } else { Split-Path -Parent $MyInvocation.MyCommand.Path }
    $OutFile = Join-Path $scriptDir '..\blocker.properties'
}

if (-not $Pin) {
    $secure = Read-Host -AsSecureString 'Pincode (4 tot 12 cijfers)'
    $bstr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)
    try { $Pin = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($bstr) }
    finally { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr) }
}

# Alleen cijfers: dan is de bytecodering (UTF-8 hier, UTF-8 in Android) gegarandeerd gelijk.
if ($Pin -notmatch '^\d{4,12}$') { throw 'De pincode moet uit 4 tot 12 cijfers bestaan.' }
if ($UnlockMinutes -lt 1 -or $UnlockMinutes -gt 240) { throw 'UnlockMinutes moet tussen 1 en 240 liggen.' }

# Moet overeenkomen met PinVerifier.kt (leest dit ook uit blocker.properties).
$iterations = 120000

$salt = New-Object byte[] 16
$rng = [Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($salt)
$rng.Dispose()

$kdf = New-Object Security.Cryptography.Rfc2898DeriveBytes(
    $Pin, $salt, $iterations, [Security.Cryptography.HashAlgorithmName]::SHA256)
$hash = $kdf.GetBytes(32)
$kdf.Dispose()

function ConvertTo-Hex([byte[]]$Bytes) { -join ($Bytes | ForEach-Object { $_.ToString('x2') }) }

$lines = @(
    '# Lokaal bestand, staat in .gitignore. Bevat alleen een salted PBKDF2-hash, geen pincode.',
    "pinSalt=$(ConvertTo-Hex $salt)",
    "pinHash=$(ConvertTo-Hex $hash)",
    "pinIterations=$iterations",
    "unlockMinutes=$UnlockMinutes"
)
Set-Content -Path $OutFile -Value $lines -Encoding ascii

Write-Host "Geschreven: $((Resolve-Path $OutFile).Path)"
Write-Host "Ontgrendelduur: $UnlockMinutes minuten"
