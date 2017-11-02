package org.jnanaprabodhini.happyteacher.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import org.jnanaprabodhini.happyteacher.fragment.ContributeDraftsFragment

/**
 * Created by grahamearley on 11/1/17.
 */
class ContributeFragmentAdapter(fragmentManager: FragmentManager): FragmentStatePagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        return ContributeDraftsFragment()
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Drafts"
            1 -> "Submitted"
            2 -> "Published"
            else -> ""
        }
    }
}