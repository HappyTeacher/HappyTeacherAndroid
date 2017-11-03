package org.jnanaprabodhini.happyteacher.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_recycler.*

import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.firestore.LessonHeaderRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader

open class RecyclerFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater?.inflate(R.layout.fragment_recycler, container, false)
    }

    fun <VH: RecyclerView.ViewHolder> initializeRecycler(adapter: RecyclerView.Adapter<VH>) {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

}

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

        val shouldShowSubmissionsCount = false
        val dummyObserver = object: FirebaseDataObserver {}
        val adapter = LessonHeaderRecyclerAdapter("TODO: make real adapter..", shouldShowSubmissionsCount, adapterOptions, activity, dummyObserver)
        adapter.startListening()

        recyclerView.adapter = adapter
    }
}