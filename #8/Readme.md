# 22110041 - Bùi Nguyễn An Khang

## 🔧 Running the Application

### 📋 Prerequisites
- Android Studio Bumblebee or later  
- Android SDK version 24 or higher  
- Active internet connection (for image downloading)

### 🚀 Setup Instructions
1. Clone the repository or open the project in Android Studio.
2. Ensure the following permissions are present in `AndroidManifest.xml` (already included):
   ```xml
   <uses-permission android:name="android.permission.INTERNET"/>
   <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
   <uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
   ```
3. Connect a physical device or emulator with internet access.
4. Build and run the project from Android Studio.

---

## 💡 Features & Component Breakdown

### 1. 🧵 AsyncTask
- The `ImageDownloadTask` class utilizes `AsyncTask` to download images in the background.
- Shows a loading status using a `TextView` during the download.
- Manages errors such as invalid URLs or network issues.
- This feature has been enhanced using `AsyncTaskLoader`.

### 2. 🔁 AsyncTaskLoader
- `ImageLoader` is an `AsyncTaskLoader<Bitmap>` for more efficient background processing.
- Integrated with `LoaderManager` to preserve the loaded image across configuration changes.
- Offers better lifecycle management than `AsyncTask`, reducing the risk of memory leaks.

### 3. 🌐 BroadcastReceiver for Network Status
- A custom `NetworkChangeReceiver` monitors `CONNECTIVITY_ACTION` broadcasts.
- Uses `ConnectivityManager` to check current internet status.
- Disables the "Load Image" button and shows a "No internet connection" message when offline.
- Automatically re-enables the button when the connection is restored.

### 4. 🚀 Foreground Service with Notification
- A `ForegroundService` runs every 5 minutes, showing a persistent notification via `NotificationCompat`.
- The notification includes the message "Image Loader Service is running" and opens the app on tap.
- Compatible with Android 8.0+ using `NotificationChannel`.
- Requests the `POST_NOTIFICATIONS` permission at runtime on Android 13+.

### 5. 🛡 Permissions
- Required permissions (`INTERNET`, `ACCESS_NETWORK_STATE`, and `POST_NOTIFICATIONS`) are defined in `AndroidManifest.xml`.
- `POST_NOTIFICATIONS` is requested dynamically for API level 33+ (Tiramisu).

---

## 🖼 User Interface Overview

### Core UI Components
- **EditText** – Accepts the image URL  
- **Button (Load Image)** – Initiates image download  
- **Button (Reset)** – Clears the image view  
- **ImageView** – Displays the fetched image  
- **TextView** – Shows status updates or error messages  

### UI Design
- Built with `ConstraintLayout` for a responsive and adaptable layout.
- Fully handles screen orientation changes.
- Provides user feedback during all loading and error scenarios.

---

## 💬 Code Structure & Documentation

- All significant classes and methods include descriptive comments:
  - `ImageLoader` – Covers how loading is done in the background and how results are returned.
  - `NetworkChangeReceiver` – Details the logic for monitoring connectivity changes.
  - `ForegroundService` – Includes full notification setup with documentation.
- The `MainActivity.java` is well-organized with labeled sections:
  - UI Initialization  
  - Permission Handling  
  - Loader Setup  

---

