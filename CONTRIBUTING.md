# Contributing

Despensa al Día is an open source, offline-first Android app. Contributions should keep the app usable without paid services, accounts or a private backend.

## Local Setup

1. Install Android Studio and JDK 17 or newer.
2. Clone the repository.
3. Run `./gradlew testDebugUnitTest assembleDebug`.

## Contribution Rules

- Keep pantry data local by default.
- Do not add required paid services, ads, analytics or account systems.
- If a feature uses the internet, make it optional and clearly disclose what data leaves the device.
- Add tests for data migrations, backup/import logic and non-trivial business rules.
- Never use destructive Room migrations for user data.

## Pull Requests

- Explain the user-facing change.
- Mention privacy or data model implications.
- Include the verification command you ran.
