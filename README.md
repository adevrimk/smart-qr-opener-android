# Smart QR Opener

Android-only QR scanner and smart opener.

## Requirements

- Android Studio Hedgehog or newer
- JDK 17
- Android SDK installed

## Clone

```bash
git clone https://github.com/adevrimk/smart-qr-opener-android.git
cd smart-qr-opener-android
```

## Local setup

Create `local.properties` at the project root if Android Studio does not generate it automatically:

```properties
sdk.dir=C:\\Users\\ADK\\AppData\\Local\\Android\\Sdk
```

## Build

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

## Run

- Open the project in Android Studio
- Select an Android device or emulator
- Run the `app` configuration

## Current focus

- Smart scanner flow
- History and settings
- Security-aware URL handling
- Quick shortcuts
- Gallery scan support
