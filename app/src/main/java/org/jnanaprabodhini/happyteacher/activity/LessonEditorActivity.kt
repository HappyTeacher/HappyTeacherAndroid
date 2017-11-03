package org.jnanaprabodhini.happyteacher.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference
import kotlinx.android.synthetic.main.activity_card_list_content_viewer.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.contentlist.CardEditorActivity
import org.jnanaprabodhini.happyteacher.adapter.contentlist.CardListContentRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.contentlist.EditableLessonRecyclerAdapter
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader
import org.jnanaprabodhini.happyteacher.model.ContentCard

/**
 * Created by grahamearley on 11/3/17.
 */
class LessonEditorActivity: CardListContentViewerActivity() {

    companion object {
        fun launch(from: Activity, cardRef: CollectionReference, cardListContentHeader: CardListContentHeader, topicName: String) {
            val lessonEditorIntent = Intent(from, LessonEditorActivity::class.java)

            lessonEditorIntent.apply {
                putExtra(CARDS_REF_PATH, cardRef.path)
                putExtra(HEADER, cardListContentHeader)
                putExtra(TOPIC_NAME, topicName)
            }
            from.startActivity(lessonEditorIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setSubtitle(R.string.lesson_editor)
        setupFab()
    }

    private fun setupFab() {
        fab.setVisible()

        fab.setOnClickListener {
            val newCardRef = cardsRef.document()
            CardEditorActivity.launch(this, newCardRef)
        }
    }

    override fun getCardRecyclerAdapter(): CardListContentRecyclerAdapter {
        val options = FirestoreRecyclerOptions.Builder<ContentCard>()
                .setQuery(cardsRef.orderBy(getString(R.string.order_number)), ContentCard::class.java).build()
        return EditableLessonRecyclerAdapter(options, attachmentDestinationDirectory, header.subtopic, this, this)
    }

}

