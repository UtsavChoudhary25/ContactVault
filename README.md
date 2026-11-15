# ContactVault - MAD Lab Experiment 10

## 📱 Experiment Overview

- **Experiment Number:** 10
- **Title:** Implement Data Sharing Using Contacts Content Provider
- **Application Name:** ContactVault
- **Package Name:** `com.utsav.contactvault`
- **Course:** Master of Computer Applications (MCA)
- **Student Name:** Utsav Choudhary
- **USN:** 1RV23MC001
- **Technology Stack:** Kotlin 2.0, Jetpack Compose, Material 3, Android Contacts ContentProvider, ContentResolver, ContactsContract API

---

## 🎯 Aim

To design, develop, and test a complete, production-quality Android application using **Kotlin** and **Jetpack Compose** that demonstrates accessing device contacts using Android's **Contacts Content Provider** (`ContentResolver` & `ContactsContract`), handling runtime permissions (`READ_CONTACTS`), and sharing contact details with other applications using Android's native **Sharesheet (`ACTION_SEND`)**.

---

## 📖 Introduction

In Android development, **Content Providers** serve as a standard interface for sharing data between different applications while maintaining system security. The system **Contacts Provider** is a multi-table database managed by Android that stores information such as contact names, phone numbers, email addresses, and photos.

**ContactVault** is a modern Android application built following Google's recommended architecture (MVVM + StateFlow + Material 3) that securely accesses device contacts, aggregates multiple phone numbers per contact, presents them in a clean dashboard UI, and provides seamless contact sharing via Android Sharesheet.

---

## 🔬 Technical Theory

### 1. Android Content Providers
A **ContentProvider** abstracts data storage (SQLite database, files, or network) and exposes data to client applications using standard `Uri` endpoints (`content://...`). Key methods exposed by a ContentProvider include:
- `query()`: Retrieves data (similar to SQL `SELECT`).
- `insert()`: Adds new records.
- `update()`: Modifies existing records.
- `delete()`: Removes records.

### 2. ContentResolver & ContactsContract API
- **ContentResolver:** Acts as a client proxy within an app to query external ContentProviders.
- **ContactsContract:** The contract class defining table schemas and columns for contacts data.
- **Key URI used:**
  ```kotlin
  ContactsContract.CommonDataKinds.Phone.CONTENT_URI
  ```
- **Query Projection:**
  - `ContactsContract.CommonDataKinds.Phone.CONTACT_ID`
  - `ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY`
  - `ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY`
  - `ContactsContract.CommonDataKinds.Phone.NUMBER`
  - `ContactsContract.CommonDataKinds.Phone.TYPE`
  - `ContactsContract.CommonDataKinds.Phone.LABEL`
  - `ContactsContract.CommonDataKinds.Phone.PHOTO_URI`

---

## 🚀 Key Application Features

1. **🔒 Runtime Permission Management:**
   - Explains why `READ_CONTACTS` is required before requesting.
   - Handles `Granted`, `Denied`, and `Permanently Denied` states with direct button redirect to Application Settings.

2. **📞 Contacts Retrieval & Multi-Number Aggregation:**
   - Asynchronously queries `ContactsContract.CommonDataKinds.Phone.CONTENT_URI` on `Dispatchers.IO` using Kotlin Coroutines.
   - Groups multiple phone numbers (Mobile, Home, Work) under a single contact record.
   - Safe `Cursor` resource management (`use { cursor -> ... }`).

3. **📊 Contacts Dashboard:**
   - Header displaying total contact count badge (`Filtered / Total`).
   - Live Search bar filtering by display name or phone number.
   - Deterministic color-coded circular initial avatars.
   - Alphabetical contact sorting.
   - Pull-to-refresh / manual refresh button.

4. **👤 Contact Details Screen:**
   - Full Name, Avatar, and category-tagged phone numbers list.
   - **Quick Action Call:** Triggers `ACTION_DIAL` intent with `tel:<number>`.
   - **Quick Action SMS:** Triggers `ACTION_SENDTO` with `smsto:<number>`.
   - **ContentProvider Metadata:** Displays `Contact ID` and `Lookup Key` for educational verification.

5. **📤 Data Sharing via Android Sharesheet:**
   - Uses `Intent.ACTION_SEND` with `text/plain` MIME type.
   - Formats contact details cleanly and invokes `Intent.createChooser()`.

6. **🎓 About Experiment Screen:**
   - Displays student name, USN, experiment details, course info, and interactive technical theory.

---

## 🏗️ Project Architecture & Directory Structure

```
ContactVault/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── java/com/utsav/contactvault/
│           ├── MainActivity.kt
│           ├── data/
│           │   ├── model/
│           │   │   └── Contact.kt
│           │   └── repository/
│           │       └── ContactsRepository.kt
│           ├── navigation/
│           │   └── NavGraph.kt
│           ├── ui/
│           │   ├── components/
│           │   │   ├── ContactAvatar.kt
│           │   │   ├── ContactCard.kt
│           │   │   ├── EmptyState.kt
│           │   │   └── SearchBar.kt
│           │   ├── screens/
│           │   │   ├── AboutScreen.kt
│           │   │   ├── DashboardScreen.kt
│           │   │   ├── DetailScreen.kt
│           │   │   └── PermissionScreen.kt
│           │   ├── theme/
│           │   │   ├── Color.kt
│           │   │   ├── Theme.kt
│           │   │   └── Type.kt
│           │   └── viewmodel/
│           │       ├── ContactsUiState.kt
│           │       └── ContactsViewModel.kt
│           └── util/
│               ├── PermissionUtils.kt
│               └── ShareUtils.kt
├── screenshots/
│   └── README.md
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 🧪 Test Cases & Expected Results

| Test Case | Scenario Description | Action | Expected Output | Status |
| :--- | :--- | :--- | :--- | :--- |
| **TC-01** | First Launch / Permission Request | Open app for first time | App displays Permission Rationale screen asking for `READ_CONTACTS`. | PASS |
| **TC-02** | Permission Granted | Tap "Grant Contacts Permission" | Device contacts are queried via `ContentResolver` and listed in Dashboard. | PASS |
| **TC-03** | Live Contact Search | Enter contact name in SearchBar | List live filters contacts matching entered text or phone number. | PASS |
| **TC-04** | Contact Selection & Details | Tap a contact card in Dashboard | Navigates to Contact Details screen displaying full name, all numbers, and IDs. | PASS |
| **TC-05** | Share Contact Data | Tap "Share" button on Contact Details | Launches Android Sharesheet (`ACTION_SEND`) with formatted contact text. | PASS |
| **TC-06** | Call Contact Action | Tap "Call" button | Launches device Phone Dialer (`ACTION_DIAL`) with pre-filled phone number. | PASS |
| **TC-07** | Permission Denied State | Deny runtime permission | App presents Denied state with "Try Again" and "Open Application Settings" buttons. | PASS |

---

## 📸 Application Screenshots

Captured screenshots from the running application should be placed in the `./screenshots/` folder:

1. `01_permission_request.png` - Permission Request Screen
2. `02_permission_denied.png` - Permission Denied Screen
3. `03_home_dashboard.png` - Home Dashboard Screen
4. `04_contact_details.png` - Contact Details Screen
5. `05_contact_sharing.png` - Android Sharesheet Screen
6. `06_student_identity.png` - About Experiment & Student Identity Screen

---

## 🛠️ How to Build and Run

1. Open project in **Android Studio (Ladybug / Jellyfish or newer)**.
2. Select target device / emulator running **Android 7.0 (API 24)** or higher.
3. Click **Run 'app'** (`Shift + F10`) or run `./gradlew assembleDebug`.
4. Grant Contacts permission when prompted to load contacts from device.

---

## 👤 Student Metadata

- **Student Name:** Utsav Choudhary
- **USN:** 1RV23MC001
- **Course:** Master of Computer Applications (MCA)
- **Subject:** Mobile Application Development (MAD) Lab - Experiment 10
- **GitHub Repository:** [https://github.com/UtsavChoudhary25/ContactVault](https://github.com/UtsavChoudhary25/ContactVault)
