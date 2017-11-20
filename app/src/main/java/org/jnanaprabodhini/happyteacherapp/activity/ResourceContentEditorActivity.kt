package org.jnanaprabodhini.happyteacherapp.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.helper.ItemTouchHelper
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.activity_card_list_content_viewer.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.adapter.contentlist.ResourceContentRecyclerAdapter
import org.jnanaprabodhini.happyteacherapp.adapter.contentlist.EditableResourceRecyclerAdapter
import org.jnanaprabodhini.happyteacherapp.adapter.helper.MovableViewContainer
import org.jnanaprabodhini.happyteacherapp.adapter.helper.RecyclerVerticalDragHelperCallback
import org.jnanaprabodhini.happyteacherapp.extension.setTooltip
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.extension.showToast
import org.jnanaprabodhini.happyteacherapp.model.ResourceHeader
import org.jnanaprabodhini.happyteacherapp.model.ContentCard
import org.jnanaprabodhini.happyteacherapp.util.FirestoreKeys
import org.jnanaprabodhini.happyteacherapp.util.ResourceStatus
import org.jnanaprabodhini.happyteacherapp.util.ResourceType

/**
 * Created by grahamearley on 11/3/17.
 */
class ResourceContentEditorActivity : ResourceContentActivity() {

    companion object {
        fun launch(from: Activity, resourceRef: DocumentReference, resourceHeader: ResourceHeader) {
            val lessonEditorIntent = Intent(from, ResourceContentEditorActivity::class.java)

            lessonEditorIntent.apply {
                putExtra(CONTENT_REF_PATH, resourceRef.path)
                putExtra(HEADER, resourceHeader)
            }
            from.startActivity(lessonEditorIntent)
        }
    }

    override val cardRecyclerAdapter: ResourceContentRecyclerAdapter by lazy {
        val options = FirestoreRecyclerOptions.Builder<ContentCard>()
                .setQuery(cardsRef.orderBy(getString(R.string.order_number)), ContentCard::class.java).build()

        EditableResourceRecyclerAdapter(options, attachmentDestinationDirectory, header.subtopic, this, this)
    }

    // Dialog text (depending on resource type):
    private val confirmationDialogTitle by lazy {
        when (header.resourceType) {
            ResourceType.LESSON -> R.string.submit_lesson_question
            ResourceType.CLASSROOM_RESOURCE -> R.string.submit_classroom_resource_question
            else -> R.string.submit_question
        }
    }

    private val confirmationDialogMessage by lazy {
        when (header.resourceType) {
            ResourceType.LESSON -> R.string.submit_lesson_confirmation_message
            ResourceType.CLASSROOM_RESOURCE -> R.string.submit_classroom_resource_confirmation_message
            else -> R.string.submit_resource_confirmation_message
        }
    }

    private val resourceSubmittedMessage by lazy {
        when (header.resourceType) {
            ResourceType.LESSON -> R.string.lesson_submitted
            ResourceType.CLASSROOM_RESOURCE -> R.string.classroom_resource_submitted
            else -> R.string.submitted_toast
        }
    }

    private val emptyResourceAlertMessage by lazy {
        when (header.resourceType) {
            ResourceType.LESSON -> R.string.there_are_no_cards_in_this_lesson_would_you_like_to_delete_it
            ResourceType.CLASSROOM_RESOURCE -> R.string.there_are_no_cards_in_this_classroom_resource_would_you_like_to_delete_it
            else -> R.string.there_are_no_cards_in_this_resource_would_you_like_to_delete_it
        }
    }

    private val deleteEmptyResourceQuestion by lazy {
        when (header.resourceType) {
            ResourceType.LESSON -> R.string.delete_empty_lesson_question
            ResourceType.CLASSROOM_RESOURCE -> R.string.delete_empty_classroom_resource_question
            else -> R.string.delete_empty_resource_question
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setSubtitle(when(header.resourceType) {
            ResourceType.LESSON -> R.string.lesson_editor
            ResourceType.CLASSROOM_RESOURCE -> R.string.classroom_resource_editor
            else -> R.string.editor
        })
        setupFabs()
    }

    override fun initializeRecyclerView() {
        super.initializeRecyclerView()
        val dragCallback = RecyclerVerticalDragHelperCallback(cardRecyclerAdapter as MovableViewContainer)
        val itemTouchHelper = ItemTouchHelper(dragCallback)
        itemTouchHelper.attachToRecyclerView(cardRecyclerView)
    }

    private fun setupFabs() {
        primaryFab.setVisible()
        secondaryFab.setVisible()

        primaryFab.setTooltip(getString(R.string.add_card))
        secondaryFab.setTooltip(getString(R.string.submit))

        primaryFab.setOnClickListener {
            addNewCard()
        }

        secondaryFab.setOnClickListener{
            showSubmitConfirmationDialog()
        }
    }

    private fun showSubmitConfirmationDialog() {
        AlertDialog.Builder(this)
                .setTitle(confirmationDialogTitle)
                .setMessage(confirmationDialogMessage)
                .setPositiveButton(R.string.submit, { dialog, _ ->
                    submit()
                    dialog.dismiss()
                })
                .setNegativeButton(R.string.cancel, { dialog, _ -> dialog.dismiss() })
                .show()
    }

    private fun submit() {
        showToast(getString(R.string.submitting))
        // The AWAITING_REVIEW_OR_CHANGES_REQUESTED field is set by a Cloud Function,
        //  but we'll set it manually here too so that it is reflected immediately when
        //  the update is complete.
        contentRef.update(mapOf(FirestoreKeys.STATUS to ResourceStatus.AWAITING_REVIEW,
                ResourceStatus.AWAITING_REVIEW_OR_CHANGES_REQUESTED to true))
                    .addOnSuccessListener {
                        showToast(resourceSubmittedMessage)
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
                    .setTitle(deleteEmptyResourceQuestion)
                    .setMessage(emptyResourceAlertMessage)
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

