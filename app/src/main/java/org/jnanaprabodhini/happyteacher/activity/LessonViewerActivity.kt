package org.jnanaprabodhini.happyteacher.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_lesson_viewer.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.parent.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.adapter.LessonPlanRecyclerAdapter
import org.jnanaprabodhini.happyteacher.extension.onSingleValueEvent
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.SubtopicLesson

class LessonViewerActivity : HappyTeacherActivity() {

    companion object Constants {
        val WRITE_STORAGE_PERMISSION_CODE = 1

        val LESSON_ID: String = "LESSON_ID"
        fun Intent.hasLessonId(): Boolean = hasExtra(LESSON_ID)
        fun Intent.getLessonId(): String = getStringExtra(LESSON_ID)

        val SUBTOPIC_ID: String = "SUBTOPIC_ID"
        fun Intent.hasSubtopicId(): Boolean = hasExtra(SUBTOPIC_ID)
        fun Intent.getSubtopicId(): String = getStringExtra(SUBTOPIC_ID)

        val SUBJECT: String = "SUBJECT"
        fun Intent.hasSubject(): Boolean = hasExtra(SUBJECT)
        fun Intent.getSubject(): String = getStringExtra(SUBJECT)

        fun Intent.hasAllExtras(): Boolean = hasLessonId() && hasSubtopicId() && hasSubject()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_viewer)

        if (intent.hasLessonId() && intent.hasSubtopicId()) {
            getLessonFromDatabase()
        } else {
            showErrorToastAndFinish()
        }

    }

    private fun getLessonFromDatabase() {
        progressBar.setVisible()

        val lessonId = intent.getLessonId()
        val subtopicId = intent.getSubtopicId()
        val subject = intent.getSubject()

        val lessonQuery = databaseReference.child(getString(R.string.subtopic_lessons))
                                            .child(subtopicId)
                                            .child(lessonId)

        lessonQuery.onSingleValueEvent { dataSnapshot ->
            val lesson = dataSnapshot?.getValue(SubtopicLesson::class.java)
            initializeUiForLesson(lesson, subject)
        }
    }

    private fun initializeUiForLesson(lesson: SubtopicLesson?, subject: String) {
        progressBar.setVisibilityGone()
        setHeaderViewForLesson(lesson, subject)
        initializeRecyclerView(lesson)
    }

    private fun setHeaderViewForLesson(lesson: SubtopicLesson?, subject: String) {
        headerView.setVisible()
        supportActionBar?.title = lesson?.name

        val authorName = lesson?.authorName
        val institutionName = lesson?.authorInstitution
        val location = lesson?.authorLocation

        subjectTextView.text = subject
        authorNameTextView.text = authorName
        institutionTextView.text = institutionName
        locationTextView.text = location
    }

    private fun initializeRecyclerView(lesson: SubtopicLesson?) {
        lessonPlanRecyclerView.layoutManager = LinearLayoutManager(this)

        if (lesson == null) {
            showErrorToastAndFinish()
        } else {
            lessonPlanRecyclerView?.adapter = LessonPlanRecyclerAdapter(lesson.getLessonCards(), this)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Make "Up" button go Back
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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
