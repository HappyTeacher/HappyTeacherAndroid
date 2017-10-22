package org.jnanaprabodhini.happyteacher.adapter.viewholder

import android.app.Activity
import android.view.View
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.CardListContentViewerActivity
import org.jnanaprabodhini.happyteacher.activity.SubtopicSubmissionsListActivity
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader
import java.text.DateFormat

class LessonHeaderViewHolder(itemView: View): CardListHeaderViewHolder(itemView) {
    override fun launchContentViewerActivity(activity: Activity, contentId: String, subtopicId: String, subjectName: String, topicName: String, topicId: String, subtopicName: String, subtopicSubmissionCount: Int) {
        CardListContentViewerActivity.launchLessonViewerActivity(activity, contentId, subtopicId, subjectName, topicName, topicId, subtopicName, subtopicSubmissionCount)
    }

    override fun populateView(cardListContentHeaderModel: CardListContentHeader?, topicName: String, activity: Activity, dateFormat: DateFormat) {
        super.populateView(cardListContentHeaderModel, topicName, activity, dateFormat)

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