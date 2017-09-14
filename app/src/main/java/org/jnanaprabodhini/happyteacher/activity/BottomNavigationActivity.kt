package org.jnanaprabodhini.happyteacher.activity

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.BottomNavigationView
import kotlinx.android.synthetic.main.activity_board_lessons.*
import org.jnanaprabodhini.happyteacher.R

/**
 * Created by grahamearley on 9/14/17.
 */
abstract class BottomNavigationActivity: HappyTeacherActivity() {

    abstract val bottomNavigationMenuItemId: Int

    protected val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        if (item.itemId == bottomNavigationMenuItemId) return@OnNavigationItemSelectedListener true

        when (item.itemId) {
            R.id.navigation_board -> {
                val boardActivityIntent = Intent(this, BoardLessonsActivity::class.java)
                startActivity(boardActivityIntent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_topics -> {
                val topicsActivityIntent = Intent(this, TopicsListActivity::class.java)
                startActivity(topicsActivityIntent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_contribute -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onResume() {
        super.onResume()

        // Ensure that the correct bottom nav item is selected:
        bottomNavigation.selectedItemId = this.bottomNavigationMenuItemId
    }

}