package com.example.appusagetracker

import android.graphics.drawable.Drawable

data class AppUsageInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val totalTimeInForegroundMs: Long
) {
    /** Formats duration as "Xh Ym" or "Ym Zs" for short durations. */
    fun formattedDuration(): String {
        val totalSeconds = totalTimeInForegroundMs / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m ${seconds}s"
            else -> "${seconds}s"
        }
    }
}
