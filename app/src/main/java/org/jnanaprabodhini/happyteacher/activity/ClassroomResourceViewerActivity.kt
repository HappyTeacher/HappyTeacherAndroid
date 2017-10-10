package org.jnanaprabodhini.happyteacher.activity

import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_card_list_content_viewer.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.CardListContentRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.ClassroomResourcesRecyclerAdapter
import org.jnanaprabodhini.happyteacher.extension.setDrawableResource
import org.jnanaprabodhini.happyteacher.model.CardListContent
import org.jnanaprabodhini.happyteacher.model.ContentCard
import java.io.File

class ClassroomResourceViewerActivity : CardListContentViewerActivity() {

    override fun getContentRef(): DatabaseReference = databaseReference.child(getString(R.string.classroom_resources_key))
                                                                        .child(topicId)
                                                                        .child(subtopicId)
                                                                        .child(contentId)

    override fun setHeaderViewForContent(content: CardListContent?) {
        super.setHeaderViewForContent(content)

        headerView.setBackgroundResource(R.color.deepGrassGreen)
        icon.setDrawableResource(R.drawable.ic_tv_video_white_24dp)
    }

    override fun getCardRecyclerAdapter(cards: Map<String, ContentCard>, attachmentDestinationDirectory: File): CardListContentRecyclerAdapter {
        return ClassroomResourcesRecyclerAdapter(cards, attachmentDestinationDirectory, topicName, topicId, subtopicId, this)
    }
}