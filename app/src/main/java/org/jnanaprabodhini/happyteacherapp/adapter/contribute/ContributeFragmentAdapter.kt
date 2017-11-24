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
    companion object {
        const val DRAFTS_PAGE = 0
        const val SUBMITTED_PAGE = 1
        const val PUBLISHED_PAGE = 2
    }

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> DraftsFragment()
        1 -> SubmittedContentFragment()
        2 -> PublishedContentFragment()
        else -> Fragment()
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        DRAFTS_PAGE -> context.getString(R.string.drafts)
        SUBMITTED_PAGE -> context.getString(R.string.submitted)
        PUBLISHED_PAGE -> context.getString(R.string.published_tab)
        else -> ""
    }
}

