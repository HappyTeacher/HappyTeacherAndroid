package org.jnanaprabodhini.happyteacher.adapter.viewholder

import android.app.Activity
import android.view.View
import com.google.firebase.firestore.DocumentReference
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.LessonViewerActivity
import org.jnanaprabodhini.happyteacher.activity.SubtopicSubmissionsListActivity
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader
import java.text.DateFormat

class LessonHeaderViewHolder(itemView: View, private val shouldShowSubmissionCount: Boolean): CardListHeaderViewHolder(itemView) {
    override fun launchContentViewerActivity(activity: Activity, contentDocumentRef: DocumentReference, cardListContentHeaderModel: CardListContentHeader?) {
        LessonViewerActivity.launch(activity, contentDocumentRef, cardListContentHeaderModel ?: CardListContentHeader(), shouldShowSubmissionCount)
    }

    override fun populateView(cardListContentHeaderModel: CardListContentHeader?, contentDocumentRef: DocumentReference, activity: Activity, dateFormat: DateFormat) {
        super.populateView(cardListContentHeaderModel, contentDocumentRef, activity, dateFormat)

        if (shouldShowSubmissionCount && cardListContentHeaderModel != null
                && cardListContentHeaderModel.subtopicSubmissionCount > 1) {
            submissionCountTextView.setVisible()
            submissionCountTextView.text = activity.getString(R.string.plus_number, cardListContentHeaderModel.subtopicSubmissionCount - 1) // subtract one to exclude the featured contentKey
            submissionCountTextView.setOnClickListener {
                SubtopicSubmissionsListActivity.launch(activity, cardListContentHeaderModel.subtopic)
            }
        } else {
            submissionCountTextView.setVisibilityGone()
        }
    }
}