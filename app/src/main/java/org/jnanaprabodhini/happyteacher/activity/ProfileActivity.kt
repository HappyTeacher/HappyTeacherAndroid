package org.jnanaprabodhini.happyteacher.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.activity_profile.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.extension.showToast

class ProfileActivity : HappyTeacherActivity() {

    private val PLACE_AUTOCOMPLETE_REQUEST_CODE = 1
    private val currentUser by lazy { auth.currentUser }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        signOutButton.setOnClickListener {
            auth.signOut()
            finish()
        }

        setupNameInput()
        setupPlacesAutocomplete()

        setupSaveButton()
    }

    private fun setupNameInput() {
        currentUser?.let { nameInput.setText(it.displayName) }
    }

    private fun setupPlacesAutocomplete() {
        locationInput.inputType = InputType.TYPE_NULL
        locationInput.setOnClickListener { launchPlacesAutocompleteOverlay() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val place = PlaceAutocomplete.getPlace(this, data)
                    locationInput.setText(place.name)
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

    private fun getUserDocumentRef(): DocumentReference? {
        return if (!currentUser?.phoneNumber.isNullOrEmpty()) {
            firestoreUsersCollection.document(currentUser?.phoneNumber!!)
        } else {
            showToast("You need a phone number for now, maybe forever.")
            null
        }
    }

    private fun setupSaveButton() = saveButton.setOnClickListener {
        saveName()
        saveLocation()
        saveInstitution()
    }

    private fun saveName() {
        val name = nameInput.text.toString()

        val nameChangeRequest = UserProfileChangeRequest.Builder().setDisplayName(name).build()
        currentUser?.updateProfile(nameChangeRequest)

        getUserDocumentRef()?.update("name", name)
    }

    private fun saveLocation() {
        val location = locationInput.text.toString()
        getUserDocumentRef()?.update("location", location)
    }

    private fun saveInstitution() {
        val institution = institutionInput.text.toString()
        getUserDocumentRef()?.update("institution", institution)
    }
}
