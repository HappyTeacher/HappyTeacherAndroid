package org.jnanaprabodhini.happyteacher.activity

import android.os.Bundle
import android.support.annotation.IntegerRes
import android.support.design.widget.Snackbar
import com.google.firebase.auth.FirebaseAuth
import org.jnanaprabodhini.happyteacher.R

import kotlinx.android.synthetic.main.activity_contribute.*
import org.jnanaprabodhini.happyteacher.activity.base.BottomNavigationActivity
import org.jnanaprabodhini.happyteacher.extension.hasCompleteContributorProfile

class ContributeActivity : BottomNavigationActivity(), FirebaseAuth.AuthStateListener {

    @IntegerRes override val bottomNavigationMenuItemId: Int = R.id.navigation_contribute

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contribute)

        auth.addAuthStateListener(this)

        bottomNavigation.selectedItemId = bottomNavigationMenuItemId
        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onAuthStateChanged(changedAuth: FirebaseAuth) {
        val user = changedAuth.currentUser
        when {
            user == null -> showUiForSignedOutUser()
            user.hasCompleteContributorProfile(this) -> initializeUiForSignedInUser()
            else -> initializeUiForIncompleteProfile()
        }
    }

    private fun showUiForSignedOutUser() {
        //
    }

    private fun initializeUiForIncompleteProfile() {
        //
    }

    private fun initializeUiForSignedInUser() {
        //
    }

    override fun onBottomNavigationItemReselected() {
        // todo
    }

}
