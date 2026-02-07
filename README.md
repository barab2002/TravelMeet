# TravelMeet - Bar Abramovich 323098889 & Idan Tepper 

A modern Android social travel app where users discover, share, and explore travel spots around the world. Built with Kotlin, Firebase, and Google Maps.

## Features

- **Authentication** - Email/password sign-up and login via Firebase Auth
- **Spot Feed** - Scroll through travel spots with swipeable multi-image carousels and GIF support
- **Add Spots** - Upload spots with multiple photos/GIFs, titles, descriptions, and location
- **Google Places Autocomplete** - Search and pick locations with smart autocomplete suggestions
- **Interactive Map** - Browse all spots on a Google Maps view with markers
- **Spot Details** - Full detail view with hero image carousel, map preview, and live weather data
- **Like System** - Real-time like/unlike with counters synced across devices
- **Weather Info** - Live weather conditions for each spot location (via Open-Meteo)
- **User Profiles** - Profile management with avatar, username, and "My Spots" section
- **Offline Support** - Local Room database cache with real-time Firestore sync
- **Dark/Light Mode** - Toggle between themes in settings (persisted across sessions)
- **Lottie Animations** - Smooth loading animations throughout the app

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin 1.8.0 |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 33 (Android 13) |
| Architecture | MVVM + Repository Pattern |
| UI | Material Design Components, ViewBinding, Navigation Component |
| Local DB | Room 2.5.0 |
| Remote DB | Cloud Firestore |
| Auth | Firebase Authentication |
| Storage | Firebase Storage |
| Maps | Google Maps SDK 18.1.0, Google Places 3.4.0 |
| Location | Google Play Services Location 21.0.1 |
| Networking | Retrofit 2.9.0 + Gson |
| Images | Glide 4.15.1 (carousel + GIFs), Picasso 2.8 (avatars) |
| Animations | Lottie 5.2.0 |
| Async | Kotlin Coroutines 1.6.4 |
| Weather API | Open-Meteo (free, no key required) |

## Prerequisites

Before you begin, make sure you have:

- **Android Studio** (Arctic Fox or later recommended)
- **JDK 11** (bundled with Android Studio)
- **Android SDK 33** installed via SDK Manager
- A **Google Cloud** account with billing enabled
- A **Firebase** project

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/barab2002/TravelMeet.git
cd TravelMeet
```

### 2. Set up Firebase

1. Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project (or use an existing one).
2. Add an Android app with package name: `com.travelmeet.app`
3. Download the generated `google-services.json` file.
4. Place it in the `app/` directory:
   ```
   TravelMeet/
   └── app/
       └── google-services.json   <-- place here
   ```
5. In the Firebase Console, enable the following services:
   - **Authentication** > Sign-in method > Enable **Email/Password**
   - **Cloud Firestore** > Create database (start in test mode or configure rules below)
   - **Storage** > Get started (start in test mode or configure rules below)

#### Firestore Security Rules (recommended)

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users collection
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
    // Spots collection
    match /spots/{spotId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow update, delete: if request.auth != null
        && resource.data.userId == request.auth.uid;
    }
  }
}
```

#### Storage Security Rules (recommended)

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /profile_images/{userId}/{allPaths=**} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
    match /spot_images/{spotId}/{allPaths=**} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }
  }
}
```

### 3. Set up Google Maps & Places API

1. Go to the [Google Cloud Console](https://console.cloud.google.com/).
2. Select the same project linked to your Firebase project.
3. Go to **APIs & Services** > **Library** and enable:
   - **Maps SDK for Android**
   - **Places API**
4. Go to **APIs & Services** > **Credentials** and create an **API key**.
5. (Recommended) Restrict the key:
   - **Application restrictions**: Android apps
   - **API restrictions**: Maps SDK for Android, Places API
   - Add your app's SHA-1 fingerprint and package name `com.travelmeet.app`

### 4. Configure local.properties

Open (or create) `local.properties` in the project root and add your API key:

```properties
sdk.dir=C\:\\Users\\YOUR_USERNAME\\AppData\\Local\\Android\\Sdk
MAPS_API_KEY=YOUR_GOOGLE_MAPS_API_KEY_HERE
```

> **Important:** `local.properties` is gitignored and should never be committed. Each developer needs their own copy.

The key is injected into the app via `BuildConfig.MAPS_API_KEY` and the AndroidManifest `${MAPS_API_KEY}` placeholder.

### 5. Build and Run

```bash
# Build debug APK
./gradlew assembleDebug

# Or open the project in Android Studio and click Run
```

> On Windows, use `gradlew.bat assembleDebug` instead.

## Project Structure

```
app/src/main/
├── java/com/travelmeet/app/
│   ├── TravelMeetApp.kt                 # Application class (Places SDK init)
│   ├── data/
│   │   ├── local/
│   │   │   ├── AppDatabase.kt           # Room database (v2)
│   │   │   ├── Converters.kt            # Type converters for Room
│   │   │   ├── SpotDao.kt               # Spot data access object
│   │   │   ├── UserDao.kt               # User data access object
│   │   │   └── entity/
│   │   │       ├── SpotEntity.kt         # Spot data model
│   │   │       └── UserEntity.kt         # User data model
│   │   ├── remote/
│   │   │   ├── WeatherApiService.kt      # Retrofit API interface
│   │   │   └── model/
│   │   │       └── WeatherResponse.kt    # Weather API response model
│   │   └── repository/
│   │       ├── AuthRepository.kt         # Auth + user profile management
│   │       ├── SpotRepository.kt         # Spot CRUD + real-time sync
│   │       └── WeatherRepository.kt      # Weather data fetching
│   ├── ui/
│   │   ├── MainActivity.kt              # Single activity host
│   │   ├── auth/
│   │   │   ├── LoginFragment.kt
│   │   │   └── RegisterFragment.kt
│   │   ├── feed/
│   │   │   ├── FeedFragment.kt           # Main feed with pull-to-refresh
│   │   │   ├── SpotAdapter.kt            # RecyclerView adapter for spots
│   │   │   └── ImageSliderAdapter.kt     # ViewPager2 image carousel adapter
│   │   ├── addspot/
│   │   │   └── AddSpotFragment.kt        # Add/edit spot form
│   │   ├── detail/
│   │   │   └── SpotDetailFragment.kt     # Spot detail with weather + map
│   │   ├── map/
│   │   │   └── MapFragment.kt            # Google Maps with markers
│   │   ├── profile/
│   │   │   ├── ProfileFragment.kt        # User profile screen
│   │   │   └── MySpotsFragment.kt        # User's spots list
│   │   └── viewmodel/
│   │       ├── AuthViewModel.kt
│   │       ├── SpotViewModel.kt
│   │       └── WeatherViewModel.kt
│   └── util/
│       ├── Constants.kt                  # App-wide constants
│       ├── Resource.kt                   # Sealed class for state management
│       └── TimeUtils.kt                  # Relative time formatting
├── res/
│   ├── navigation/nav_graph.xml          # Navigation graph
│   ├── layout/                           # 14 layout files
│   ├── drawable/                         # Icons and shapes
│   ├── menu/bottom_nav_menu.xml          # Bottom navigation menu
│   ├── raw/loading_animation.json        # Lottie animation
│   ├── xml/file_paths.xml                # FileProvider config (camera)
│   └── values/                           # Colors, strings, dimens, themes
└── AndroidManifest.xml
```

## Architecture

```
┌─────────────────────────────────────────────────┐
│                    UI Layer                      │
│  Fragments ──── ViewModels ──── LiveData/State   │
├─────────────────────────────────────────────────┤
│                Repository Layer                  │
│  AuthRepository  SpotRepository  WeatherRepo     │
├─────────────────────────────────────────────────┤
│                 Data Layer                        │
│  Room DB (local)  │  Firestore (remote)          │
│  SpotDao, UserDao │  Firebase Auth & Storage     │
│                   │  Retrofit (Weather API)       │
└─────────────────────────────────────────────────┘
```

**Data flow:** UI observes LiveData from ViewModels. ViewModels call Repository methods. Repositories coordinate between local Room cache and remote Firebase/API services. Firestore snapshot listeners push real-time updates to the local database, which automatically updates the UI through LiveData.

## Configuration Reference

| File | Purpose | Gitignored? |
|------|---------|-------------|
| `local.properties` | SDK path + `MAPS_API_KEY` | Yes |
| `app/google-services.json` | Firebase configuration | Yes |
| `gradle.properties` | AndroidX flags + key placeholder | No |
| `app/proguard-rules.pro` | ProGuard rules for release builds | No |

## Permissions

| Permission | Purpose |
|-----------|---------|
| `INTERNET` | Network access for Firebase, Maps, Weather API |
| `ACCESS_NETWORK_STATE` | Check connectivity for offline mode |
| `ACCESS_FINE_LOCATION` | GPS location for tagging spots |
| `ACCESS_COARSE_LOCATION` | Approximate location fallback |
| `CAMERA` | Take photos for spots |
| `READ_MEDIA_IMAGES` | Pick images from gallery (Android 13+) |
| `READ_EXTERNAL_STORAGE` | Pick images from gallery (Android 12 and below) |

## External APIs

| API | Auth Required | Usage |
|-----|--------------|-------|
| Google Maps SDK | API Key | Map display and markers |
| Google Places API | API Key | Location autocomplete search |
| Open-Meteo | None (free) | Weather data for spot locations |
| Firebase Auth | google-services.json | User authentication |
| Cloud Firestore | google-services.json | Spot and user data storage |
| Firebase Storage | google-services.json | Image uploads and hosting |

## Troubleshooting

**Build fails with "MAPS_API_KEY not found"**
- Make sure `local.properties` exists in the project root and contains `MAPS_API_KEY=your_key_here`

**Map shows grey tiles**
- Verify your API key has Maps SDK for Android enabled in Google Cloud Console
- Check that the key isn't restricted to a different SHA-1 fingerprint

**Places autocomplete doesn't return results**
- Ensure Places API is enabled in Google Cloud Console for your API key
- Check that billing is enabled on your Google Cloud project

**Firebase authentication fails**
- Verify `google-services.json` is in the `app/` directory
- Check that Email/Password sign-in is enabled in Firebase Console

**Images fail to upload**
- Check Firebase Storage rules allow writes for authenticated users
- Verify your Firebase project has Storage enabled

**Weather shows "N/A"**
- Open-Meteo is a free API with no key needed - check your internet connection
- The API requires valid latitude/longitude coordinates

## Getting Your SHA-1 Fingerprint

For API key restrictions, you'll need your debug SHA-1:

```bash
# macOS/Linux
./gradlew signingReport

# Windows
gradlew.bat signingReport
```

Look for the `SHA1` value under `Variant: debug`.

## License

This project is for educational and personal use.
