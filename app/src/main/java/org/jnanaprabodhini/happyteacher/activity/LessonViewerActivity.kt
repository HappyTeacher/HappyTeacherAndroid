package org.jnanaprabodhini.happyteacher.activity

import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_card_list_content_viewer.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.contentlist.CardListContentRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.contentlist.LessonPlanRecyclerAdapter
import org.jnanaprabodhini.happyteacher.extension.setDrawableRight
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.CardListContent
import org.jnanaprabodhini.happyteacher.model.ContentCard
import java.io.File

class LessonViewerActivity : CardListContentViewerActivity() {

    override fun getContentRef(): DatabaseReference = databaseReference.child(getString(R.string.subtopic_lessons))
                                                                        .child(topicId)
                                                                        .child(subtopicId)
                                                                        .child(contentId)

    override fun setHeaderViewForContent(content: CardListContent?) {
        super.setHeaderViewForContent(content)

        if (submissionCount > 1) {
            otherSubmissionsTextView.setVisible()
            otherSubmissionsTextView.text = getString(R.string.see_all_n_lesson_plans_for_lesson, submissionCount, content?.name)
            otherSubmissionsTextView.setDrawableRight(R.drawable.ic_keyboard_arrow_right_white_24dp)
            otherSubmissionsTextView.setOnClickListener {
                SubtopicSubmissionsListActivity.launchActivity(this, topicName, subtopicId, topicId)
            }
        } else {
            otherSubmissionsTextView.setVisibilityGone()
        }
    }

    override fun getCardRecyclerAdapter(cards: Map<String, ContentCard>, attachmentDestinationDirectory: File): CardListContentRecyclerAdapter {
        return LessonPlanRecyclerAdapter(cards, attachmentDestinationDirectory, topicName, topicId, subtopicId, this)
    }
}