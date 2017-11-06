package org.jnanaprabodhini.happyteacher.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.activity_card_list_content_viewer.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.contentlist.CardListContentRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.contentlist.EditableLessonRecyclerAdapter
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.extension.showToast
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader
import org.jnanaprabodhini.happyteacher.model.ContentCard

/**
 * Created by grahamearley on 11/3/17.
 */
class LessonEditorActivity: CardListContentViewerActivity() {

    companion object {
        fun launch(from: Activity, lessonRef: DocumentReference, cardListContentHeader: CardListContentHeader, topicName: String) {
            val lessonEditorIntent = Intent(from, LessonEditorActivity::class.java)

            lessonEditorIntent.apply {
                putExtra(CONTENT_REF_PATH, lessonRef.path)
                putExtra(HEADER, cardListContentHeader)
                putExtra(TOPIC_NAME, topicName)
            }
            from.startActivity(lessonEditorIntent)
        }
    }

    override val cardRecyclerAdapter: CardListContentRecyclerAdapter by lazy {
        val options = FirestoreRecyclerOptions.Builder<ContentCard>()
                .setQuery(cardsRef.orderBy(getString(R.string.order_number)), ContentCard::class.java).build()

        EditableLessonRecyclerAdapter(options, attachmentDestinationDirectory, header.subtopic, this, this)
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

            val cardCount = cardRecyclerAdapter.itemCount
            if (cardCount > 0) {
                val lastCard = cardRecyclerAdapter.getItem(cardRecyclerAdapter.itemCount - 1)
                val newCardNumber = lastCard.orderNumber + 1
                CardEditorActivity.launch(this, newCardRef, newCardNumber, isNewCard = true)
            } else {
                CardEditorActivity.launch(this, newCardRef, isNewCard = true)
            }
        }
    }

    override fun finish() {
        if (cardRecyclerAdapter.itemCount == 0) {
            // Ask if user wants to delete empty lesson
            AlertDialog.Builder(this)
                    .setTitle(getString(R.string.delete_empty_lesson_question))
                    .setMessage(getString(R.string.there_are_no_cards_in_this_lesson_would_you_like_to_delete_it))
                    .setPositiveButton(getString(R.string.yes), { _, _ ->
                        contentRef.delete()
                        super.finish()
                    })
                    .setNegativeButton(getString(R.string.no), { _, _ -> super.finish()})
                    .show()
        } else {
            super.finish()
        }
    }
}

