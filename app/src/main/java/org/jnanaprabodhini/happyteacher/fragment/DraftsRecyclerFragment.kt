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

class DraftsRecyclerFragment: RecyclerFragment() {

    val firestoreRoot: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val firestoreUsersCollection: CollectionReference by lazy {
        firestoreRoot.collection(getString(R.string.users))
    }

    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        recyclerView.layoutManager = LinearLayoutManager(context)

        setupAdapter()
    }

    private fun setupAdapter() {
        val draftQuery = firestoreUsersCollection.document(auth.currentUser!!.uid)
                .collection(activity.getString(R.string.drafts_key))

        val adapterOptions = FirestoreRecyclerOptions.Builder<CardListContentHeader>()
                .setQuery(draftQuery, CardListContentHeader::class.java).build()

        val dummyObserver = object: FirebaseDataObserver {} // todo: fragment -> observer
        val adapter = DraftHeaderRecyclerAdapter(adapterOptions, dummyObserver, context)
        adapter.startListening()

        recyclerView.adapter = adapter
    }
}