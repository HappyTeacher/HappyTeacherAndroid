package org.jnanaprabodhini.happyteacherapp.fragment

import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.fragment_recycler.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.adapter.contribute.PublishedSubmissionHeaderAdapter
import org.jnanaprabodhini.happyteacherapp.model.ResourceHeader
import org.jnanaprabodhini.happyteacherapp.util.ResourceStatus

class PublishedContentFragment : RecyclerFragment() {

    override val emptyRecyclerText: String by lazy { getString(R.string.you_have_no_published_contributions) }
    override val errorText: String by lazy { getString(R.string.there_was_an_error_loading_your_published_contributions) }

    override fun setupAdapter() {
        val userId = auth.currentUser?.uid ?: return

        val publishedQuery = firestoreLocalized.collection(getString(R.string.resources))
                .whereEqualTo(getString(R.string.author_id), userId)
                .whereEqualTo(getString(R.string.status), ResourceStatus.PUBLISHED)

        val adapterOptions = FirestoreRecyclerOptions.Builder<ResourceHeader>()
                .setQuery(publishedQuery, ResourceHeader::class.java).build()

        val adapter = PublishedSubmissionHeaderAdapter(adapterOptions, this, activity)
        adapter.startListening()

        recyclerView.adapter = adapter
    }

}