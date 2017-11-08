package org.jnanaprabodhini.happyteacher.activity

import android.app.Activity
import android.content.Intent
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.activity_card_list_content_viewer.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.contentlist.CardListContentRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.contentlist.ClassroomResourcesRecyclerAdapter
import org.jnanaprabodhini.happyteacher.extension.setDrawableResource
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader
import org.jnanaprabodhini.happyteacher.model.ContentCard

class ClassroomResourceViewerActivity : CardListContentViewerActivity() {

    companion object {
        fun launch(from: Activity, classroomResourceRef: DocumentReference, cardListContentHeader: CardListContentHeader) {
            val classroomResourcesViewerIntent = Intent(from, ClassroomResourceViewerActivity::class.java)

            classroomResourcesViewerIntent.apply {
                putExtra(CONTENT_REF_PATH, classroomResourceRef.path)
                putExtra(HEADER, cardListContentHeader)
            }
            from.startActivity(classroomResourcesViewerIntent)
        }
    }

    override val cardRecyclerAdapter: CardListContentRecyclerAdapter by lazy {
        val options = FirestoreRecyclerOptions.Builder<ContentCard>()
                .setQuery(cardsRef.orderBy(getString(R.string.order_number)), ContentCard::class.java).build()

        ClassroomResourcesRecyclerAdapter(options, attachmentDestinationDirectory, header.subtopic, this, this)
    }

    override fun setHeaderView() {
        super.setHeaderView()

        headerView.setBackgroundResource(R.color.deepGrassGreen)
        icon.setDrawableResource(R.drawable.ic_tv_video_white_24dp)
    }
}