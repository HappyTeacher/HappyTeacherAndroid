package org.jnanaprabodhini.happyteacher.activity

import android.content.Intent
import android.os.Bundle
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

        fun Intent.hasAllExtras(): Boolean = hasLessonId() && hasSubtopicId()
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
        val lessonId = intent.getLessonId()
        val subtopicId = intent.getSubtopicId()

        val lessonQuery = databaseReference.child("subtopic_lessons")
                                            .child(subtopicId)
                                            .child(lessonId)

        lessonQuery.onSingleValueEvent { dataSnapshot ->
            val lesson = dataSnapshot?.getValue(SubtopicLesson::class.java)
            initializeUiForLesson(lesson)
        }
    }

    private fun initializeUiForLesson(lesson: SubtopicLesson?) {
        //
    }
}
