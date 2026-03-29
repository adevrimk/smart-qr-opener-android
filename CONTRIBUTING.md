# Contributing

Thanks for helping improve Smart QR Opener.

## Before you start

- Pull the latest `main`
- Make sure `./gradlew testDebugUnitTest assembleDebug` passes
- Keep changes focused and small when possible

## Local setup

```bash
git clone https://github.com/adevrimk/smart-qr-opener-android.git
cd smart-qr-opener-android
```

Create `local.properties` if needed:

```properties
sdk.dir=C:\\Users\\ADK\\AppData\\Local\\Android\\Sdk
```

## What to include in a PR

- A clear summary of the change
- How it was tested
- Screenshots for UI work
- Notes for any behavior change

## Coding style

- Prefer small, readable functions
- Keep scanner, decoder, action, history, and settings logic separated
- Avoid adding unused settings or UI controls

## Testing

Run:

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

