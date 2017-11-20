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
import com.google.firebase.firestore.DocumentReference
import org.jnanaprabodhini.happyteacherapp.activity.ClassroomResourceViewerActivity
import org.jnanaprabodhini.happyteacherapp.activity.LessonViewerActivity
import org.jnanaprabodhini.happyteacherapp.util.ResourceStatus
import org.jnanaprabodhini.happyteacherapp.util.ResourceType


/**
 * An adapter for a list of cards showing header information for resources (lessons, classroom resources).
 */
class ResourceHeaderRecyclerAdapter(options: FirestoreRecyclerOptions<ResourceHeader>,
                                    private val showSubmissionCount: Boolean,
                                    val activity: Activity,
                                    firebaseDataObserver: FirebaseDataObserver):
        FirestoreObserverRecyclerAdapter<ResourceHeader, ResourceHeaderViewHolder>(options, firebaseDataObserver) {

    private val dateFormat by lazy {
        DateFormat.getDateFormat(activity)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = ResourceHeaderViewHolder(inflateView(parent))

    override fun onBindViewHolder(holder: ResourceHeaderViewHolder, position: Int, model: ResourceHeader?) {
        val contentRef = snapshots.getSnapshot(position).reference
        holder.populateView(model, contentRef, activity, dateFormat, showSubmissionCount)

        holder.itemView.setOnClickListener {
            launchContentViewerActivity(contentRef, model)
        }
    }

    private fun launchContentViewerActivity(contentDocumentRef: DocumentReference, resourceHeaderModel: ResourceHeader?) {
        when (resourceHeaderModel?.resourceType) {
            ResourceType.LESSON -> LessonViewerActivity.launch(activity, contentDocumentRef, resourceHeaderModel, showSubmissionCount)
            ResourceType.CLASSROOM_RESOURCE -> ClassroomResourceViewerActivity.launch(activity, contentDocumentRef, resourceHeaderModel)
        }
    }

    private fun inflateView(parent: ViewGroup?): View {
        return LayoutInflater.from(parent?.context)
                .inflate(R.layout.list_item_resource_header_card, parent, false)
    }

}