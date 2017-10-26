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

/**
 * An abstract activity for activities that are opened
 *  from the bottom navigation bar.
 */
abstract class BottomNavigationActivity: HappyTeacherActivity() {

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
        }
        return true
    }

    fun showLanguageChangeDialog() {
        val dialog = LanguageChoiceDialog(this)
        dialog.show()
    }

    fun changeLocaleAndRefresh(locale: String) {
        if (locale != prefs.getCurrentLanguageCode()) {
            LocaleManager.changeLocale(locale)
            refreshActivity()
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