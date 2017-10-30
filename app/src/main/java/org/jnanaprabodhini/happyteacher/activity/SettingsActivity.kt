package org.jnanaprabodhini.happyteacher.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import com.google.firebase.auth.UserProfileChangeRequest
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.HappyTeacherActivity


class SettingsActivity : HappyTeacherActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    lateinit var settingsFragment: SettingsFragment

    class SettingsFragment: PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingsFragment = SettingsFragment()
        fragmentManager.beginTransaction()
                .replace(android.R.id.content, settingsFragment)
                .commit()
    }

    override fun onResume() {
        super.onResume()
        settingsFragment.preferenceScreen.sharedPreferences
                .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        settingsFragment.preferenceScreen.sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(preferences: SharedPreferences?, key: String?) {
        when(key) {
            getString(R.string.prefs_key_user_name) -> onNameChange(preferences?.getString(key, ""))
            getString(R.string.prefs_key_user_institution) -> onInstitutionChange(preferences?.getString(key, ""))
            getString(R.string.prefs_key_user_location) -> onLocationChange(preferences?.getString(key, ""))
        }
    }

    private fun onNameChange(newName: String?) {
        val nameChangeRequest = UserProfileChangeRequest.Builder().setDisplayName(newName).build()
        auth.currentUser?.updateProfile(nameChangeRequest)

        // TODO: save to firestore
    }

    private fun onInstitutionChange(newInstitution: String?) {
        // todo: save to firestore
    }

    private fun onLocationChange(newLocation: String?) {
        // todo: save to Firestore
    }

}
