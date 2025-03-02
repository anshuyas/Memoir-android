package com.example.memoir.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.memoir.R
import com.example.memoir.databinding.ActivityHomeBinding
import com.example.memoir.ui.fragment.AnalyticsFragment
import com.example.memoir.ui.fragment.CalendarFragment
import com.example.memoir.ui.fragment.DashboardFragment
import com.example.memoir.ui.fragment.EditProfileFragment
import com.example.memoir.ui.fragment.ViewEntriesFragment
import com.example.memoir.ui.fragments.JournalEntryFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set default fragment
        if (savedInstanceState == null) {
            replaceFragment(DashboardFragment())
        }

        // Handle BottomNavigationView item selection
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard -> replaceFragment(DashboardFragment())
                R.id.nav_journal -> replaceFragment(JournalEntryFragment())
                R.id.nav_calendar -> replaceFragment(CalendarFragment())
                R.id.nav_edit_profile -> replaceFragment(EditProfileFragment())
                R.id.nav_analytics -> replaceFragment(AnalyticsFragment())
                else -> {
                    Log.w(TAG, "Unknown menu item selected: ${menuItem.itemId}")
                    false
                }
            }
            true
        }

        // Handle window insets (status bar padding)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        Log.d(TAG, "Replacing fragment with ${fragment::class.simpleName}")
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)  // Allows navigating back between fragments
            .commit()
    }

    companion object {
        private const val TAG = "HomeActivity"
    }
}