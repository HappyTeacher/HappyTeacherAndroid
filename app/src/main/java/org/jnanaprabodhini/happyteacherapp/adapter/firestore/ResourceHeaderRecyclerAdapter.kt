package org.jnanaprabodhini.happyteacherapp.adapter.firestore

import android.app.Activity
import android.text.format.DateFormat
import android.view.ViewGroup
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.adapter.viewholder.ResourceHeaderViewHolder
import org.jnanaprabodhini.happyteacherapp.model.ResourceHeader
import android.view.LayoutInflater
import android.view.View
import com.firebase.ui.firestore.FirestoreRecyclerOptions


/**
 * An adapter for a list of cards showing header information for resources (lessons, classroom resources).
 */
class ResourceHeaderRecyclerAdapter(options: FirestoreRecyclerOptions<ResourceHeader>, private val showSubmissionCount: Boolean, val activity: Activity, firebaseDataObserver: FirebaseDataObserver):
        FirestoreObserverRecyclerAdapter<ResourceHeader, ResourceHeaderViewHolder>(options, firebaseDataObserver) {

    private val dateFormat by lazy {
        DateFormat.getDateFormat(activity)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = ResourceHeaderViewHolder(inflateView(parent))

    override fun onBindViewHolder(holder: ResourceHeaderViewHolder, position: Int, model: ResourceHeader?) {
        val contentRef = snapshots.getSnapshot(position).reference
        holder.populateView(model, contentRef, activity, dateFormat, showSubmissionCount)
    }

    private fun inflateView(parent: ViewGroup?): View {
        return LayoutInflater.from(parent?.context)
                .inflate(R.layout.list_item_resource_header_card, parent, false)
    }

}