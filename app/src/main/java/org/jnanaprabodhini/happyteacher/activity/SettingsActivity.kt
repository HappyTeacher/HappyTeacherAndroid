package org.jnanaprabodhini.happyteacher.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.extension.showToast
import org.jnanaprabodhini.happyteacher.preference.EditTextValueDisplayPreference
import org.jnanaprabodhini.happyteacher.util.PreferencesManager

class SettingsActivity : HappyTeacherActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    companion object { const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 1 }

    private lateinit var settingsFragment: SettingsFragment

    class SettingsFragment: PreferenceFragmentCompat() {

        val prefs: PreferencesManager by lazy {
            PreferencesManager.getInstance(activity)
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.preferences)

            val locationPref = findPreference(getString(R.string.prefs_key_user_location))
            val parentActivity = activity as SettingsActivity

            locationPref.setOnPreferenceClickListener { parentActivity.launchPlacesAutocompleteOverlay(); true }

            // Remove user info prefs if user is not signed in. If signed in, setup sign out button
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser == null) {
                removeUserPreferences()
            } else {
                val signOutPref = findPreference(getString(R.string.prefs_key_user_sign_out))
                signOutPref.setOnPreferenceClickListener {
                    auth.signOut()
                    removeUserPreferences()
                    true
                }
            }
        }

        private fun removeUserPreferences() {
            val userPreferences = findPreference(getString(R.string.prefs_key_user_settings))
            preferenceScreen.removePreference(userPreferences)
        }

        /**
         * User data is updated in preferences after sign-in. If the user enters Settings before
         *  this completes, the Settings page needs to refresh if the Preference values change (from
         *  outside the Settings page).
         *
         *  This function checks that the summary UI displays the correct values, and if it doesn't,
         *   it refreshes the UI.
         */
        fun ensureUserInfoDisplayIsCurrent() {
            val namePref = findPreference(getString(R.string.prefs_key_user_name))
            val institutionNamePref = findPreference(getString(R.string.prefs_key_user_institution))
            val locationPref = findPreference(getString(R.string.prefs_key_user_location))

            val name = prefs.getUserName()
            val institution = prefs.getUserInstitution()
            val location = prefs.getUserLocation()

            if (namePref.summary != name || institutionNamePref.summary != institution || locationPref.summary != location) {
                refreshPreferenceList()
            }
        }

        private fun refreshPreferenceList() {
            preferenceScreen = null
            addPreferencesFromResource(R.xml.preferences)
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
        settingsFragment.ensureUserInfoDisplayIsCurrent()
    }

    private fun onNameChange(newName: String?) {
        val nameChangeRequest = UserProfileChangeRequest.Builder().setDisplayName(newName).build()
        auth.currentUser?.updateProfile(nameChangeRequest)

        getUserReference()?.update(getString(R.string.display_name), newName)
    }

    private fun onInstitutionChange(newInstitution: String?) {
        getUserReference()?.update(getString(R.string.institution_key), newInstitution)
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

        getUserReference()?.update(getString(R.string.location_key), newLocation)
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
