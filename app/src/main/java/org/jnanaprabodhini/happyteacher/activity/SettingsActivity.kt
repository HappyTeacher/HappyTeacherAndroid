package org.jnanaprabodhini.happyteacher.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.activity.SettingsActivity.SettingsFragment



class SettingsActivity : HappyTeacherActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentManager.beginTransaction()
                .replace(android.R.id.content, SettingsFragment())
                .commit()
    }

    class SettingsFragment: PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)
        }

        override fun onResume() {
            super.onResume()
            preferenceScreen.sharedPreferences
                    .registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            super.onPause()
            preferenceScreen.sharedPreferences
                    .unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onSharedPreferenceChanged(preferences: SharedPreferences?, key: String?) {

        }
    }

}
