package org.jnanaprabodhini.happyteacher.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.fragment.DraftsFragment
import org.jnanaprabodhini.happyteacher.fragment.PublishedContentFragment
import org.jnanaprabodhini.happyteacher.fragment.SubmittedContentFragment

/**
 * Created by grahamearley on 11/1/17.
 */
class ContributeFragmentAdapter(fragmentManager: FragmentManager, val context: Context): FragmentStatePagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> DraftsFragment()
            1 -> SubmittedContentFragment()
            2 -> PublishedContentFragment()
            else -> Fragment()
        }
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> context.getString(R.string.drafts)
            1 -> context.getString(R.string.submitted)
            2 -> context.getString(R.string.published_tab)
            else -> ""
        }
    }
}

