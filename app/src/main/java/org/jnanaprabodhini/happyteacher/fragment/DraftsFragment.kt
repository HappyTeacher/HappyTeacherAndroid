package org.jnanaprabodhini.happyteacher.fragment

import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.fragment_recycler.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.firestore.DraftHeaderRecyclerAdapter
import org.jnanaprabodhini.happyteacher.model.ResourceHeader
import org.jnanaprabodhini.happyteacher.util.ResourceStatus

class DraftsFragment : RecyclerFragment() {

    override val emptyRecyclerText: String by lazy { getString(R.string.you_have_no_drafts_yet) }
    override val errorText: String by lazy { getString(R.string.there_was_an_error_loading_your_drafts) }

    override fun setupAdapter() {
        val userId = auth.currentUser?.uid ?: return

        val draftQuery = firestoreLocalized.collection(getString(R.string.resources))
                .whereEqualTo(getString(R.string.author_id), userId)
                .whereEqualTo(getString(R.string.status), ResourceStatus.DRAFT)

        val adapterOptions = FirestoreRecyclerOptions.Builder<ResourceHeader>()
                .setQuery(draftQuery, ResourceHeader::class.java).build()

        val adapter = DraftHeaderRecyclerAdapter(adapterOptions, this, activity)
        adapter.startListening()

        recyclerView.adapter = adapter
    }
}