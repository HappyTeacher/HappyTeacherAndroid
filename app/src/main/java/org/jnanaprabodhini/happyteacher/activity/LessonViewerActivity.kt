package org.jnanaprabodhini.happyteacher.activity

import android.content.Intent
import android.os.Bundle
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.parent.HappyTeacherActivity

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
    }
}
