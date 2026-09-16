# App Usage Tracker (Android)

An Android app that shows how much time you've spent in each app on your device today.

## How it works

Android doesn't let regular apps silently monitor other apps for privacy reasons.
Instead, it exposes `UsageStatsManager`, which requires the user to manually grant
a special **"Usage Access"** permission in system Settings. This app:

1. Checks whether Usage Access is granted (`UsageStatsHelper.hasUsageAccess`).
2. If not, shows a button that opens the system settings screen where the user
   grants access.
3. Once granted, queries usage stats from midnight to now and lists every app
   with a launcher icon, sorted by time used (most-used first).

## Project structure

```
AppUsageTracker/
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/appusagetracker/
│       │   ├── MainActivity.kt        # permission flow + list loading
│       │   ├── UsageStatsHelper.kt    # queries UsageStatsManager, aggregates per app
│       │   ├── UsageAdapter.kt        # RecyclerView adapter
│       │   └── AppUsageInfo.kt        # data model + duration formatting
│       └── res/
│           ├── layout/activity_main.xml
│           ├── layout/item_app_usage.xml
│           └── values/
├── build.gradle
└── settings.gradle
```

## Get an APK via GitHub Actions (no local Android Studio needed)

This project includes `.github/workflows/build-apk.yml`, which builds a debug APK
in the cloud every time you push to `main`.

1. **Create a new GitHub repo** (e.g. `AppUsageTracker`) — can be private or public.
2. **Upload this project** into it. Easiest way with git installed locally:
   ```bash
   cd AppUsageTracker
   git init
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/AppUsageTracker.git
   git push -u origin main
   ```
   (No git installed? On github.com, click "Add file" → "Upload files" and drag
   the whole unzipped folder in instead.)
3. **Watch the build**: on GitHub, go to the **Actions** tab of your repo. You'll
   see a "Build APK" workflow run start automatically (pushing to `main` triggers
   it). Wait for the green checkmark (~3-5 minutes).
4. **Download the APK**: click into the finished workflow run → scroll to
   **Artifacts** at the bottom → download `app-debug-apk` (a zip containing
   `app-debug.apk`).
5. **Install it on your phone**:
   - Transfer the APK to your phone (email it to yourself, use Google Drive, USB cable, etc.)
   - Open it on your phone. Android will warn about "unknown sources" — tap
     Settings → allow installs from that source (Chrome/Files, whichever you used).
   - Tap Install.
6. **Re-run anytime**: any future push to `main` rebuilds automatically. You can
   also trigger it manually from the Actions tab (`Run workflow` button), since
   the workflow includes `workflow_dispatch`.

Note: this builds a **debug** APK (unsigned, fine for installing on your own
device for testing). It won't be accepted by the Play Store without additional
signing configuration — ask if you want that set up too.

## How to run it locally in Android Studio

1. Open the `AppUsageTracker/` folder in Android Studio (Hedgehog or newer recommended).
2. Let Gradle sync (it will download dependencies — needs internet).
3. Run on a device or emulator running **Android 8.0 (API 26) or higher**.
4. On first launch, tap "Grant Permission" — this opens Settings → Usage Access.
   Find "App Usage Tracker" in the list and toggle it on.
5. Return to the app (press back) — it automatically re-checks and loads today's
   usage the moment you resume it.

## Extending it

Some natural next steps if you want to build on this:

- **Custom date ranges**: swap the hardcoded "since midnight" query in
  `loadTodayUsage()` for a date picker, and pass different `startTime`/`endTime`
  values to `UsageStatsHelper.queryUsage`.
- **Weekly/monthly view**: use `UsageStatsManager.INTERVAL_WEEKLY` or
  `INTERVAL_MONTHLY` instead of `INTERVAL_DAILY`.
- **Charts**: feed `AppUsageInfo` list into a bar/pie chart library (e.g. MPAndroidChart).
- **Widgets/notifications**: add a home-screen widget or daily summary notification
  using the same `UsageStatsHelper`.
- **Per-app daily limits**: store limits in `SharedPreferences` or Room, and check
  against them in `loadTodayUsage()`.

## Notes

- `PACKAGE_USAGE_STATS` is a "special" permission — it can't be requested via the
  normal runtime permission dialog. The Settings deep-link (`ACTION_USAGE_ACCESS_SETTINGS`)
  is the only way to get there.
- Accuracy: `totalTimeInForeground` reflects time an app's activity was in the
  foreground, not necessarily "actively used" (e.g. video playing in background
  won't count, but an idle foreground screen will).
