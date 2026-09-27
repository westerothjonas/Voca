# Project Plan

App mit Firebase Integration zum Vokabeln lernen mit schönem, ansprechendem und natürlichem Design. Unterstützung für regelmäßige und unregelmäßige Verben/Vokabeln (mit Simple Past und Past Participle für unregelmäßige Verben).

## Project Brief

# Project Brief: Voca - Vocabulary Learning App

## Features

1. **Firebase Authentication & Cloud Sync**: Secure user sign- in and seamless cloud synchronization of user vocabulary lists and learning progress via Firebase Cloud Firestore.
2. **Vocabulary & Verb Management**: Support for creating, organizing, and categorizing both regular and irregular verbs and vocabulary items.
3. **Advanced Verb Forms (Simple Past & Past Participle)**: Specialized data structures and UI views dedicated to capturing and testing base forms, Simple Past, and Past Participle forms for irregular verbs.
4. **Interactive Flashcard Learning Mode**: An engaging, beautifully styled review session utilizing natural design principles for active recall and self-assessment.

---

## High-Level Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose & Material 3
- **Asynchronous Processing**: Kotlin Coroutines & StateFlow
- **Navigation**: Jetpack Navigation 3 (State-driven navigation model)
- **Adaptive Layouts**: Compose Material Adaptive library (supporting multi-pane and responsive layouts across phones, foldables, and tablets)
- **Backend & Cloud Persistence**: Firebase Authentication & Cloud Firestore (required for cloud sync and user vocabulary storage)

## Implementation Steps
**Total Duration:** 9m 5s

### 1: Firebase E-Mail & Passwort Authentifizierung (Registrierung & Login Screen auf Deutsch)
- **Status:** COMPLETED
- **Updates:** Package name confirmed as com.example.voca. google-services.json location clarified (app/google-services.json). Email & password auth, vocabulary packs, Cabu-style typing exercise mode, and full German localization implemented.
- **Acceptance Criteria:**
  - E-Mail und Passwort Registrierung & Login UI
  - Firebase Auth Integration mit Fehlerbehandlung
  - Automatischer Login-Status

### 2: Vokabel-Pakete Verwaltung & Entfernen von Kategorien
- **Status:** COMPLETED
- **Updates:** Vocabulary packs implemented and categories fully removed.
- **Acceptance Criteria:**
  - Vokabel-Pakete mit Zeitraum/Datum erstellen und verwalten
  - Kategorien komplett entfernt und durch Pakete ersetzt
  - Vokabeln und unregelmäßige Verben gehören zu Paketen
- **Duration:** 2m 37s

### 3: Cabu-Style Schreib-Übungsmodus (Eintippen/Abschreiben) & Lern-Erinnerungen
- **Status:** COMPLETED
- **Updates:** Cabu-style typing exercise mode and daily notifications reminder implemented.
- **Acceptance Criteria:**
  - Interaktiver Schreib-Modus (Wort/Übersetzung/Simple Past/Past Participle eintippen & prüfen)
  - Erinnerungsfunktion für Lernpakete
- **Duration:** 2m 26s

### 4: Komplette deutsche Lokalisierung, Platzhalter entfernen & Verifikation
- **Status:** COMPLETED
- **Updates:** Full German localization, placeholder removal, and build verification completed successfully.
- **Acceptance Criteria:**
  - 100% Deutsche Sprache in allen Texten und Dialogen
  - Keine Dummy-/Platzhalter-Daten mehr
  - Erfolgreicher Build und Test
- **Duration:** 4m 2s

