package org.jnanaprabodhini.happyteacherapp.adapter.contribute

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.fragment.DraftsFragment
import org.jnanaprabodhini.happyteacherapp.fragment.PublishedContentFragment
import org.jnanaprabodhini.happyteacherapp.fragment.SubmittedContentFragment

/**
 * Created by grahamearley on 11/1/17.
 */
class ContributeFragmentAdapter(fragmentManager: FragmentManager, val context: Context): FragmentStatePagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment = when (position) {
        0 -> DraftsFragment()
        1 -> SubmittedContentFragment()
        2 -> PublishedContentFragment()
        else -> Fragment()
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> context.getString(R.string.drafts)
        1 -> context.getString(R.string.submitted)
        2 -> context.getString(R.string.published_tab)
        else -> ""
    }
}

