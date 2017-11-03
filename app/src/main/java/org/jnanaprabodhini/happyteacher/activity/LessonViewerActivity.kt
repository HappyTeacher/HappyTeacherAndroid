package org.jnanaprabodhini.happyteacher.activity

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference
import kotlinx.android.synthetic.main.activity_card_list_content_viewer.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.contentlist.CardListContentRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.contentlist.LessonPlanRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.extension.setDrawableRight
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.CardListContent
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader
import org.jnanaprabodhini.happyteacher.model.ContentCard
import java.io.File

class LessonViewerActivity : CardListContentViewerActivity(){

    companion object {
        fun launch(from: Activity, cardRef: CollectionReference, cardListContentHeader: CardListContentHeader, topicName: String, shouldShowSubmissionCount: Boolean) {
            val lessonViewerIntent = Intent(from, LessonViewerActivity::class.java)

            lessonViewerIntent.apply {
                putExtra(CARD_REF_PATH, cardRef.path)
                putExtra(HEADER, cardListContentHeader)
                putExtra(TOPIC_NAME, topicName)
                putExtra(SHOW_SUBMISSION_COUNT, shouldShowSubmissionCount)
            }
            from.startActivity(lessonViewerIntent)
        }

        private const val SHOW_SUBMISSION_COUNT: String = "SHOW_SUBMISSION_COUNT"
        fun Intent.shouldShowSubmissionCount(): Boolean = getBooleanExtra(SHOW_SUBMISSION_COUNT, false)
    }

    private val shouldShowSubmissionCount by lazy { intent.shouldShowSubmissionCount() }

    override fun setHeaderView() {
        super.setHeaderView()

        if (shouldShowSubmissionCount && header.subtopicSubmissionCount > 1) {
            otherSubmissionsTextView.setVisible()
            otherSubmissionsTextView.text = getString(R.string.see_all_n_lesson_plans_for_lesson, header.subtopicSubmissionCount, header.name)
            otherSubmissionsTextView.setDrawableRight(R.drawable.ic_keyboard_arrow_right_white_24dp)
            otherSubmissionsTextView.setOnClickListener {
                SubtopicSubmissionsListActivity.launchActivity(this, topicName, header.subtopic)
            }
        } else {
            otherSubmissionsTextView.setVisibilityGone()
        }
    }

    override fun getCardRecyclerAdapter(cardRef: CollectionReference, attachmentDestinationDirectory: File): CardListContentRecyclerAdapter {
        val options = FirestoreRecyclerOptions.Builder<ContentCard>()
                .setQuery(cardRef.orderBy(getString(R.string.order_number)), ContentCard::class.java).build()

        return LessonPlanRecyclerAdapter(options, attachmentDestinationDirectory, topicName, header.topic, header.subtopic, this, this)
    }
}