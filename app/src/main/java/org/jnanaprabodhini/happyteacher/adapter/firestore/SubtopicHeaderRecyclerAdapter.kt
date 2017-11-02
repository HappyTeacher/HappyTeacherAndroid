package org.jnanaprabodhini.happyteacher.adapter.firestore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.adapter.viewholder.SubtopicViewHolder
import org.jnanaprabodhini.happyteacher.model.Subtopic

class SubtopicHeaderRecyclerAdapter(options: FirestoreRecyclerOptions<Subtopic>, firebaseDataObserver: FirebaseDataObserver):
        FirestoreObserverRecyclerAdapter<Subtopic, SubtopicViewHolder>(options, firebaseDataObserver) {

    override fun onBindViewHolder(holder: SubtopicViewHolder, position: Int, model: Subtopic?) {
        holder.populateView(model?.name.orEmpty())
    }

    private fun inflateView(parent: ViewGroup?): View {
        return LayoutInflater.from(parent?.context)
                .inflate(R.layout.list_item_subtopic_header_card, parent, false)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SubtopicViewHolder {
        return SubtopicViewHolder(inflateView(parent))
    }

}