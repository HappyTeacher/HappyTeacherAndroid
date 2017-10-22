package org.jnanaprabodhini.happyteacher.adapter.viewholder

import android.app.Activity
import android.view.View
import com.google.firebase.firestore.CollectionReference
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.CardListContentViewerActivity
import org.jnanaprabodhini.happyteacher.activity.SubtopicSubmissionsListActivity
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader
import java.text.DateFormat

class LessonHeaderViewHolder(itemView: View): CardListHeaderViewHolder(itemView) {
    override fun launchContentViewerActivity(activity: Activity, cardRef: CollectionReference, cardListContentHeaderModel: CardListContentHeader?, topicName: String) {
        CardListContentViewerActivity.launchLessonViewerActivity(activity, cardRef, cardListContentHeaderModel ?: CardListContentHeader(), topicName)
    }

    override fun populateView(cardListContentHeaderModel: CardListContentHeader?, cardRef: CollectionReference, topicName: String, activity: Activity, dateFormat: DateFormat) {
        super.populateView(cardListContentHeaderModel, cardRef, topicName, activity, dateFormat)

        cardListContentHeaderModel?.let {
            if (cardListContentHeaderModel.subtopicSubmissionCount > 1) {
                submissionCountTextView.setVisible()
                submissionCountTextView.text = activity.getString(R.string.plus_number, cardListContentHeaderModel.subtopicSubmissionCount - 1) // subtract one to exclude the featured contentKey
                submissionCountTextView.setOnClickListener {
                    SubtopicSubmissionsListActivity.launchActivity(activity, topicName, cardListContentHeaderModel.subtopic, cardListContentHeaderModel.topic)
                } // todo: ^ make submission list work!
            } else {
                submissionCountTextView.setVisibilityGone()
            }
        }
    }
}