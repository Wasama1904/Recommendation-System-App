# SME South Africa App - Firebase Version

## Setup Steps (3 Quick Steps)

1. **Firebase Console Setup**
   - Go to [console.firebase.google.com](https://console.firebase.google.com)
   - Add Project: **Recommendation-System-App**
   - Disable Google Analytics (optional/not needed)

2. **Add Android App & Download `google-services.json`**
   - In Firebase Console > Project Settings > Add App > Android
   - Package name: `com.smesouthafrica.app`
   - Download `google-services.json`
   - Place `google-services.json` inside the `app/` folder (`C:/Users/makol/AndroidStudioProjects/RecommendationSystem/app/google-services.json`)

3. **Enable Firestore Database**
   - In Firebase Console: Build > Firestore Database > Create Database
   - Choose location (e.g. `eur3` or `us-central`)
   - Start in **Test Mode** (allows read/write)

4. **Run App**
   - Run the app on device/emulator.
   - On first launch, the app automatically seeds 12 articles and 9 categories in Firestore.
