package org.jnanaprabodhini.happyteacher.activity

import android.os.Bundle
import android.support.annotation.IntegerRes
import android.support.design.widget.Snackbar
import org.jnanaprabodhini.happyteacher.R

import kotlinx.android.synthetic.main.activity_contribute.*
import org.jnanaprabodhini.happyteacher.activity.base.BottomNavigationActivity

class ContributeActivity : BottomNavigationActivity() {
    @IntegerRes override val bottomNavigationMenuItemId: Int = R.id.navigation_contribute

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contribute)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onBottomNavigationItemReselected() {
    }

}
