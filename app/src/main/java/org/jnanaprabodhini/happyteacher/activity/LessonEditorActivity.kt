package org.jnanaprabodhini.happyteacher.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.helper.ItemTouchHelper
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.activity_card_list_content_viewer.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.contentlist.ResourceContentRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.contentlist.EditableLessonRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.helper.MovableViewContainer
import org.jnanaprabodhini.happyteacher.adapter.helper.RecyclerVerticalDragHelperCallback
import org.jnanaprabodhini.happyteacher.extension.setTooltip
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.extension.showSnackbar
import org.jnanaprabodhini.happyteacher.extension.showToast
import org.jnanaprabodhini.happyteacher.model.ResourceHeader
import org.jnanaprabodhini.happyteacher.model.ContentCard
import org.jnanaprabodhini.happyteacher.util.ResourceStatus

/**
 * Created by grahamearley on 11/3/17.
 */
class LessonEditorActivity: ResourceContentViewerActivity() {

    companion object {
        fun launch(from: Activity, lessonRef: DocumentReference, resourceHeader: ResourceHeader) {
            val lessonEditorIntent = Intent(from, LessonEditorActivity::class.java)

            lessonEditorIntent.apply {
                putExtra(CONTENT_REF_PATH, lessonRef.path)
                putExtra(HEADER, resourceHeader)
            }
            from.startActivity(lessonEditorIntent)
        }
    }

    override val cardRecyclerAdapter: ResourceContentRecyclerAdapter by lazy {
        val options = FirestoreRecyclerOptions.Builder<ContentCard>()
                .setQuery(cardsRef.orderBy(getString(R.string.order_number)), ContentCard::class.java).build()

        EditableLessonRecyclerAdapter(options, attachmentDestinationDirectory, header.subtopic, this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setSubtitle(R.string.lesson_editor)
        setupFabs()
    }

    override fun initializeRecyclerView() {
        super.initializeRecyclerView()
        val dragCallback = RecyclerVerticalDragHelperCallback(cardRecyclerAdapter as MovableViewContainer)
        val itemTouchHelper = ItemTouchHelper(dragCallback)
        itemTouchHelper.attachToRecyclerView(cardRecyclerView)
    }

    private fun setupFabs() {
        addCardFab.setVisible()
        submitLessonFab.setVisible()

        addCardFab.setTooltip(getString(R.string.add_card))
        submitLessonFab.setTooltip(getString(R.string.submit))

        addCardFab.setOnClickListener {
            addNewCard()
        }

        submitLessonFab.setOnClickListener{
            showSubmitConfirmationDialog()
        }
    }

    private fun showSubmitConfirmationDialog() {
        AlertDialog.Builder(this)
                .setTitle("Submit lesson?")
                // TODO: add details in this message -- user will have to unsubmit lesson to be able to edit it again
                .setMessage("Are you ready to submit this lesson for review? \n\nOnce a lesson is submitted, you will not be able to edit it unless you unsubmit it. Once an editor has reviewed your lesson, it will either be published or you will be requested to make changes.")
                .setPositiveButton(R.string.submit, { dialog, _ ->
                    submit()
                    dialog.dismiss()
                })
                .setNegativeButton(R.string.cancel, { dialog, _ -> dialog.dismiss() })
                .show()
    }

    private fun submit() {
        showToast(getString(R.string.submitting))
        contentRef.update(getString(R.string.status), ResourceStatus.AWAITING_REVIEW)
                .addOnSuccessListener {
                    showToast("Lesson submitted")
                    finish()
                }.addOnFailureListener {
                    showToast(getString(R.string.submission_failed_try_again_later))
                }
    }

    private fun addNewCard() {
        val newCardRef = cardsRef.document()
        val newCard = ContentCard()

        val cardCount = cardRecyclerAdapter.itemCount
        if (cardCount > 0) {
            val lastCard = cardRecyclerAdapter.getItem(cardRecyclerAdapter.itemCount - 1)
            val newCardNumber = lastCard.orderNumber + 1
            newCard.orderNumber = newCardNumber
        }

        CardEditorActivity.launch(this, newCardRef, newCard, contentRef.id)
    }

    override fun finish() {
        if (cardRecyclerAdapter.itemCount == 0) {
            // Ask if user wants to delete empty lesson
            AlertDialog.Builder(this)
                    .setTitle(R.string.delete_empty_lesson_question)
                    .setMessage(R.string.there_are_no_cards_in_this_lesson_would_you_like_to_delete_it)
                    .setPositiveButton(R.string.yes, { _, _ ->
                        contentRef.delete()
                        super.finish()
                    })
                    .setNegativeButton(R.string.no, { _, _ -> super.finish()})
                    .show()
        } else {
            super.finish()
        }
    }
}

