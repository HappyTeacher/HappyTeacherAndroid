package org.jnanaprabodhini.happyteacher.fragment

import android.support.v7.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_recycler.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.firestore.DraftHeaderRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.firestore.LessonHeaderRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader

class DraftsFragment : RecyclerFragment() {

    override val emptyRecyclerText: String by lazy { getString(R.string.you_have_no_drafts_yet) }
    override val errorText: String by lazy { getString(R.string.there_was_an_error_loading_your_drafts) }

    override fun setupAdapter() {
        val userId = auth.currentUser?.uid ?: return

        val draftQuery = firestoreLocalized.collection(getString(R.string.resources))
                .whereEqualTo(getString(R.string.author_id), userId)
                .whereEqualTo(getString(R.string.status), getString(R.string.status_draft))

        val adapterOptions = FirestoreRecyclerOptions.Builder<CardListContentHeader>()
                .setQuery(draftQuery, CardListContentHeader::class.java).build()

        val adapter = DraftHeaderRecyclerAdapter(adapterOptions, this, activity)
        adapter.startListening()

        recyclerView.adapter = adapter
    }
}