package org.jnanaprabodhini.happyteacher.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceCategory
import android.preference.PreferenceFragment
import android.support.v7.preference.PreferenceFragmentCompat
import android.util.Log
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.extension.showToast
import org.jnanaprabodhini.happyteacher.preference.EditTextValueDisplayPreference
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

            // Remove user info prefs if user is not signed in
            if (FirebaseAuth.getInstance().currentUser == null) {
                val userPreferences = findPreference(getString(R.string.prefs_key_user_settings))
                preferenceScreen.removePreference(userPreferences)
            }
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

    /**
     * Location is changed from onActivityResult when the Google Places API
     *  returns a value. We must explicitly set the Preference's text after
     *  a place is chosen.
     */
    private fun onLocationChange(newLocation: String?) {
        // Persist value and refresh the UI:
        val locationPref = settingsFragment.findPreference(getString(R.string.prefs_key_user_location)) as EditTextValueDisplayPreference
        locationPref.text = newLocation
        locationPref.callChangeListener(newLocation)

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
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.connectionStatusCode, 0)
        } catch (e: GooglePlayServicesNotAvailableException) {
            showToast(getString(R.string.you_must_have_google_play_service_to_do_this))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val place = PlaceAutocomplete.getPlace(this, data)
                    onLocationChange(place.name as String)
                }
                PlaceAutocomplete.RESULT_ERROR -> {
                    val status = PlaceAutocomplete.getStatus(this, data)
                    // TODO: Log status to analytics.
                    showToast(getString(R.string.there_was_an_issue_choosing_a_location))
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
        }
    }

}
