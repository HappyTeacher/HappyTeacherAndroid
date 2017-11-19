package org.jnanaprabodhini.happyteacherapp.activity

import android.content.Intent
import android.os.Bundle
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.base.HappyTeacherActivity

class SubmissionsForReviewActivity : HappyTeacherActivity() {

    companion object IntentExtraHelper {
        fun launch(from: HappyTeacherActivity) {
            val intent = Intent(from, SubmissionsForReviewActivity::class.java)
            from.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submissions_for_review)
    }
}
