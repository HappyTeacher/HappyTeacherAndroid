package org.jnanaprabodhini.happyteacher.activity.base

import android.content.Intent
import android.support.design.widget.BottomNavigationView
import android.view.Menu
import android.view.MenuItem
import org.jnanaprabodhini.happyteacher.util.LocaleManager
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.BoardLessonsActivity
import org.jnanaprabodhini.happyteacher.activity.TopicsListActivity
import org.jnanaprabodhini.happyteacher.dialog.LanguageChoiceDialog
import org.jnanaprabodhini.happyteacher.prefs
import com.google.firebase.auth.FirebaseAuth
import org.jnanaprabodhini.happyteacher.extension.showToast
import com.firebase.ui.auth.AuthUI
import java.util.*
import com.firebase.ui.auth.ErrorCodes
import android.R.attr.data
import android.app.Activity
import android.support.design.widget.Snackbar
import android.util.Log
import android.widget.Toast
import com.firebase.ui.auth.IdpResponse
import org.jnanaprabodhini.happyteacher.BuildConfig


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
                // TODO
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
        menuInflater.inflate(R.menu.top_level_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_change_language -> showLanguageChangeDialog()
            R.id.menu_sign_in -> beginSignIn()
        }
        return true
    }

    private fun showLanguageChangeDialog() {
        val dialog = LanguageChoiceDialog(this)
        dialog.show()
    }

    private fun beginSignIn() {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            auth.signOut()
            this.showToast("You've been signed out.")
        } else {
            launchSignIn()
        }
    }

    private fun launchSignIn() {
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

    fun changeLocaleAndRefresh(locale: String) {
        if (locale != prefs.getCurrentLanguageCode()) {
            LocaleManager.changeLocale(locale)
            refreshActivity()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTH_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)

            // Successfully signed in
            if (resultCode == Activity.RESULT_OK) {
                showToast("Sign in successful. Nice.")
                return
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    showToast("Sign in FAILED. Did you press the back button?")
                    return
                }

                if (response.errorCode == ErrorCodes.NO_NETWORK) {
                    showToast("Sign in FAILED. No network.")
                    return
                }

                if (response.errorCode == ErrorCodes.UNKNOWN_ERROR) {
                    showToast("Sign in failed. Unknown error.")
                    return
                }
            }

            showToast("Unknown sign in response... hm.")
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