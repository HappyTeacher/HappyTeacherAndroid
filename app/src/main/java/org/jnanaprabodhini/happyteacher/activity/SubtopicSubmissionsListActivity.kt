package org.jnanaprabodhini.happyteacher.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.format.DateFormat
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_subtopic_submissions_list.*

import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.parent.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.adapter.FirebaseObserverRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.adapter.viewholder.SubtopicHeaderViewHolder
import org.jnanaprabodhini.happyteacher.extension.setDrawableLeft
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.extension.showToast
import org.jnanaprabodhini.happyteacher.model.SubtopicLessonHeader
import java.util.*

class SubtopicSubmissionsListActivity : HappyTeacherActivity(), FirebaseDataObserver {

    private val dateFormat by lazy {
        DateFormat.getDateFormat(this)
    }

    companion object IntentExtraHelper {
        val TOPIC_KEY: String = "TOPIC_KEY"
        fun Intent.hasTopicKey(): Boolean = hasExtra(TOPIC_KEY)
        fun Intent.getTopicKey(): String = getStringExtra(TOPIC_KEY)

        val SUBTOPIC_KEY: String = "SUBTOPIC_KEY"
        fun Intent.hasSubtopicKey(): Boolean = hasExtra(SUBTOPIC_KEY)
        fun Intent.getSubtopicKey(): String = getStringExtra(SUBTOPIC_KEY)

        val TOPIC_NAME: String = "TOPIC_NAME"
        fun Intent.hasTopicName(): Boolean = hasExtra(TOPIC_NAME)
        fun Intent.getTopicName(): String = getStringExtra(TOPIC_NAME)

        fun Intent.hasAllExtras(): Boolean = hasTopicKey() && hasSubtopicKey() && hasTopicName()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subtopic_submissions_list)

        if (intent.hasAllExtras()) {
            initializeRecyclerViewForSubtopic(intent.getTopicKey(), intent.getSubtopicKey(), intent.getTopicName())
        } else {
            // todo: log error
            showToast(R.string.error_loading_other_lessons)
            finish()
        }
    }

    fun initializeRecyclerViewForSubtopic(topicKey: String, subtopicKey: String, topicName: String) {

        val submissionHeadersQuery = databaseReference.child(getString(R.string.subtopic_lesson_headers))
                                                        .child(topicKey)
                                                        .child(subtopicKey)

        val adapter = object: FirebaseObserverRecyclerAdapter<SubtopicLessonHeader, SubtopicHeaderViewHolder>(SubtopicLessonHeader::class.java, R.layout.list_item_lesson_header, SubtopicHeaderViewHolder::class.java, submissionHeadersQuery, this) {
            override fun populateViewHolder(subtopicHeaderViewHolder: SubtopicHeaderViewHolder?, subtopicHeaderModel: SubtopicLessonHeader?, lessonHeaderPosition: Int) {
                populateLessonHeaderViewHolder(subtopicHeaderViewHolder, subtopicHeaderModel, topicName)
            }
        }

        submissionRecyclerView.layoutManager = LinearLayoutManager(this)
        submissionRecyclerView.adapter = adapter
    }

    private fun  populateLessonHeaderViewHolder(subtopicHeaderViewHolder: SubtopicHeaderViewHolder?, subtopicHeaderModel: SubtopicLessonHeader?, topicName: String) {
        subtopicHeaderViewHolder?.lessonTitleTextView?.text = subtopicHeaderModel?.name
        subtopicHeaderViewHolder?.authorNameTextView?.text = subtopicHeaderModel?.authorName
        subtopicHeaderViewHolder?.institutionTextView?.text = subtopicHeaderModel?.authorInstitution
        subtopicHeaderViewHolder?.locationTextView?.text = subtopicHeaderModel?.authorLocation

        subtopicHeaderViewHolder?.authorNameTextView?.setDrawableLeft(R.drawable.ic_person_accent)
        subtopicHeaderViewHolder?.institutionTextView?.setDrawableLeft(R.drawable.ic_school_accent)
        subtopicHeaderViewHolder?.locationTextView?.setDrawableLeft(R.drawable.ic_location_accent)

        subtopicHeaderModel?.let {
            subtopicHeaderViewHolder?.dateEditedTextView?.text = dateFormat.format(Date(subtopicHeaderModel.dateEdited))
            subtopicHeaderViewHolder?.dateEditedTextView?.setDrawableLeft(R.drawable.ic_clock_light_gray)

            if (subtopicHeaderModel.subtopicSubmissionCount > 1) {
                subtopicHeaderViewHolder?.submissionCountTextView?.setVisible()
                subtopicHeaderViewHolder?.submissionCountTextView?.text = getString(R.string.plus_number, subtopicHeaderModel.subtopicSubmissionCount - 1) // subtract one to exclude the featured lesson
            } else {
                subtopicHeaderViewHolder?.submissionCountTextView?.setVisibilityGone()
            }
        }

        subtopicHeaderViewHolder?.itemView?.setOnClickListener {
            val lessonId = subtopicHeaderModel?.lesson
            val subtopicId = subtopicHeaderModel?.subtopic
            val subjectName = subtopicHeaderModel?.subjectName
            val topicId = subtopicHeaderModel?.topic

            val lessonViewerIntent = Intent(this, LessonViewerActivity::class.java)

            lessonViewerIntent.apply {
                putExtra(LessonViewerActivity.LESSON_ID, lessonId)
                putExtra(LessonViewerActivity.SUBTOPIC_ID, subtopicId)
                putExtra(LessonViewerActivity.SUBJECT, subjectName)
                putExtra(LessonViewerActivity.TOPIC_NAME, topicName)
                putExtra(LessonViewerActivity.TOPIC_ID, topicId)
                putExtra(LessonViewerActivity.SUBTOPIC_NAME, subtopicHeaderModel?.name)
                putExtra(LessonViewerActivity.SUBMISSION_COUNT, subtopicHeaderModel?.subtopicSubmissionCount)
            }

            startActivity(lessonViewerIntent)
        }
    }
}
