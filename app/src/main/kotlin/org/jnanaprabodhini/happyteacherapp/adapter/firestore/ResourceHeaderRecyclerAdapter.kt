package org.jnanaprabodhini.happyteacherapp.adapter.firestore

import android.app.Activity
import android.text.format.DateFormat
import android.view.ViewGroup
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.adapter.viewholder.ResourceHeaderViewHolder
import org.jnanaprabodhini.happyteacherapp.adapter.viewholder.ClassroomResourcesHeaderViewHolder
import org.jnanaprabodhini.happyteacherapp.adapter.viewholder.LessonHeaderViewHolder
import org.jnanaprabodhini.happyteacherapp.model.ResourceHeader
import android.view.LayoutInflater
import android.view.View
import com.firebase.ui.firestore.FirestoreRecyclerOptions


/**
 * An abstract adapter for a list of cards showing header information for content lists.
 */
abstract class ResourceHeaderRecyclerAdapter<VH: ResourceHeaderViewHolder>(options: FirestoreRecyclerOptions<ResourceHeader>, val activity: Activity, firebaseDataObserver: FirebaseDataObserver):
        FirestoreObserverRecyclerAdapter<ResourceHeader, VH>(options, firebaseDataObserver) {

    private val dateFormat by lazy {
        DateFormat.getDateFormat(activity)
    }

    override fun onBindViewHolder(holder: VH, position: Int, model: ResourceHeader?) {
        val contentRef = snapshots.getSnapshot(position).reference
        holder.populateView(model, contentRef, activity, dateFormat)
    }

    fun inflateView(parent: ViewGroup?): View {
        return LayoutInflater.from(parent?.context)
                .inflate(R.layout.list_item_resource_header_card, parent, false)
    }

}

/**
 * A ResourceHeaderRecyclerAdapter implementation for lessons.
 */
class LessonHeaderRecyclerAdapter(private val shouldShowSubmissionCount: Boolean, options: FirestoreRecyclerOptions<ResourceHeader>, activity: Activity, firebaseDataObserver: FirebaseDataObserver):
        ResourceHeaderRecyclerAdapter<LessonHeaderViewHolder>(options, activity, firebaseDataObserver) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): LessonHeaderViewHolder {
        return LessonHeaderViewHolder(inflateView(parent), shouldShowSubmissionCount)
    }
}

/**
 * A ResourceHeaderRecyclerAdapter implementation for classroom resources.
 */
class ClassroomResourceHeaderRecyclerAdapter(options: FirestoreRecyclerOptions<ResourceHeader>, activity: Activity, firebaseDataObserver: FirebaseDataObserver):
        ResourceHeaderRecyclerAdapter<ClassroomResourcesHeaderViewHolder>(options, activity, firebaseDataObserver) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ClassroomResourcesHeaderViewHolder {
        return ClassroomResourcesHeaderViewHolder(inflateView(parent))
    }
}