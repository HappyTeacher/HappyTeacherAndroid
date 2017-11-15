package org.jnanaprabodhini.happyteacher.fragment

import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.fragment_recycler.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.firestore.DraftHeaderRecyclerAdapter
import org.jnanaprabodhini.happyteacher.model.ResourceHeader

class SubmittedContentFragment : RecyclerFragment() {

    override val emptyRecyclerText: String by lazy { getString(R.string.you_have_no_submitted_contributions) }
    override val errorText: String by lazy { getString(R.string.there_was_an_error_loading_your_submissions) }

    override fun setupAdapter() {
        val userId = auth.currentUser?.uid ?: return

        val submissionsQuery = firestoreLocalized.collection(getString(R.string.resources))
                .whereEqualTo(getString(R.string.author_id), userId)
                .whereEqualTo(getString(R.string.status), getString(R.string.status_awaiting_review))

        val adapterOptions = FirestoreRecyclerOptions.Builder<ResourceHeader>()
                .setQuery(submissionsQuery, ResourceHeader::class.java).build()

        // TODO: update adapter!!
        //  add feedback status to the views (awaiting feedback, or feedback received + awaiting edits)
        val adapter = DraftHeaderRecyclerAdapter(adapterOptions, this, activity)
        adapter.startListening()

        recyclerView.adapter = adapter
    }
}