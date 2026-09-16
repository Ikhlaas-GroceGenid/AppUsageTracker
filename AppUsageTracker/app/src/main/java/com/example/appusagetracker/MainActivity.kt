package com.example.appusagetracker

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appusagetracker.databinding.ActivityMainBinding
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.usageList.layoutManager = LinearLayoutManager(this)

        binding.grantPermissionButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }

    override fun onResume() {
        super.onResume()
        // Re-check every time the activity resumes, since the user grants this
        // permission from a separate Settings screen and then returns here.
        if (UsageStatsHelper.hasUsageAccess(this)) {
            binding.permissionGroup.visibility = android.view.View.GONE
            binding.usageList.visibility = android.view.View.VISIBLE
            loadTodayUsage()
        } else {
            binding.permissionGroup.visibility = android.view.View.VISIBLE
            binding.usageList.visibility = android.view.View.GONE
        }
    }

    private fun loadTodayUsage() {
        val endTime = System.currentTimeMillis()
        val startTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val usageList = UsageStatsHelper.queryUsage(this, startTime, endTime)

        binding.emptyState.visibility =
            if (usageList.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE

        binding.usageList.adapter = UsageAdapter(usageList)
    }
}
