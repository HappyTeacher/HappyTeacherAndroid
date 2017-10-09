package org.jnanaprabodhini.happyteacher.adapter.viewholder

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_lesson_header.view.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.LessonViewerActivity
import org.jnanaprabodhini.happyteacher.extension.setDrawableLeft
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.SubtopicLessonHeader
import java.text.DateFormat
import java.util.*

/**
 * Created by grahamearley on 9/12/17.
 */
class SubtopicHeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val lessonTitleTextView: TextView = itemView.lessonTitleTextView
    val authorNameTextView: TextView = itemView.authorNameTextView
    val institutionTextView: TextView = itemView.institutionTextView
    val locationTextView: TextView = itemView.locationTextView
    val dateEditedTextView: TextView = itemView.dateEditedTextView
    val submissionCountTextView: TextView = itemView.submissionCountTextView

    fun populateView(subtopicHeaderModel: SubtopicLessonHeader?, topicName: String, activity: Activity, dateFormat: DateFormat) {
        lessonTitleTextView.text = subtopicHeaderModel?.name
        authorNameTextView.text = subtopicHeaderModel?.authorName
        institutionTextView.text = subtopicHeaderModel?.authorInstitution
        locationTextView.text = subtopicHeaderModel?.authorLocation

        authorNameTextView.setDrawableLeft(R.drawable.ic_person_accent)
        institutionTextView.setDrawableLeft(R.drawable.ic_school_accent)
        locationTextView.setDrawableLeft(R.drawable.ic_location_accent)

        subtopicHeaderModel?.let {
            dateEditedTextView.text = dateFormat.format(Date(subtopicHeaderModel.dateEdited))
            dateEditedTextView.setDrawableLeft(R.drawable.ic_clock_light_gray)

            if (subtopicHeaderModel.subtopicSubmissionCount > 1) {
                submissionCountTextView.setVisible()
                submissionCountTextView.text = activity.getString(R.string.plus_number, subtopicHeaderModel.subtopicSubmissionCount - 1) // subtract one to exclude the featured lesson
            } else {
                submissionCountTextView.setVisibilityGone()
            }
        }

        itemView.setOnClickListener {
            val lessonId = subtopicHeaderModel?.lesson
            val subtopicId = subtopicHeaderModel?.subtopic
            val subjectName = subtopicHeaderModel?.subjectName
            val topicId = subtopicHeaderModel?.topic

            val lessonViewerIntent = Intent(activity, LessonViewerActivity::class.java)

            lessonViewerIntent.apply {
                putExtra(LessonViewerActivity.LESSON_ID, lessonId)
                putExtra(LessonViewerActivity.SUBTOPIC_ID, subtopicId)
                putExtra(LessonViewerActivity.SUBJECT, subjectName)
                putExtra(LessonViewerActivity.TOPIC_NAME, topicName)
                putExtra(LessonViewerActivity.TOPIC_ID, topicId)
                putExtra(LessonViewerActivity.SUBTOPIC_NAME, subtopicHeaderModel?.name)
                putExtra(LessonViewerActivity.SUBMISSION_COUNT, subtopicHeaderModel?.subtopicSubmissionCount)
            }

            activity.startActivity(lessonViewerIntent)
        }
    }
}