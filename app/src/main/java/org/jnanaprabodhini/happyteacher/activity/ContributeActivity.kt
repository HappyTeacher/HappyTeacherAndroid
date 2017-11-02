package org.jnanaprabodhini.happyteacher.activity

import android.os.Bundle
import android.support.annotation.IntegerRes
import android.support.design.widget.Snackbar
import com.google.firebase.auth.FirebaseAuth
import org.jnanaprabodhini.happyteacher.R

import kotlinx.android.synthetic.main.activity_contribute.*
import kotlinx.android.synthetic.main.content_contribute.*
import org.jnanaprabodhini.happyteacher.activity.base.BottomNavigationActivity
import org.jnanaprabodhini.happyteacher.adapter.ContributeFragmentAdapter
import org.jnanaprabodhini.happyteacher.extension.hasCompleteContributorProfile
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible

class ContributeActivity : BottomNavigationActivity(), FirebaseAuth.AuthStateListener {

    @IntegerRes override val bottomNavigationMenuItemId: Int = R.id.navigation_contribute

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contribute)

        bottomNavigation.selectedItemId = bottomNavigationMenuItemId
        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    override fun onResume() {
        super.onResume()
        auth.addAuthStateListener(this)

    }

    override fun onPause() {
        super.onPause()
        auth.removeAuthStateListener(this)
    }

    override fun onAuthStateChanged(changedAuth: FirebaseAuth) {
        val user = changedAuth.currentUser
        when {
            user == null -> showUiForSignedOutUser()
            user.hasCompleteContributorProfile(this) -> initializeUiForSignedInUser()
            else -> showUiForIncompleteProfile()
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
        fragmentPager.adapter = ContributeFragmentAdapter(supportFragmentManager)

        tabBar.setupWithViewPager(fragmentPager)
    }

    private fun showPager() {
        // Blend action bar and tab layout by removing elevation
        supportActionBar?.elevation = 0f
        fragmentPager.setVisible()
    }

    private fun hidePager() {
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
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
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
