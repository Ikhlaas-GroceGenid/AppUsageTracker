package com.example.appusagetracker

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Process

object UsageStatsHelper {

    /** Checks whether the user has granted the special "Usage Access" permission. */
    fun hasUsageAccess(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    /**
     * Returns per-app foreground usage time between [startTime] and [endTime] (epoch millis),
     * sorted descending by time used. Excludes this app itself and apps with zero usage.
     */
    fun queryUsage(context: Context, startTime: Long, endTime: Long): List<AppUsageInfo> {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val pm = context.packageManager

        val statsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        ) ?: emptyList()

        // Multiple UsageStats entries can exist per package within the interval; sum them.
        val aggregated = HashMap<String, Long>()
        for (stat in statsList) {
            if (stat.totalTimeInForeground <= 0) continue
            aggregated[stat.packageName] =
                (aggregated[stat.packageName] ?: 0L) + stat.totalTimeInForeground
        }

        val result = mutableListOf<AppUsageInfo>()
        for ((packageName, timeMs) in aggregated) {
            if (packageName == context.packageName) continue
            try {
                val appInfo = pm.getApplicationInfo(packageName, 0)
                // Skip apps with no launcher activity (system/background components) for a cleaner list.
                if (pm.getLaunchIntentForPackage(packageName) == null) continue
                val label = pm.getApplicationLabel(appInfo).toString()
                val icon = pm.getApplicationIcon(appInfo)
                result.add(AppUsageInfo(packageName, label, icon, timeMs))
            } catch (e: PackageManager.NameNotFoundException) {
                // App was uninstalled since the usage was recorded; skip it.
            }
        }

        return result.sortedByDescending { it.totalTimeInForegroundMs }
    }
}
