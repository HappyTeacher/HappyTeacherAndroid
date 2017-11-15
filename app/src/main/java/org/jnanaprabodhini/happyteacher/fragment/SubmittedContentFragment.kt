package org.jnanaprabodhini.happyteacher.fragment

import org.jnanaprabodhini.happyteacher.R

class SubmittedContentFragment : RecyclerFragment() {

    override val emptyRecyclerText: String by lazy { getString(R.string.you_have_no_published_contributions) }
    override val errorText: String by lazy { getString(R.string.there_was_an_error_loading_your_published_contributions) }

    override fun setupAdapter() {
        // todo
    }
}