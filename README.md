# Scam Message Analyzer

An Android cybersecurity proof of concept that analyses suspicious messages locally and explains why a message may be risky. Users can paste text manually or, after granting permission, scan recent and incoming SMS messages.

**Originally developed:** 29 March 2026.

## Features

- Rule-based scam detection for urgency, threats, fake rewards, requests for money, and requests for sensitive data
- URL extraction and heuristic checks for shorteners, insecure HTTP, raw IP addresses, punycode, unusual subdomains, risky top-level domains, and suspicious paths
- Clear risk score with an explanation of every contributing signal
- Optional recent-SMS scanning and monitoring of incoming SMS messages
- Duplicate prevention for previously processed messages
- Local incident history using Room
- Local password gate using PBKDF2-HMAC-SHA256 with a random salt
- Jetpack Compose interface with separate analysis, monitoring, and history screens

## Technology

- Kotlin
- Android SDK and Jetpack Compose
- Room database
- Kotlin coroutines and Flow
- Gradle Kotlin DSL

## Privacy and security

Analysis is performed on the device and the application does not send messages to an external service. SMS access is optional and requires Android runtime permission. Cloud backup is disabled to reduce the risk of message history being copied outside the device.

The password protects access through the application interface, but the Room database is not encrypted at rest. This repository is an educational prototype, not a replacement for professional anti-phishing software.

## Run locally

1. Install Android Studio and Android SDK 36.
2. Clone the repository and open its root folder in Android Studio.
3. Allow Gradle to synchronise the project dependencies.
4. Run the `app` configuration on an Android 7.0 (API 24) or newer emulator/device.
5. Grant SMS permissions only if you want to use SMS monitoring; manual analysis works without them.

Command-line build on Windows:

```powershell
.\gradlew.bat assembleDebug
```

Command-line build on macOS or Linux:

```bash
./gradlew assembleDebug
```

## Repository scope

Generated build output, IDE caches, local SDK paths, APK files, the large demonstration video, and coursework documents are intentionally excluded from the repository.
