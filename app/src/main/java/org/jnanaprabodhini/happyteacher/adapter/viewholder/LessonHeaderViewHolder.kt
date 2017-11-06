package org.jnanaprabodhini.happyteacher.adapter.viewholder

import android.app.Activity
import android.view.View
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.CardListContentViewerActivity
import org.jnanaprabodhini.happyteacher.activity.LessonViewerActivity
import org.jnanaprabodhini.happyteacher.activity.SubtopicSubmissionsListActivity
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader
import java.text.DateFormat

class LessonHeaderViewHolder(itemView: View, val shouldShowSubmissionCount: Boolean): CardListHeaderViewHolder(itemView) {
    override fun launchContentViewerActivity(activity: Activity, contentDocumentRef: DocumentReference, cardListContentHeaderModel: CardListContentHeader?, topicName: String) {
        LessonViewerActivity.launch(activity, contentDocumentRef, cardListContentHeaderModel ?: CardListContentHeader(), topicName, shouldShowSubmissionCount)
    }

    override fun populateView(cardListContentHeaderModel: CardListContentHeader?, contentDocumentRef: DocumentReference, topicName: String, activity: Activity, dateFormat: DateFormat) {
        super.populateView(cardListContentHeaderModel, contentDocumentRef, topicName, activity, dateFormat)

        if (shouldShowSubmissionCount && cardListContentHeaderModel != null
                && cardListContentHeaderModel.subtopicSubmissionCount > 1) {
            submissionCountTextView.setVisible()
            submissionCountTextView.text = activity.getString(R.string.plus_number, cardListContentHeaderModel.subtopicSubmissionCount - 1) // subtract one to exclude the featured contentKey
            submissionCountTextView.setOnClickListener {
                SubtopicSubmissionsListActivity.launchActivity(activity, topicName, cardListContentHeaderModel.subtopic)
            }
        } else {
            submissionCountTextView.setVisibilityGone()
        }
    }
}