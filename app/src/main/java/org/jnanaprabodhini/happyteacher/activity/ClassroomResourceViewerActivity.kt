package org.jnanaprabodhini.happyteacher.activity

import android.app.Activity
import android.content.Intent
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference
import kotlinx.android.synthetic.main.activity_card_list_content_viewer.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.contentlist.CardListContentRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.contentlist.ClassroomResourcesRecyclerAdapter
import org.jnanaprabodhini.happyteacher.extension.setDrawableResource
import org.jnanaprabodhini.happyteacher.model.CardListContent
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader
import org.jnanaprabodhini.happyteacher.model.ContentCard
import java.io.File

class ClassroomResourceViewerActivity : CardListContentViewerActivity() {

    companion object {
        fun launch(from: Activity, cardRef: CollectionReference, cardListContentHeader: CardListContentHeader, topicName: String) {
            val classroomResourcesViewerIntent = Intent(from, ClassroomResourceViewerActivity::class.java)

            classroomResourcesViewerIntent.apply {
                putExtra(CARD_REF_PATH, cardRef.path)
                putExtra(HEADER, cardListContentHeader)
                putExtra(TOPIC_NAME, topicName)
            }
            from.startActivity(classroomResourcesViewerIntent)
        }
    }

    override fun setHeaderView() {
        super.setHeaderView()

        headerView.setBackgroundResource(R.color.deepGrassGreen)
        icon.setDrawableResource(R.drawable.ic_tv_video_white_24dp)
    }

    override fun getCardRecyclerAdapter(cardRef: CollectionReference, attachmentDestinationDirectory: File): CardListContentRecyclerAdapter {
        val options = FirestoreRecyclerOptions.Builder<ContentCard>()
                .setQuery(cardRef.orderBy(getString(R.string.order_number)), ContentCard::class.java).build()
        return ClassroomResourcesRecyclerAdapter(options, attachmentDestinationDirectory, header.topic, header.subtopic, this, this)
    }
}