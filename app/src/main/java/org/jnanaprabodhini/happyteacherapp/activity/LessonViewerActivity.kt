package org.jnanaprabodhini.happyteacherapp.activity

import android.app.Activity
import android.content.Intent
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.android.synthetic.main.activity_card_list_content_viewer.*
import kotlinx.android.synthetic.main.view_recycler_horizontal_pager.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.adapter.contentlist.ResourceContentRecyclerAdapter
import org.jnanaprabodhini.happyteacherapp.adapter.contentlist.LessonPlanRecyclerAdapter
import org.jnanaprabodhini.happyteacherapp.extension.setDrawableRight
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.model.ResourceHeader
import org.jnanaprabodhini.happyteacherapp.model.ContentCard

class LessonViewerActivity : ResourceContentViewerActivity(){

    companion object {
        fun launch(from: Activity, lessonRef: DocumentReference, resourceHeader: ResourceHeader, showSubmissionCount: Boolean) {
            val lessonViewerIntent = Intent(from, LessonViewerActivity::class.java)

            lessonViewerIntent.apply {
                putExtra(CONTENT_REF_PATH, lessonRef.path)
                putExtra(HEADER, resourceHeader)
                putExtra(SHOW_SUBMISSION_COUNT, showSubmissionCount)
            }
            from.startActivity(lessonViewerIntent)
        }

        private const val SHOW_SUBMISSION_COUNT: String = "SHOW_SUBMISSION_COUNT"
        fun Intent.shouldShowSubmissionCount(): Boolean = getBooleanExtra(SHOW_SUBMISSION_COUNT, false)
    }

    private val shouldShowSubmissionCount by lazy { intent.shouldShowSubmissionCount() }

    override val cardRecyclerAdapter: ResourceContentRecyclerAdapter by lazy {
        val options = FirestoreRecyclerOptions.Builder<ContentCard>()
                .setQuery(cardsRef.orderBy(getString(R.string.order_number)), ContentCard::class.java).build()

        LessonPlanRecyclerAdapter(options, attachmentDestinationDirectory, header.topicName, header.subtopic, this, this)
    }

    override fun setHeaderView() {
        super.setHeaderView()

        if (shouldShowSubmissionCount && header.subtopicSubmissionCount > 1) {
            otherSubmissionsTextView.setVisible()
            otherSubmissionsTextView.text = getString(R.string.see_all_n_lesson_plans_for_lesson, header.subtopicSubmissionCount, header.name)
            otherSubmissionsTextView.setDrawableRight(R.drawable.ic_keyboard_arrow_right_white_24dp)
            otherSubmissionsTextView.setOnClickListener {
                SubtopicLessonListActivity.launch(this, header.subtopic)
            }
        } else {
            otherSubmissionsTextView.setVisibilityGone()
        }
    }

    override fun onError(e: FirebaseFirestoreException?) {
        recyclerView.setVisibilityGone()
        statusTextView.setVisible()
        statusTextView.setText(R.string.there_was_an_error_loading_this_lesson)
    }
}