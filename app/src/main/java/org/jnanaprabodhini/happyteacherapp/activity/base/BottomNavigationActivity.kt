package org.jnanaprabodhini.happyteacherapp.activity.base

import android.content.Intent
import android.support.design.widget.BottomNavigationView
import android.view.Menu
import android.view.MenuItem
import org.jnanaprabodhini.happyteacherapp.util.LocaleManager
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.dialog.LanguageChoiceDialog
import org.jnanaprabodhini.happyteacherapp.extension.showToast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import android.app.Activity
import com.crashlytics.android.Crashlytics
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.analytics.FirebaseAnalytics
import org.jnanaprabodhini.happyteacherapp.BuildConfig
import org.jnanaprabodhini.happyteacherapp.activity.*
import org.jnanaprabodhini.happyteacherapp.extension.signOutAndCleanup
import org.jnanaprabodhini.happyteacherapp.model.User
import org.jnanaprabodhini.happyteacherapp.service.FirebaseRegistrationTokenService


/**
 * An abstract activity for activities that are opened
 *  from the bottom navigation bar.
 */
abstract class BottomNavigationActivity: HappyTeacherActivity() {

    companion object {
        const val AUTH_REQUEST_CODE = 0
    }

    abstract val bottomNavigationMenuItemId: Int

    protected val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when (item.itemId) {
            bottomNavigationMenuItemId -> {
                this.onBottomNavigationItemReselected()
            }

            R.id.navigation_board -> {
                val boardActivityIntent = Intent(this, BoardLessonsActivity::class.java)
                startBottomNavigationActivityWithFade(boardActivityIntent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_topics -> {
                val topicsActivityIntent = Intent(this, TopicsListActivity::class.java)
                startBottomNavigationActivityWithFade(topicsActivityIntent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_contribute -> {
                val contributeActivityIntent = Intent(this, ContributeActivity::class.java)
                startBottomNavigationActivityWithFade(contributeActivityIntent)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    fun startBottomNavigationActivityWithFade(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in_quick, R.anim.fade_out_quick)
    }

    abstract fun onBottomNavigationItemReselected()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_top_level_activity, menu)
        val reviewLessonsMenuItem = menu?.findItem(R.id.menu_moderator_submission_review)

        if (prefs.userIsAdmin() || prefs.userIsMod()) {
            reviewLessonsMenuItem?.isVisible = true
        }

        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val signInMenuItem = menu?.findItem(R.id.menu_sign_in)
        val isUserSignedIn = auth.currentUser != null
        signInMenuItem?.isVisible = !isUserSignedIn

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_change_language -> showLanguageChangeDialog()
            R.id.menu_sign_in -> if (auth.currentUser == null) launchSignIn() else signOut()
            R.id.menu_settings -> launchSettings()
            R.id.menu_moderator_submission_review -> launchSubmissionReview()
        }
        return true
    }

    private fun launchSubmissionReview() {
        SubmissionsForReviewActivity.launch(this)
    }

    protected fun launchSettings() {
        val profileIntent = Intent(this, SettingsActivity::class.java)
        startActivity(profileIntent)
    }

    private fun showLanguageChangeDialog() {
        val dialog = LanguageChoiceDialog(this)
        dialog.show()
    }

    protected fun launchSignIn() {
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

    private fun signOut() {
        auth.signOutAndCleanup(this)
    }

    fun changeLocaleAndRefresh(locale: String) {
        if (locale != prefs.getCurrentLanguageCode()) {
            LocaleManager.changeLocale(locale, this)
            refreshActivity()
        }
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
        auth.currentUser?.uid?.let { FirebaseAnalytics.getInstance(this).setUserId(it) }

        FirebaseRegistrationTokenService.updateUserToken(this)
        getUserReference()?.get()?.addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val userModel = snapshot.toObject(User::class.java)

                prefs.setUserLocation(userModel.location)
                prefs.setUserInstitution(userModel.institution)
                prefs.setUserName(userModel.displayName)
                prefs.setUserRole(userModel.role)
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
        }
    }

    /**
     * According to Material Design Guidelines, top-level activities
     *  should close on back pressed. These bottom nav activities are
     *  top-level.
     *
     *  https://material.io/guidelines/components/bottom-navigation.html#bottom-navigation-behavior
     */
    override fun onBackPressed() {
        finishAffinity()
    }

}