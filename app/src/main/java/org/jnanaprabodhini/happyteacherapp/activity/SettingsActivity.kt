package org.jnanaprabodhini.happyteacherapp.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.PreferenceCategory
import android.support.v7.preference.PreferenceFragmentCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.crashlytics.android.Crashlytics
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacherapp.extension.showToast
import org.jnanaprabodhini.happyteacherapp.extension.signOutAndCleanup
import org.jnanaprabodhini.happyteacherapp.preference.MandatoryContributorPreference
import org.jnanaprabodhini.happyteacherapp.util.PreferencesManager


class SettingsActivity : HappyTeacherActivity(),
        SharedPreferences.OnSharedPreferenceChangeListener,
        FirebaseAuth.AuthStateListener {

    companion object {
        fun launch(from: Activity) {
            val intent = Intent(from, CardEditorActivity::class.java)
            from.startActivity(intent)
        }

        const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 1
    }

    private lateinit var settingsFragment: SettingsFragment

    private val analytics by lazy {
        FirebaseAnalytics.getInstance(this)
    }

    class SettingsFragment: PreferenceFragmentCompat() {

        init {
            this.retainInstance = false
        }

        val prefs: PreferencesManager by lazy {
            PreferencesManager.getInstance(activity)
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            refreshPreferenceList()
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

            if (namePref?.summary != name || institutionNamePref?.summary != institution || locationPref?.summary != location) {
                refreshPreferenceList()
            }
        }

        private fun refreshPreferenceList() {
            preferenceScreen = null
            addPreferencesFromResource(R.xml.preferences)

            val locationPref = findPreference(getString(R.string.prefs_key_user_location))
            val parentActivity = activity as SettingsActivity

            locationPref.setOnPreferenceClickListener {
                parentActivity.launchPlacesAutocompleteOverlay()
                true
            }

            refreshReviewerPreferenceDisplay()

            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser == null) {
                removeUserPreferences()
            }
        }

        fun removeUserPreferences() {
            val userPreferences = findPreference(getString(R.string.prefs_key_user_settings))
            userPreferences?.let {
                preferenceScreen?.removePreference(userPreferences)
            }
        }

        fun refreshReviewerPreferenceDisplay() {
            val reviewerSettingsCategory = findPreference(getString(R.string.prefs_key_reviewer_settings))
            val auth = FirebaseAuth.getInstance()

            reviewerSettingsCategory?.let {
                if (auth.currentUser == null || !(prefs.userIsAdmin() || prefs.userIsMod())) {
                    preferenceScreen?.removePreference(reviewerSettingsCategory)
                }
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
        auth.addAuthStateListener(this)
    }

    override fun onPause() {
        super.onPause()
        settingsFragment.preferenceScreen.sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(this)
        auth.removeAuthStateListener(this)

        if (auth.currentUser == null) {
            prefs.clearUserProfileData()
        }
    }

    override fun onSharedPreferenceChanged(preferences: SharedPreferences?, key: String?) {
        when(key) {
            getString(R.string.prefs_key_user_name) -> onNameChange(preferences?.getString(key, ""))
            getString(R.string.prefs_key_user_institution) -> onInstitutionChange(preferences?.getString(key, ""))
            getString(R.string.prefs_key_user_location) -> onLocationChange(preferences?.getString(key, ""))
        }
        settingsFragment.ensureUserInfoDisplayIsCurrent()
    }

    override fun onAuthStateChanged(auth: FirebaseAuth) {
        settingsFragment.refreshReviewerPreferenceDisplay()
    }

    private fun onNameChange(newName: String?) {
        val nameChangeRequest = UserProfileChangeRequest.Builder().setDisplayName(newName).build()
        auth.currentUser?.updateProfile(nameChangeRequest)

        getUserReference()?.update(getString(R.string.display_name), newName)
    }

    private fun onInstitutionChange(newInstitution: String?) {
        getUserReference()?.update(getString(R.string.institution_key), newInstitution)
        analytics.setUserProperty(getString(R.string.institution_key), newInstitution)
    }

    /**
     * Location is changed from onActivityResult when the Google Places API
     *  returns a value. We must explicitly set the Preference view's text after
     *  a place is chosen.
     */
    private fun onLocationChange(newLocation: String?) {
        // Persist value and refresh the UI:
        val locationPref = settingsFragment.findPreference(getString(R.string.prefs_key_user_location)) as MandatoryContributorPreference
        locationPref.text = newLocation
        locationPref.callChangeListener(newLocation)

        getUserReference()?.update(getString(R.string.location_key), newLocation)
        analytics.setUserProperty(getString(R.string.location_key), newLocation)
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
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.connectionStatusCode, 0).show()
            Crashlytics.logException(e)
        } catch (e: GooglePlayServicesNotAvailableException) {
            showToast(getString(R.string.you_must_have_google_play_service_to_do_this))
            Crashlytics.logException(e)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val place = PlaceAutocomplete.getPlace(this, data)
                    onLocationChange(place.name as String)
                }
                PlaceAutocomplete.RESULT_ERROR -> {
                    val status = PlaceAutocomplete.getStatus(this, data)
                    showToast(getString(R.string.there_was_an_issue_choosing_a_location))
                    Crashlytics.log("PlaceAutocomplete returned an error. Status message: ${status.statusMessage}")
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val signOutMenuItem = menu?.findItem(R.id.menu_sign_out)
        val isUserSignedIn = auth.currentUser != null
        signOutMenuItem?.isVisible = isUserSignedIn

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item.itemId) {
            R.id.menu_open_source_notices -> launchOpenSourceNotices()
            R.id.menu_sign_out -> signOut()
        }
        return true
    }

    private fun launchOpenSourceNotices() {
        val intent = Intent(this, OssLicensesMenuActivity::class.java)
        val title = getString(R.string.open_source_notices)
        intent.putExtra("title", title)
        startActivity(intent)
    }

    private fun signOut() {
        auth.signOutAndCleanup(this)
        settingsFragment.removeUserPreferences()
    }

}