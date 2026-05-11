# Lecture Reaction Tracker

An Android application that allows students to track their real-time comprehension during lectures. By logging reactions — Understood, Confused, or Lost — at precise timestamps, students can review session analytics and identify which topics caused the most difficulty.

---

## Features

- **Course Management** — Create and manage multiple lecture courses; long-press to delete
- **Live Session Tracking** — Start a tracked session with a running chronometer; log reactions at any moment
- **Reaction Types** — Three reaction buttons: *Understood*, *Confused*, and *Lost*; optional freetext notes on Confused/Lost reactions
- **Session History** — Browse all past sessions per course with start time and reaction count
- **Session Detail & Analytics** — View every timestamped reaction in a session and share a formatted comprehension report via any installed app (e.g., WhatsApp, Gmail)
- **Dark / Light Theme** — Toggle between themes from the toolbar
- **Freemium Model** — Free tier supports up to 2 courses; a simulated in-app purchase unlocks unlimited courses and removes banner ads

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | XML layouts, Material Design 3 components |
| Architecture | MVVM (ViewModel + Repository) |
| Database | Room (SQLite) |
| Async | Kotlin Coroutines & Flow |
| Min SDK | 23 (Android 6.0) |
| Target SDK | 36 |

---

## Architecture

```
app/src/main/java/.../
├── data/
│   ├── dao/          # Room DAOs (CourseDao, SessionDao, ReactionDao)
│   ├── database/     # SystemDatabase (Room singleton)
│   └── entity/       # Data models (Course, Session, Reaction)
├── repository/
│   └── LectureRepo   # Single repository for all data operations
├── viewmodel/
│   ├── MainViewModel         # Course list
│   ├── DetailsViewModel      # Sessions for a course
│   ├── TrackingViewModel     # Active session & reactions
│   ├── SessionDetailViewModel# Reactions for a past session
│   └── ViewModelFactory      # Custom factory for repo injection
├── ui/
│   ├── activity/     # MainActivity, AddCourseActivity, DetailsActivity,
│   │                 # TrackingActivity, SessionDetailActivity, FakeAdActivity
│   └── adapter/      # CourseAdapter, SessionAdapter, ReactionAdapter
└── PremiumManager    # SharedPreferences-backed premium state
```

---

## Setup & Run

### Prerequisites

- Android Studio **Hedgehog (2023.1.1)** or newer
- Android SDK with **API 36** platform installed
- A physical device or emulator running **Android 6.0+**

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/<your-username>/Lecture_Reaction_Tracker.git
   cd Lecture_Reaction_Tracker
   ```

2. **Open in Android Studio**
   - Select *File → Open* and choose the project root folder.

3. **Sync Gradle**
   - Android Studio will prompt you to sync. Click *Sync Now* and wait for dependencies to resolve.

4. **Run the app**
   - Select a connected device or emulator.
   - Click *Run → Run 'app'* (or press `Shift + F10`).

> No API keys, external services, or configuration files are required. The app runs fully offline using a local Room database.

---

## How to Use

1. Launch the app — you will see the **My Courses** screen.
2. Tap **+** to add a course (free tier: max 2 courses).
3. Tap a course card to open its **Session History**.
4. Tap **New Session** to start tracking; the chronometer begins immediately.
5. During the lecture, tap **Understood**, **Confused**, or **Lost** to log a reaction. Confused/Lost prompts an optional note dialog.
6. Tap **Finish Session** to save. Free users will briefly see a simulated ad.
7. Tap any session in the history to view timestamped reactions and tap the **share icon** to export a report.

---
