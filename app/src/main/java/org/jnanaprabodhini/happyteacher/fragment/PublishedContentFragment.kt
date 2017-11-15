package org.jnanaprabodhini.happyteacher.fragment

import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.fragment_recycler.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.firestore.DraftHeaderRecyclerAdapter
import org.jnanaprabodhini.happyteacher.model.ResourceHeader

class PublishedContentFragment : RecyclerFragment() {

    override val emptyRecyclerText: String by lazy { getString(R.string.you_have_no_published_contributions) }
    override val errorText: String by lazy { getString(R.string.there_was_an_error_loading_your_published_contributions) }

    override fun setupAdapter() {
        val userId = auth.currentUser?.uid ?: return

        val publishedQuery = firestoreLocalized.collection(getString(R.string.resources))
                .whereEqualTo(getString(R.string.author_id), userId)
                .whereEqualTo(getString(R.string.status), getString(R.string.status_published))

        val adapterOptions = FirestoreRecyclerOptions.Builder<ResourceHeader>()
                .setQuery(publishedQuery, ResourceHeader::class.java).build()

        // TODO: update adapter!
        val adapter = DraftHeaderRecyclerAdapter(adapterOptions, this, activity)
        adapter.startListening()

        recyclerView.adapter = adapter
    }

}