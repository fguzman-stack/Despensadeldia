# Privacy

Despensa al Día is designed as an offline-first app.

## Stored Locally

- Inventory products
- Shopping list
- App settings
- Local meal planning data

This data is stored on the device using Android local storage such as Room and SharedPreferences.

## Optional Network Features

Some features may contact public services when the user chooses to use them:

- Barcode and nutrition lookup through OpenFoodFacts
- Online recipe lookup through TheMealDB
- Currency rates through Frankfurter
- Optional Gemini assistant when the user provides an API key

The core inventory does not require these services. If Gemini is used, the prompt can include pantry product names and the user's question.

## Multi-Device Use

There is no project-owned sync server. Multi-device use should rely on export/import or user-controlled file synchronization.
