package org.jnanaprabodhini.happyteacher.fragment

import org.jnanaprabodhini.happyteacher.R

class PublishedContentFragment : RecyclerFragment() {

    override val emptyRecyclerText: String by lazy { getString(R.string.you_have_no_submitted_contributions) }
    override val errorText: String by lazy { getString(R.string.there_was_an_error_loading_your_submissions) }

    override fun setupAdapter() {
        // todo
    }

}