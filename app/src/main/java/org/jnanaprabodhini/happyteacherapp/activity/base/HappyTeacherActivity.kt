package org.jnanaprabodhini.happyteacherapp.activity.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.crashlytics.android.Crashlytics
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import org.jnanaprabodhini.happyteacherapp.BuildConfig
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.extension.addOneTimeSnapshotListener
import org.jnanaprabodhini.happyteacherapp.extension.showToast
import org.jnanaprabodhini.happyteacherapp.extension.withCurrentLocale
import org.jnanaprabodhini.happyteacherapp.model.User
import org.jnanaprabodhini.happyteacherapp.service.FirebaseRegistrationTokenService
import org.jnanaprabodhini.happyteacherapp.util.PreferencesManager
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

/**
 * An abstract activity for all activities in the app. Includes access
 *  to the Firebase root database and the database for the current language,
 *  and locale switching.
 */
abstract class HappyTeacherActivity: AppCompatActivity() {

    companion object {
        const val AUTH_REQUEST_CODE = 0
    }

    val firestoreRoot: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val firestoreLocalized: DocumentReference by lazy {
        firestoreRoot.collection(getString(R.string.localized)).document(prefs.getCurrentLanguageCode())
    }

    val firestoreUsersCollection: CollectionReference by lazy {
        firestoreRoot.collection(getString(R.string.users))
    }

    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val prefs: PreferencesManager by lazy {
        PreferencesManager.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resetTitle()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase.withCurrentLocale()))
    }

    protected fun refreshActivity() {
        val intent = intent
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Make "Up" button go Back
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun resetTitle() {
        try {
            val titleId = packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA).labelRes
            if (titleId != 0) {
                setTitle(titleId)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            // Then we will show the title as it is set in top-level resources.
            Crashlytics.logException(e)
        }
    }

    fun getUserReference(): DocumentReference? {
        auth.currentUser?.let { user ->
            val id = user.uid
            return firestoreUsersCollection.document(id)
        }

        return null
    }

    fun launchSignIn() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                        .setAvailableProviders(
                                listOf(AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                        AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                                        AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .build(),
                AUTH_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTH_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)

            // Successfully signed in
            if (resultCode == Activity.RESULT_OK) {
                persistUserInfo()
                Crashlytics.setUserIdentifier(auth.currentUser?.uid)
                return
            } else {
                // Sign in failed
                when {
                    response == null -> return // User pressed back button
                    response.errorCode == ErrorCodes.NO_NETWORK -> {
                        showToast(R.string.sign_in_failed_network_error)
                        return
                    }
                    response.errorCode == ErrorCodes.UNKNOWN_ERROR -> {
                        showToast(R.string.sign_in_failed)
                        Crashlytics.log("Sign in failed due to unknown error.")
                        return
                    }
                    else -> {
                        showToast(R.string.sign_in_failed)
                        Crashlytics.log("Sign in failed due to unknown error with unknown error code.")
                        return
                    }
                }
            }
        }
    }

    private fun persistUserInfo() {
        val analytics = FirebaseAnalytics.getInstance(this)
        auth.currentUser?.uid?.let { analytics.setUserId(it) }

        FirebaseRegistrationTokenService.updateUserToken(this)
        getUserReference()?.addOneTimeSnapshotListener(this, { snapshot, firebaseFirestoreException ->
            if (snapshot.exists()) {
                val userModel = snapshot.toObject(User::class.java)

                prefs.setUserLocation(userModel.location)
                prefs.setUserInstitution(userModel.institution)
                prefs.setUserName(userModel.displayName)
                prefs.setUserRole(userModel.role)

                analytics.setUserProperty("role", userModel.role)
                analytics.setUserProperty("institution", userModel.institution)
            } else {
                // If Cloud Functions haven't created the user fast enough,
                //  it's possible for the user Document to not exist yet.
                //  In this case, fail silently. The user will have to re-enter
                //  their profile info. :(
                Crashlytics.log("User object does not exist in Firestore upon registration.")

                prefs.clearUserProfileData()

                // Recover name if possible
                prefs.setUserName(auth.currentUser?.displayName.orEmpty())
            }
        })
    }

    fun startActivityWithSlideOutTransition(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.anim.stay, R.anim.fade_slide_out_bottom)
    }
}