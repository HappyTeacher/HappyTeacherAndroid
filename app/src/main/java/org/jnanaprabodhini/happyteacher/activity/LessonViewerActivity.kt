package org.jnanaprabodhini.happyteacher.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_lesson_viewer.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.parent.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.extension.onSingleValueEvent
import org.jnanaprabodhini.happyteacher.model.SubtopicLesson

class LessonViewerActivity : HappyTeacherActivity() {

    companion object IntentExtraHelper {
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
            // TODO: Show error -- lesson extras weren't passed properly
        }

    }

    private fun getLessonFromDatabase() {
        // TODO: Progress bar until the lesson is received.

        val lessonId = intent.getLessonId()
        val subtopicId = intent.getSubtopicId()
        val subject = intent.getSubject()

        val lessonQuery = databaseReference.child("subtopic_lessons")
                                            .child(subtopicId)
                                            .child(lessonId)

        lessonQuery.onSingleValueEvent { dataSnapshot ->
            val lesson = dataSnapshot?.getValue(SubtopicLesson::class.java)
            initializeUiForLesson(lesson, subject)
        }
    }

    private fun initializeUiForLesson(lesson: SubtopicLesson?, subject: String) {
        setHeaderViewForLesson(lesson, subject)
        initializeRecyclerView(lesson)
    }

    private fun setHeaderViewForLesson(lesson: SubtopicLesson?, subject: String) {
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
        // todo
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
}
