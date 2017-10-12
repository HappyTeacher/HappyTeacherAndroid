package org.jnanaprabodhini.happyteacher.adapter.viewholder

import android.app.Activity
import android.view.View
import org.jnanaprabodhini.happyteacher.activity.CardListContentViewerActivity

class ClassroomResourcesHeaderViewHolder(itemView: View): CardListHeaderViewHolder(itemView) {
    override fun launchContentViewerActivity(activity: Activity, contentId: String, subtopicId: String, subjectName: String, topicName: String, topicId: String, subtopicName: String, subtopicSubmissionCount: Int) {
        CardListContentViewerActivity.launchClassroomResourcesActivity(activity, contentId, subtopicId, subjectName, topicName, topicId, subtopicName, subtopicSubmissionCount)
    }
}