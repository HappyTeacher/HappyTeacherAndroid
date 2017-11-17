package org.jnanaprabodhini.happyteacher.adapter.viewholder

import android.app.Activity
import android.view.View
import com.google.firebase.firestore.DocumentReference
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.LessonViewerActivity
import org.jnanaprabodhini.happyteacher.activity.SubtopicSubmissionsListActivity
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.ResourceHeader
import java.text.DateFormat

class LessonHeaderViewHolder(itemView: View, private val shouldShowSubmissionCount: Boolean): ResourceHeaderViewHolder(itemView) {
    override fun launchContentViewerActivity(activity: Activity, contentDocumentRef: DocumentReference, resourceHeaderModel: ResourceHeader?) {
        LessonViewerActivity.launch(activity, contentDocumentRef, resourceHeaderModel ?: ResourceHeader(), shouldShowSubmissionCount)
    }

    override fun populateView(resourceHeaderModel: ResourceHeader?, contentDocumentRef: DocumentReference, activity: Activity, dateFormat: DateFormat) {
        super.populateView(resourceHeaderModel, contentDocumentRef, activity, dateFormat)

        if (shouldShowSubmissionCount && resourceHeaderModel != null
                && resourceHeaderModel.subtopicSubmissionCount > 1) {
            submissionCountTextView.setVisible()
            submissionCountTextView.text = activity.getString(R.string.plus_number, resourceHeaderModel.subtopicSubmissionCount - 1) // subtract one to exclude the featured contentKey
            submissionCountTextView.setOnClickListener {
                SubtopicSubmissionsListActivity.launch(activity, resourceHeaderModel.subtopic)
            }
        } else {
            submissionCountTextView.setVisibilityGone()
        }
    }
}