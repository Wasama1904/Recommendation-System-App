# SME South Africa - Personalised Business Feed App
Task 2 - XISD6329

This is the mobile version of the recommendation system described in the report.
Architecture: Mobile App -> REST API (concept) -> MySQL (Room) + MongoDB (Behaviour Logs)

## Features implemented (per assignment checklist)
- Android Studio project with Kotlin + Jetpack Compose
- Runs on physical phone
- 8 core screens: Splash, Login, Register, Interest Selection, Home Feed, Article Detail, Search/Explore, Saved, Profile
- Database with 10+ records (12 articles, 9 categories)
- Registration/Login
- Recommendation engine with weighted scoring
- Behaviour tracking: CLICK, READ, SAVE, LIKE, SEARCH, CATEGORY_SELECTED
- GitHub + GitHub Actions + Azure DevOps

## Recommendation logic (from report)
Score = Interest Match (+5) + Search Match (+4) + Reading History (+3) + Long Read (+4) + Same Category (+2) + Saved (+5) + Liked (+5)

## How to run
1. Open in Android Studio Hedgehog+
2. Let Gradle sync
3. Run on device
4. Flow to demo: Register -> Select Funding+Technology -> Home shows those -> Search "funding" -> Read 2 funding articles -> Back to Home -> Funding articles now on top with higher scores

## GitHub
git remote add origin https://github.com/Wasama1904/Recommendation-System-App.git
git push -u origin main

## Azure DevOps
git remote add azure https://Recommendation-System-App@dev.azure.com/Recommendation-System-App/SME-Recommendation%20System%20App/_git/SME-Recommendation%20System%20App
git push azure main

Include AB# task links in commits.

## GitHub Actions already included
.github/workflows/android.yml will build on every push.

## Video demo script
1. Show Splash -> Login
2. Show Register -> Interest selection (explain this is Preferences table)
3. Show Home - Recommended for You with scores
4. Open article - show READ log inserted
5. Search "funding" - show SearchLogs
6. Return to Home - show funding articles jumped to top - PROVES recommendation engine works
7. Show Saved + Profile
8. Show Room database inspector: App Inspection > Database Inspector - show 12 articles
