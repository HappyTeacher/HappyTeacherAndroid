package org.jnanaprabodhini.happyteacher.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import android.support.v7.preference.PreferenceFragmentCompat
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.firebase.auth.UserProfileChangeRequest
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.prefs


class SettingsActivity : HappyTeacherActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    companion object { const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 1 }

    private lateinit var settingsFragment: SettingsFragment

    class SettingsFragment: PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.preferences)

            val locationPref = findPreference(getString(R.string.prefs_key_user_location))
            val parentActivity = activity as SettingsActivity

            locationPref.setOnPreferenceClickListener { parentActivity.launchPlacesAutocompleteOverlay(); true }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingsFragment = SettingsFragment()
        supportFragmentManager.beginTransaction()
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

    private fun launchPlacesAutocompleteOverlay() {
        val cityFilter = AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build()

        try {
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(cityFilter)
                    .build(this)
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
        } catch (e: GooglePlayServicesRepairableException) {
            // TODO: Handle the error.
        } catch (e: GooglePlayServicesNotAvailableException) {
            // TODO: Handle the error.
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val place = PlaceAutocomplete.getPlace(this, data)
                    prefs.setUserLocation(place.name as String)
                }
                PlaceAutocomplete.RESULT_ERROR -> {
                    val status = PlaceAutocomplete.getStatus(this, data)
                    // TODO: Handle the error.
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
        }
    }

}
