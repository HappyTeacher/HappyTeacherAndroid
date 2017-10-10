package org.jnanaprabodhini.happyteacher.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_lesson_viewer.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.parent.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.adapter.LessonPlanRecyclerAdapter
import org.jnanaprabodhini.happyteacher.extension.*
import org.jnanaprabodhini.happyteacher.model.SubtopicLesson
import java.io.File

class LessonViewerActivity : HappyTeacherActivity() {

    companion object Constants {
        val WRITE_STORAGE_PERMISSION_CODE = 1

        fun launchActivity(from: Activity, lessonId: String, subtopicId: String, subjectName: String, topicName: String, topicId: String, subtopicName: String, submissionCount: Int) {
            val lessonViewerIntent = Intent(from, LessonViewerActivity::class.java)

            lessonViewerIntent.apply {
                putExtra(LessonViewerActivity.LESSON_ID, lessonId)
                putExtra(LessonViewerActivity.SUBTOPIC_ID, subtopicId)
                putExtra(LessonViewerActivity.SUBJECT, subjectName)
                putExtra(LessonViewerActivity.TOPIC_NAME, topicName)
                putExtra(LessonViewerActivity.TOPIC_ID, topicId)
                putExtra(LessonViewerActivity.SUBTOPIC_NAME, subtopicName)
                putExtra(LessonViewerActivity.SUBMISSION_COUNT, submissionCount)
            }

            from.startActivity(lessonViewerIntent)
        }

        val LESSON_ID: String = "LESSON_ID"
        fun Intent.hasLessonId(): Boolean = hasExtra(LESSON_ID)
        fun Intent.getLessonId(): String = getStringExtra(LESSON_ID)

        val SUBTOPIC_ID: String = "SUBTOPIC_ID"
        fun Intent.hasSubtopicId(): Boolean = hasExtra(SUBTOPIC_ID)
        fun Intent.getSubtopicId(): String = getStringExtra(SUBTOPIC_ID)

        val SUBJECT: String = "SUBJECT"
        fun Intent.hasSubject(): Boolean = hasExtra(SUBJECT)
        fun Intent.getSubject(): String = getStringExtra(SUBJECT)

        val TOPIC_NAME: String = "TOPIC_NAME"
        fun Intent.hasTopicName(): Boolean = hasExtra(TOPIC_NAME)
        fun Intent.getTopicName(): String = getStringExtra(TOPIC_NAME)

        val  TOPIC_ID: String = "TOPIC_ID"
        fun Intent.hasTopicId(): Boolean = hasExtra(TOPIC_ID)
        fun Intent.getTopicId(): String = getStringExtra(TOPIC_ID)

        val SUBTOPIC_NAME: String = "SUBTOPIC_NAME"
        fun Intent.hasSubtopicName(): Boolean = hasExtra(SUBTOPIC_NAME)
        fun Intent.getSubtopicName(): String = getStringExtra(SUBTOPIC_NAME)

        val  SUBMISSION_COUNT: String = "SUBMISSION_COUNT"
        fun Intent.hasSubmissionCount(): Boolean = hasExtra(SUBMISSION_COUNT)
        fun Intent.getSubmissionCount(): Int = getIntExtra(SUBMISSION_COUNT, 0)

        fun Intent.hasAllExtras(): Boolean = hasLessonId() && hasSubtopicId() && hasSubject() && hasTopicName() && hasSubtopicName() && hasTopicId() && hasSubmissionCount()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_viewer)

        if (intent.hasAllExtras()) {
            initializeUiForLessonFromDatabase()
        } else {
            showErrorToastAndFinish()
        }

    }

    private fun initializeUiForLessonFromDatabase() {
        progressBar.setVisible()

        val lessonId = intent.getLessonId()
        val subtopicId = intent.getSubtopicId()
        val subject = intent.getSubject()
        val topicName = intent.getTopicName()
        val topicId = intent.getTopicId()
        val subtopicName = intent.getSubtopicName()

        val submissionCount = intent.getSubmissionCount()

        val lessonQuery = databaseReference.child(getString(R.string.subtopic_lessons))
                                            .child(topicId)
                                            .child(subtopicId)
                                            .child(lessonId)

        // This directory will be used to store any attachments downloaded from this lesson.
        val attachmentDestinationDirectory = File(Environment.getExternalStorageDirectory().path
                                                        + File.separator
                                                        + getString(R.string.app_name)
                                                        + File.separator
                                                        + subject + File.separator + topicName + File.separator + subtopicName)


        lessonQuery.onSingleValueEvent { dataSnapshot ->
            val lesson = dataSnapshot?.getValue(SubtopicLesson::class.java)
            initializeUiForLesson(lesson, subject, attachmentDestinationDirectory, submissionCount, topicId, subtopicId, topicName)
        }
    }

    private fun initializeUiForLesson(lesson: SubtopicLesson?, subject: String, attachmentDestinationDirectory: File, submissionCount: Int, topicId: String, subtopicId: String, topicName: String) {
        progressBar.setVisibilityGone()
        setHeaderViewForLesson(lesson, subject, submissionCount, topicId, subtopicId, topicName)
        initializeRecyclerView(lesson, attachmentDestinationDirectory, topicName, topicId, subtopicId)
    }

    private fun setHeaderViewForLesson(lesson: SubtopicLesson?, subject: String, submissionCount: Int, topicId: String, subtopicId: String, topicName: String) {
        headerView.setVisible()
        supportActionBar?.title = lesson?.name

        val authorName = lesson?.authorName
        val institutionName = lesson?.authorInstitution
        val location = lesson?.authorLocation

        subjectTextView.text = subject
        authorNameTextView.text = authorName
        institutionTextView.text = institutionName
        locationTextView.text = location

        if (submissionCount > 1) {
            otherSubmissionsTextView.setVisible()
            otherSubmissionsTextView.text = getString(R.string.see_all_n_lesson_plans_for_lesson, submissionCount, lesson?.name)
            otherSubmissionsTextView.setDrawableRight(R.drawable.ic_keyboard_arrow_right_white_24dp)
            otherSubmissionsTextView.setOnClickListener {
                SubtopicSubmissionsListActivity.launchActivity(this, topicName, subtopicId, topicId)
            }
        } else {
            otherSubmissionsTextView.setVisibilityGone()
        }

    }

    private fun initializeRecyclerView(lesson: SubtopicLesson?, attachmentDestinationDirectory: File, topicName: String, topicId: String, subtopicId: String) {
        lessonPlanRecyclerView.layoutManager = LinearLayoutManager(this)

        if (lesson == null) {
            showErrorToastAndFinish()
        } else {
            lessonPlanRecyclerView?.adapter = LessonPlanRecyclerAdapter(lesson.cards, attachmentDestinationDirectory, topicName, topicId, subtopicId, this)
        }
    }

    private fun showErrorToastAndFinish() {
        // TODO: Log error to analytics.
        Toast.makeText(this, R.string.there_was_an_error_loading_the_lesson, Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == WRITE_STORAGE_PERMISSION_CODE && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            // Re-draw items to reflect new permissions
            lessonPlanRecyclerView.adapter.notifyDataSetChanged()
        }
    }
}
