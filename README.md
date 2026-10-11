# SME South Africa — Personalised Business Feed

> **Business made easier.** A smart Android app that learns what you care about and delivers relevant funding, marketing, tax, technology and compliance content for South African entrepreneurs.

[[Android](https://img.shields.io/badge/Android-7.0%2B-brightgreen)]()
[[Kotlin](https://img.shields.io/badge/Kotlin-1.9-purple)]()
[[Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-blue)]()
[[Firebase](https://img.shields.io/badge/Firebase-Auth%20%2B%20Firestore-orange)]()
[[Region](https://img.shields.io/badge/Region-africa--south1-green)]()

### 📲 Repositories

- **GitHub (Primary):** https://github.com/Wasama1904/Recommendation-System-App
- **Azure DevOps (Mirror + Boards):** https://dev.azure.com/Recommendation-System-App/SME-Recommendation%20System%20App/_git/Recommendation-System-App
- **Firebase Project:** `Recommendation-System-App` — Firestore in `africa-south1` (Cape Town)

---

## ✨ What it does

SME South Africa has 100,000+ monthly readers, but everyone sees the same generic content. This app fixes that with a transparent, on-device recommendation engine.

- Select your business interests (Funding, Marketing, Tax, Tech, etc.)
- Search, read and save articles
- Your feed re-orders in real-time based on your behaviour
- No black box — you can see the relevance Score on every card

### Core Features

- **Personalised Feed** — Sorted by relevance Score
- **Smart Recommendations** — Learns from interests, searches, reads, saves and likes
- **Explore & Search** — Category filters + recent searches
- **Saved Library** — Your personal offline-ready collection
- **Dark Theme** — Full Material 3 light/dark support
- **Profile & Settings** — Edit interests, notifications, newsletter, privacy
- **Help & Support** — Dedicated help screen, no clutter

---

## 🧠 How the recommendation works

Transparent scoring, not magic:

```
Score = Interest Match (+5)
      + Search Match (+4)
      + Reading History (+3 per read)
      + Deep Read >60s (+4)
      + Same Category (+2)
      + Saved (+5)
      + Liked (+5)
```

**Example:**
You select Funding (+5), search "funding" (+4), read a Funding article for 85s (+3+4) = **Score 16** → Moves to top of your feed.

All behaviour is stored in Firestore collections: `behaviourLogs`, `searchLogs`, `preferences`, `savedArticles`.

---

## 📱 Screenshots

| Home Feed | Explore | Saved | Profile | Help |
|-----------|---------|-------|---------|------|
| <img width="412" height="878" alt="Screenshot 2026-10-11 215603" src="https://github.com/user-attachments/assets/964ae8e3-3af2-40e2-9c0d-b874dfce15c9" />
 | <img width="408" height="880" alt="Screenshot 2026-10-11 215624" src="https://github.com/user-attachments/assets/4cc1ba23-3ff5-46c2-8b69-f52652e8a5f5" />
 | <img width="410" height="879" alt="Screenshot 2026-10-11 215640" src="https://github.com/user-attachments/assets/944dda9f-7522-4bda-9cea-b665bcf86667" />
 | <img width="411" height="879" alt="Screenshot 2026-10-11 215655" src="https://github.com/user-attachments/assets/d9f06229-70c2-491a-b0fb-345354cddac3" />
 | <img width="405" height="878" alt="Screenshot 2026-10-11 215710" src="https://github.com/user-attachments/assets/2a2818a6-7a5a-454f-91c0-37424094fac3" />


> Screenshots from physical device — Dark Theme supported across all screens.

---

## 🛠️ Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Architecture:** MVVM + Three-Tier (App → Firebase → Firestore)
- **Backend:** Firebase Auth (Email/Password), Cloud Firestore (africa-south1)
- **Local:** Room for offline fallback, Firestore offline persistence
- **CI/CD:** GitHub Actions (builds APK on push), Azure Pipelines mirror
- **Min SDK:** 24 (Android 7.0) — Target 34

**Database Collections (10+ records each):**
- `articles` — 12 curated SME articles
- `categories` — 9 business categories
- `users`, `preferences`, `behaviourLogs`, `searchLogs`, `savedArticles`

---

## 🚀 Getting Started

### 1. Clone

```bash
git clone https://github.com/Wasama1904/Recommendation-System-App.git
cd Recommendation-System-App
```

### 2. Firebase Setup

1. Create Firebase project: `Recommendation-System-App`
2. Enable **Authentication > Email/Password**
3. Enable **Firestore Database** in **africa-south1**, Test Mode
4. Download `google-services.json` and place in `app/google-services.json`

The app auto-seeds 12 articles + 9 categories on first launch if Firestore is empty.

### 3. Run

- Open in Android Studio Hedgehog or newer
- Sync Gradle
- Connect Android phone with USB Debugging
- Click **Run**

### 4. Build APK

```bash
./gradlew assembleDebug
# APK at app/build/outputs/apk/debug/app-debug.apk
```

Or get it from **GitHub Actions > Artifacts**.

---

## 📂 Project Structure

```
Recommendation-System-App/
├── app/src/main/java/com/smesouthafrica/app/
│   ├── MainActivity.kt          # Navigation host
│   ├── data/
│   │   ├── Models.kt            # Article, Category, etc.
│   │   └── FirebaseRepo.kt      # Firestore + Auth wrapper
│   ├── engine/
│   │   └── RecommendationEngine.kt
│   ├── ui/
│   │   ├── Screens.kt           # All Compose screens
│   │   └── theme/Theme.kt       # Light/Dark Material 3 themes
│   └── ...
├── database/
│   └── sme_database.sql         # MySQL version of schema
├── docs/
│   ├── SME_User_Manual_Professional.pdf
│   └── SME_User_Manual_Simple.pdf
├── .github/workflows/android.yml
├── azure-pipelines.yml
└── README.md
```

---

## 🔐 Privacy & Security

- Passwords hashed via Firebase Auth
- Firestore rules in Test Mode for prototype (lock down for production)
- No personal data sold — only interests, searches and reading behaviour used for recommendations
- Region: `africa-south1` for low latency in SA

---

## 🤝 Contributing

This is an open portfolio project. Issues and PRs welcome.

1. Fork the repo
2. Create branch: `git checkout -b feature/your-feature`
3. Commit: `git commit -m "Add your feature"`
4. Push: `git push origin feature/your-feature`
5. Open PR

---

## 📄 License

MIT — Free for educational and commercial use. SME South Africa content remains property of SME South Africa.

---

## 📬 Contact

**Developer:** Wasama Makolo (ST10451742) | Tinotenda Marumahoko (ST10457568) | Cameron Johannes (ST10454395)
**Project:** https://github.com/Wasama1904/Recommendation-System-App
**Website:** https://smesouthafrica.co.za

---

SME South Africa — Business made easier.
Built with 💚 in Cape Town, South Africa

