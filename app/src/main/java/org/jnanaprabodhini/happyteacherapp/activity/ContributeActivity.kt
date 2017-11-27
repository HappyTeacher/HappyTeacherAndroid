package org.jnanaprabodhini.happyteacherapp.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.annotation.IntegerRes
import android.support.v4.view.ViewCompat
import com.google.firebase.auth.FirebaseAuth
import org.jnanaprabodhini.happyteacherapp.R
import kotlinx.android.synthetic.main.activity_contribute.*
import kotlinx.android.synthetic.main.content_contribute.*
import org.jnanaprabodhini.happyteacherapp.activity.base.BottomNavigationActivity
import org.jnanaprabodhini.happyteacherapp.adapter.contribute.ContributeFragmentAdapter
import org.jnanaprabodhini.happyteacherapp.extension.hasCompleteContributorProfile
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.util.ResourceType

class ContributeActivity : BottomNavigationActivity(), FirebaseAuth.AuthStateListener, SharedPreferences.OnSharedPreferenceChangeListener {

    @IntegerRes override val bottomNavigationMenuItemId: Int = R.id.navigation_contribute

    companion object IntentExtraHelper {
        fun launch(from: Activity) {
            val intent = Intent(from, ContributeActivity::class.java)
            from.startActivity(intent)
        }

        const val FRAGMENT_PAGE = "FRAGMENT_PAGE"
        fun Intent.getFragmentPage() = getIntExtra(FRAGMENT_PAGE, 0)
        fun Intent.hasFragmentPage() = hasExtra(FRAGMENT_PAGE)
        fun Intent.removeFragmentPage() = removeExtra(FRAGMENT_PAGE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contribute)

        bottomNavigation.selectedItemId = bottomNavigationMenuItemId
        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    override fun onResume() {
        super.onResume()
        auth.addAuthStateListener(this)
        prefs.preferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        auth.removeAuthStateListener(this)
        prefs.preferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onAuthStateChanged(changedAuth: FirebaseAuth) {
        val user = changedAuth.currentUser
        when {
            user == null -> showUiForSignedOutUser()
            user.hasCompleteContributorProfile(this) -> initializeUiForSignedInUser()
            else -> showUiForIncompleteProfile()
        }
    }

    override fun onSharedPreferenceChanged(preferences: SharedPreferences?, key: String?) {
        val user = auth.currentUser
        if (user != null && user.hasCompleteContributorProfile(this)) {
            initializeUiForSignedInUser()
        } else if (user != null && !user.hasCompleteContributorProfile(this)) {
            showUiForIncompleteProfile()
        }
    }

    private fun showUiForSignedOutUser() {
        hidePager()
        hideFab()
        showStatusText(getString(R.string.you_must_be_signed_in_to_contribute))
        showStatusActionButton(getString(R.string.sign_in), { launchSignIn() })
    }

    private fun showUiForIncompleteProfile() {
        hidePager()
        hideFab()
        showStatusText(getString(R.string.you_need_to_complete_your_contributor_profile_before_you_can_contribute))
        showStatusActionButton(getString(R.string.update_profile), { launchSettings() })
    }

    private fun initializeUiForSignedInUser() {
        showFab()
        initializePager()
        hideStatusViews()
    }

    private fun initializePager() {
        showPager()

        if (fragmentPager.adapter !is ContributeFragmentAdapter) {
            // Only set up the adapter if it's not already set
            fragmentPager.adapter = ContributeFragmentAdapter(supportFragmentManager, this)
        }

        if (intent.hasFragmentPage()) {
            fragmentPager.currentItem = intent.getFragmentPage()
            intent.removeFragmentPage()
        }

        tabBar.setupWithViewPager(fragmentPager)
    }

    private fun showPager() {
        // Blend action bar and tab layout by removing elevation
        supportActionBar?.elevation = 0f
        fragmentPager.setVisible()
        tabBar.setVisible()
    }

    private fun hidePager() {
        tabBar.setVisibilityGone()
        fragmentPager.setVisibilityGone()
        supportActionBar?.elevation = resources.getDimensionPixelSize(R.dimen.actionbar_default_elevation).toFloat()
    }

    private fun showStatusActionButton(text: String, action: () -> Unit = {}) {
        statusActionButton.text = text
        statusActionButton.setVisible()
        statusActionButton.setOnClickListener { action() }
    }

    private fun showStatusText(text: String) {
        statusTextView.text = text
        statusTextView.setVisible()
    }

    private fun showFab() {
        fab.setVisible()
        fab.setOnClickListener {
            showResourceTypeDialog()
        }
    }

    private fun showResourceTypeDialog() {
        val resourceTypeChoiceOptions = arrayOf(getString(R.string.lesson_plan), getString(R.string.classroom_resource))

        // TODO: add explanation of what a classroom resource is to this dialog
        AlertDialog.Builder(this)
            .setTitle(R.string.what_would_you_like_to_contribute)
            .setItems(resourceTypeChoiceOptions, { dialog, which ->
                when (resourceTypeChoiceOptions[which]) {
                    getString(R.string.lesson_plan) -> SubtopicWriteChoiceActivity.launch(this, ResourceType.LESSON)
                    getString(R.string.classroom_resource) -> SubtopicWriteChoiceActivity.launch(this, ResourceType.CLASSROOM_RESOURCE)
                }
                dialog.dismiss()
            }).show()
    }

    private fun hideStatusViews() {
        hideStatusActionButton()
        hideStatusText()
    }

    private fun hideFab() = fab.setVisibilityGone()

    private fun hideStatusActionButton() = statusActionButton.setVisibilityGone()

    private fun hideStatusText() = statusTextView.setVisibilityGone()

    override fun onBottomNavigationItemReselected() {
        // todo
    }

}
