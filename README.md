# TravelMeet

> **Bar Abramovich 323098889 · Idan Tepper 212929103**

A social Android app where travelers discover, share, and explore travel spots around the world. Built with Kotlin, Firebase, Google Maps, and real-time cloud sync.

![Kotlin](https://img.shields.io/badge/Kotlin-1.8.0-7F52FF?logo=kotlin&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?logo=firebase&logoColor=black)
![Google Maps](https://img.shields.io/badge/Google%20Maps-4285F4?logo=googlemaps&logoColor=white)
![MVVM](https://img.shields.io/badge/Architecture-MVVM-green)
![Min SDK](https://img.shields.io/badge/Min%20SDK-24%20(Android%207.0)-blue)

---

> **📸 Screenshots:** Add your screen captures to a `screenshots/` folder in the project root and the images below will appear automatically on GitHub.

---

## Table of Contents

1. [App Overview](#app-overview)
2. [Authentication](#authentication)
3. [Feed](#feed)
4. [Create & Edit a Spot](#create--edit-a-spot)
5. [Spot Detail](#spot-detail)
6. [Interactive Map](#interactive-map)
7. [Comments](#comments)
8. [Likes & Saves](#likes--saves)
9. [Filter & Sort](#filter--sort)
10. [User Profile](#user-profile)
11. [My Spots & Saved Spots](#my-spots--saved-spots)
12. [Dark Mode](#dark-mode)
13. [Weather](#weather)
14. [Tech Stack](#tech-stack)
15. [Architecture](#architecture)
16. [Getting Started](#getting-started)
17. [Project Structure](#project-structure)
18. [Permissions & APIs](#permissions--apis)

---

## App Overview

TravelMeet lets authenticated users post travel spots with photos, a title, description, and a pinned location. Other users can discover posts on a scrollable feed or on a live map, interact via likes, comments, and bookmarks, and explore weather conditions at each spot — all in real time.

---

## Authentication

Users must sign in before accessing the app. All data is scoped to the authenticated user.

| Screen | What you can do |
|--------|----------------|
| **Login** | Enter email and password to sign in |
| **Register** | Create a new account with email, password, and username |

**Validations applied:**
- Email format check (regex)
- Password minimum 6 characters
- Password confirmation must match on register
- Username is required

**Behind the scenes:** Firebase Authentication handles credentials. On success, user data (username, email, photo URL) is stored in Firebase Realtime Database and cached locally in Room.

![Login Screen](screenshots/login.png)
![Register Screen](screenshots/register.png)

---

## Feed

The Feed is the home screen — a vertically scrolling list of all spots posted by every user.

**What you can do:**
- Scroll through all travel spots
- Swipe left/right on a spot card to browse multiple photos (ViewPager2 carousel with dot indicators)
- See the poster's avatar, username, and relative timestamp ("2 hours ago")
- Read the spot title, short description, and location
- **Like** a spot directly from the card
- **Comment** on a spot directly from the card (opens a dialog)
- **Save/bookmark** a spot to your Saved Spots list
- **Pull down to refresh** the feed manually
- **Sort and filter** spots using the menu icon (top right)
- Tap a spot card to open its full detail view

![Feed Screen](screenshots/feed.png)
![Feed with Image Carousel](screenshots/feed_carousel.png)

---

## Create & Edit a Spot

Tap the **＋ Add** button in the bottom navigation to open the spot creation form.

**What you can do:**

**Images**
- Select up to multiple photos from your gallery
- Take a new photo with the camera
- Preview selected images in a scrollable horizontal list
- Remove individual images before posting
- GIFs are fully supported and detected automatically

**Location**
- Tap **Get My Location** to auto-fetch GPS coordinates and reverse-geocode to an address
- Or use the **search bar** with Google Places autocomplete to find any location worldwide
- The location name and coordinates are saved with the spot

**Details**
- Enter a spot **Title** (required)
- Enter a spot **Description** (required)

**Submit**
- Tap **Post Spot** — images are compressed and uploaded to Firebase Storage, then the spot record is saved to Firebase Realtime Database
- A Lottie loading animation is shown during upload
- The button is disabled while uploading to prevent double-posts

**Editing an existing spot:**
From Spot Detail or My Spots, tap the **Edit** button (owner only). The same form pre-fills with existing data — change any field and tap **Update Spot**.

![Add Spot Screen](screenshots/add_spot.png)
![Image Selection](screenshots/add_spot_images.png)
![Location Search](screenshots/add_spot_location.png)

---

## Spot Detail

Tap any spot card in the Feed, Map, My Spots, or Saved Spots to open the full detail view.

**What you see:**
- Full-screen image carousel (swipeable, with dot indicators and image count e.g. "2 / 5")
- Spot title and full description
- Creator's avatar and username
- Exact location name or coordinates
- **Google Map preview** pinned to the spot's coordinates (non-interactive inset)
- **Live weather** at the spot's location (see [Weather](#weather))
- **Comments** tab — read all comments and add your own
- **Like** and **Save** buttons

**Owner-only actions:**
- **Edit** — opens the Add Spot form pre-filled with this spot's data
- **Delete** — shows a confirmation dialog, then deletes the spot and all its images from Firebase Storage

![Spot Detail](screenshots/spot_detail.png)
![Spot Detail Map + Weather](screenshots/spot_detail_map_weather.png)

---

## Interactive Map

Tap **Map** in the bottom navigation to see every spot plotted on a Google Map.

**What you can do:**
- Pan and zoom across the map freely
- See **color-coded markers**:
  - 🔵 Blue markers = your own spots
  - 🩷 Pink markers = other users' spots
- Tap a marker to open an **info window** showing the spot title and creator name
- Tap the info window to navigate to the full Spot Detail screen
- Tap the **My Location FAB** (bottom right) to animate the camera to your current GPS position
- On load, the camera automatically fits all spots in view

![Map View](screenshots/map.png)
![Map Marker Info Window](screenshots/map_marker.png)

---

## Comments

**From the Feed card:**
- Tap the **comment icon** on any spot card → a modal dialog opens
- Type a comment (minimum 2 characters) and tap **Post**
- Comments appear in real time — a Firestore listener pushes updates instantly

**From Spot Detail:**
- A dedicated **Comments** section shows the full comment thread
- Each comment shows: profile avatar, username, comment text, relative timestamp
- Comments are sorted newest first

![Add Comment Dialog](screenshots/comment_dialog.png)
![Comments in Detail](screenshots/comments_list.png)

---

## Likes & Saves

### Likes
- Tap the **heart icon** on any spot (Feed card or Spot Detail) to like or unlike
- The like count updates immediately and syncs across all devices in real time
- A filled heart = you liked it; outline = not liked
- Tracked per user — each user can like a spot once

### Saves / Bookmarks
- Tap the **bookmark icon** on any spot card to save or unsave
- Saved spots are accessible from your profile under **Saved Spots**
- A filled bookmark = saved; outline = not saved
- Saved count is shown on the Profile screen

![Like and Save Icons](screenshots/like_save.png)

---

## Filter & Sort

Tap the **filter icon** (top-right of the Feed) to open sort and filter options.

### Sort Options
| Option | Description |
|--------|------------|
| Newest First | Most recently posted spots appear at the top (default) |
| Oldest First | Oldest spots appear at the top |
| Most Liked | Spots with the highest like count appear first |
| Least Liked | Spots with the lowest like count appear first |

### Text Search
- Type in the search bar to filter spots by **title**, **description**, or **location name** in real time

### Location Filter
- Enter a **location name** (with Google Places autocomplete suggestions)
- Enter a **distance radius** (numeric value)
- Choose a **unit**: kilometers or meters
- Tap **Apply Filter** → only spots within that radius of the entered location are shown
- Distance is calculated using the **Haversine formula** for accurate great-circle distance

### Clear & Reset
- Tap **Clear** to remove all active filters and show all spots
- A confirmation dialog appears if you switch sort mode while a location filter is active

![Sort Sheet](screenshots/sort_sheet.png)
![Filter Sheet](screenshots/filter_sheet.png)

---

## User Profile

Tap **Profile** in the bottom navigation.

**What you see:**
- Profile photo (circular)
- Username and email
- Stats: number of spots posted, spots saved, spots liked
- Navigation buttons to **My Spots**, **Saved Spots**, and **Settings**
- **Logout** button

**What you can do:**
- Tap the profile photo to **change your avatar** — pick from gallery or take a new photo
- Tap the **edit icon** next to your name to open the Edit Profile dialog and change your username
- Changes sync to Firebase and update immediately everywhere your name appears

![Profile Screen](screenshots/profile.png)
![Edit Profile Dialog](screenshots/edit_profile.png)

---

## My Spots & Saved Spots

### My Spots
Accessible from the Profile screen. Shows a list of all spots you have posted.

**What you can do:**
- Scroll through all your spots
- Tap a spot to open its detail view
- Edit or delete spots you own
- Pull down to refresh

### Saved Spots
Accessible from the Profile screen. Shows all spots you have bookmarked.

**What you can do:**
- Scroll through saved spots
- Tap a spot to open its detail view
- Tap the bookmark icon again to remove a spot from your saved list

![My Spots Screen](screenshots/my_spots.png)
![Saved Spots Screen](screenshots/saved_spots.png)

---

## Dark Mode

Accessible from **Profile → Settings**.

- Toggle the **Dark Mode** switch to switch between light and dark theme
- The preference is saved in SharedPreferences and persists across app restarts
- Uses `AppCompatDelegate.setDefaultNightMode()` for system-wide theme switching

![Settings Light](screenshots/settings_light.png)
![Settings Dark](screenshots/settings_dark.png)

---

## Weather

Shown automatically on the **Spot Detail** screen for any spot that has valid coordinates.

**Data displayed:**
| Field | Example |
|-------|---------|
| Temperature | 24°C |
| Condition | Partly Cloudy |
| Humidity | 62% |
| Wind Speed | 4.2 m/s |

**How it works:**
- Coordinates from the spot are sent to the **Open-Meteo API** (free, no API key required)
- Response is parsed via Retrofit + Gson
- A Lottie spinner shows while fetching
- If the API call fails or coordinates are missing, all fields gracefully display "N/A"

![Weather Widget](screenshots/weather.png)

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin 1.8.0 |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 33 (Android 13) |
| Architecture | MVVM + Repository Pattern |
| UI | Material Design 3, ViewBinding, Navigation Component |
| Local DB | Room 2.5.0 |
| Remote DB | Firebase Realtime Database |
| Auth | Firebase Authentication (Email/Password) |
| Storage | Firebase Storage |
| Maps | Google Maps SDK 18.1.0 |
| Location Search | Google Places API 3.4.0 |
| Location | Google Play Services Location 21.0.1 |
| Networking | Retrofit 2.9.0 + Gson |
| Image Loading | Glide 4.15.1 (carousels + GIFs), Picasso 2.8 (avatars) |
| Animations | Lottie 5.2.0 |
| Async | Kotlin Coroutines 1.6.4 |
| Weather API | Open-Meteo (free, no key required) |

---

## Architecture

TravelMeet follows the **MVVM + Repository** pattern recommended by Google:

```
┌──────────────────────────────────────────────────────┐
│                      UI Layer                         │
│   Fragments  ──►  ViewModels  ──►  LiveData           │
│                                                       │
│   LoginFragment        AuthViewModel                  │
│   RegisterFragment     SpotViewModel                  │
│   FeedFragment         WeatherViewModel               │
│   AddSpotFragment      SettingsViewModel              │
│   SpotDetailFragment                                  │
│   MapFragment                                         │
│   ProfileFragment                                     │
│   MySpotsFragment                                     │
│   SavedSpotsFragment                                  │
│   SettingsFragment                                    │
├──────────────────────────────────────────────────────┤
│                  Repository Layer                     │
│   AuthRepository  ·  SpotRepository  ·  WeatherRepo  │
├──────────────────────────────────────────────────────┤
│                    Data Layer                         │
│  Room (local cache)      │  Firebase (remote)         │
│  SpotDao · UserDao       │  Realtime DB · Storage     │
│                          │  Auth                      │
│                          │  Retrofit → Open-Meteo     │
└──────────────────────────────────────────────────────┘
```

**Data flow:** Fragments observe LiveData from ViewModels. ViewModels call Repository methods. Repositories sync data between the local Room cache and Firebase. Realtime Database listeners push updates to Room, which triggers LiveData updates that automatically refresh the UI.

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/barab2002/TravelMeet.git
cd TravelMeet
```

### 2. Set up Firebase

1. Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
2. Add an Android app with package name: `com.travelmeet.app`
3. Download `google-services.json` and place it in the `app/` directory:
   ```
   TravelMeet/
   └── app/
       └── google-services.json
   ```
4. Enable the following in the Firebase Console:
   - **Authentication** → Sign-in method → Email/Password
   - **Realtime Database** → Create database (test mode or use rules below)
   - **Storage** → Get started (test mode or use rules below)

#### Realtime Database Rules

```json
{
  "rules": {
    ".read": "auth != null",
    ".write": "auth != null"
  }
}
```

#### Storage Security Rules

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

1. Open [Google Cloud Console](https://console.cloud.google.com/).
2. Enable:
   - **Maps SDK for Android**
   - **Places API**
   - **Geocoding API**
3. Create an API key under **APIs & Services → Credentials**.

### 4. Configure local.properties

Add your API key to `local.properties` in the project root:

```properties
sdk.dir=C\:\\Users\\YOUR_USERNAME\\AppData\\Local\\Android\\Sdk
MAPS_API_KEY=YOUR_GOOGLE_MAPS_API_KEY_HERE
```

> `local.properties` is gitignored — never commit it.

### 5. Build and Run

Open the project in **Android Studio** and click **Run**, or:

```bash
# Windows
gradlew.bat assembleDebug

# macOS / Linux
./gradlew assembleDebug
```

---

## Project Structure

```
app/src/main/
├── java/com/travelmeet/app/
│   ├── TravelMeetApp.kt                  # Application class (Places SDK init)
│   ├── data/
│   │   ├── local/
│   │   │   ├── AppDatabase.kt            # Room database
│   │   │   ├── Converters.kt             # List<String> type converter
│   │   │   ├── SpotDao.kt                # Spot queries
│   │   │   ├── UserDao.kt                # User queries
│   │   │   └── entity/
│   │   │       ├── SpotEntity.kt         # Spot data model
│   │   │       └── UserEntity.kt         # User data model
│   │   ├── remote/
│   │   │   ├── WeatherApiService.kt      # Retrofit interface (Open-Meteo)
│   │   │   └── model/
│   │   │       └── WeatherResponse.kt    # Weather response model
│   │   └── repository/
│   │       ├── AuthRepository.kt         # Auth + profile management
│   │       ├── SpotRepository.kt         # Spot CRUD + real-time sync
│   │       └── WeatherRepository.kt      # Weather fetching
│   ├── ui/
│   │   ├── MainActivity.kt               # Single-activity host + bottom nav
│   │   ├── auth/
│   │   │   ├── LoginFragment.kt
│   │   │   └── RegisterFragment.kt
│   │   ├── feed/
│   │   │   ├── FeedFragment.kt           # Main feed with pull-to-refresh
│   │   │   ├── SpotAdapter.kt            # RecyclerView adapter (DiffUtil)
│   │   │   └── ImageSliderAdapter.kt     # ViewPager2 image carousel
│   │   ├── addspot/
│   │   │   └── AddSpotFragment.kt        # Create / edit spot form
│   │   ├── detail/
│   │   │   └── SpotDetailFragment.kt     # Full detail + weather + map
│   │   ├── map/
│   │   │   └── MapFragment.kt            # Google Maps with markers
│   │   ├── profile/
│   │   │   ├── ProfileFragment.kt        # Profile screen
│   │   │   ├── MySpotsFragment.kt        # User's own spots
│   │   │   └── SavedSpotsFragment.kt     # Bookmarked spots
│   │   ├── settings/
│   │   │   └── SettingsFragment.kt       # Dark mode toggle
│   │   └── viewmodel/
│   │       ├── AuthViewModel.kt
│   │       ├── SpotViewModel.kt
│   │       └── WeatherViewModel.kt
│   └── util/
│       ├── Constants.kt                  # App-wide constants
│       ├── Resource.kt                   # Sealed class for async state
│       └── TimeUtils.kt                  # Relative time formatting
├── res/
│   ├── navigation/nav_graph.xml          # Navigation graph
│   ├── layout/                           # All XML layouts
│   ├── drawable/                         # Vector icons and shapes
│   ├── menu/                             # Bottom nav and feed menu
│   ├── raw/                              # Lottie JSON animations
│   └── values/                           # Colors, strings, dimens, themes
└── AndroidManifest.xml
```

---

## Permissions & APIs

### App Permissions

| Permission | Reason |
|-----------|--------|
| `INTERNET` | Firebase, Maps, Weather API |
| `ACCESS_NETWORK_STATE` | Detect offline state |
| `ACCESS_FINE_LOCATION` | GPS for tagging spot locations |
| `ACCESS_COARSE_LOCATION` | Approximate location fallback |
| `CAMERA` | Take photos for spots |
| `READ_MEDIA_IMAGES` | Gallery access (Android 13+) |
| `READ_EXTERNAL_STORAGE` | Gallery access (Android 12 and below) |

### External APIs

| API | Key Required | Usage |
|-----|-------------|-------|
| Google Maps SDK | Yes (API key) | Map display, markers |
| Google Places API | Yes (API key) | Location autocomplete |
| Geocoding API | Yes (API key) | Address ↔ coordinates |
| Open-Meteo | No (free) | Live weather per spot |
| Firebase Auth | google-services.json | User sign-in |
| Firebase Realtime DB | google-services.json | Spot & user data |
| Firebase Storage | google-services.json | Image hosting |

---

## Troubleshooting

| Problem | Solution |
|---------|---------|
| Build fails: `MAPS_API_KEY not found` | Add `MAPS_API_KEY=your_key` to `local.properties` |
| Map shows grey tiles | Verify Maps SDK for Android is enabled in Google Cloud Console |
| Places autocomplete returns nothing | Enable Places API and Geocoding API; check billing is on |
| Firebase auth fails | Confirm `google-services.json` is in `app/` and Email/Password is enabled |
| Images fail to upload | Check Firebase Storage rules allow authenticated writes |
| Weather shows "N/A" | Check internet connection; spot must have valid coordinates |
| SHA-1 for API restrictions | Run `gradlew signingReport` and copy the SHA1 under `Variant: debug` |

---

*Project submitted for academic purposes — Bar Abramovich & Idan Tepper*
