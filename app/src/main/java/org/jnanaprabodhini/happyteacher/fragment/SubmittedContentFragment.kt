package org.jnanaprabodhini.happyteacher.fragment

import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_recycler.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.contribute.DraftHeaderAdapter
import org.jnanaprabodhini.happyteacher.adapter.contribute.SubmissionHeaderAdapter
import org.jnanaprabodhini.happyteacher.model.ResourceHeader
import org.jnanaprabodhini.happyteacher.util.FirestoreKeys
import org.jnanaprabodhini.happyteacher.util.ResourceStatus

class SubmittedContentFragment : RecyclerFragment() {

    override val emptyRecyclerText: String by lazy { getString(R.string.you_have_no_submitted_contributions) }
    override val errorText: String by lazy { getString(R.string.there_was_an_error_loading_your_submissions) }

    override fun setupAdapter() {
        val userId = auth.currentUser?.uid ?: return

        val submissionsQuery = firestoreLocalized.collection(getString(R.string.resources))
                .whereEqualTo(getString(R.string.author_id), userId)
                .whereEqualTo(ResourceStatus.AWAITING_REVIEW_OR_CHANGES_REQUESTED, true)
                .orderBy(FirestoreKeys.STATUS, Query.Direction.DESCENDING)

        val adapterOptions = FirestoreRecyclerOptions.Builder<ResourceHeader>()
                .setQuery(submissionsQuery, ResourceHeader::class.java).build()

        val adapter = SubmissionHeaderAdapter(adapterOptions, this, activity)
        adapter.startListening()

        recyclerView.adapter = adapter
    }
}