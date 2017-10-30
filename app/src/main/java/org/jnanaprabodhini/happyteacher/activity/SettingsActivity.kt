package org.jnanaprabodhini.happyteacher.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.activity.SettingsActivity.SettingsFragment



class SettingsActivity : HappyTeacherActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    class SettingsFragment: PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentManager.beginTransaction()
                .replace(android.R.id.content, SettingsFragment())
                .commit()
    }

    override fun onSharedPreferenceChanged(preferences: SharedPreferences?, key: String?) {

    }

}
