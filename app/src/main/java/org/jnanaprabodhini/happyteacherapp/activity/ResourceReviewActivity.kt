package org.jnanaprabodhini.happyteacherapp.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.activity_card_list_content_viewer.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.adapter.contentlist.CommentableResourceRecyclerAdapter
import org.jnanaprabodhini.happyteacherapp.adapter.contentlist.ResourceRecyclerAdapter
import org.jnanaprabodhini.happyteacherapp.extension.*
import org.jnanaprabodhini.happyteacherapp.model.ContentCard
import org.jnanaprabodhini.happyteacherapp.model.ResourceHeader
import org.jnanaprabodhini.happyteacherapp.util.FirestoreKeys
import org.jnanaprabodhini.happyteacherapp.util.ResourceStatus
import org.jnanaprabodhini.happyteacherapp.util.ResourceType

/**
 * Created by grahamearley on 11/20/17.
 */
class ResourceReviewActivity : ResourceViewerActivity() {

    companion object {
        fun launch(from: Activity, resourceRef: DocumentReference, resourceHeader: ResourceHeader) {
            val intent = Intent(from, ResourceReviewActivity::class.java)

            intent.apply {
                putExtra(CONTENT_REF_PATH, resourceRef.path)
                putExtra(HEADER, resourceHeader)
            }
            from.startActivity(intent)
        }
    }

    override val cardRecyclerAdapter: ResourceRecyclerAdapter by lazy {
        val options = FirestoreRecyclerOptions.Builder<ContentCard>()
                .setQuery(cardsRef.orderBy(getString(R.string.order_number)), ContentCard::class.java).build()

        CommentableResourceRecyclerAdapter(options, attachmentDestinationDirectory, header.subtopic, this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (header.status) {
            ResourceStatus.AWAITING_REVIEW -> supportActionBar?.setSubtitle(R.string.awaiting_review)
            ResourceStatus.CHANGES_REQUESTED -> supportActionBar?.setSubtitle(R.string.changes_requested)
        }
    }

    override fun onResume() {
        super.onResume()
        cardsRef.addSnapshotListener(this, { querySnapshot, exception ->
            if (querySnapshot?.documents
                    ?.map { it.toObject(ContentCard::class.java) }
                    ?.any { it.feedbackPreviewComment.isNotEmpty() } == true) {
                setFabsForChangeRequest()
            } else {
                setFabForPublishing()
            }
        })
    }

    private fun setFabForPublishing() {
        secondaryFab.setVisibilityGone()
        primaryFab.apply {
            setVisible()
            setTooltip(getString(R.string.publish))
            setDrawableResource(R.drawable.ic_check_white_24dp)
            setColor(R.color.grassGreen)

            setOnClickListener {
                showPublishAlert()
            }
        }
    }

    private fun setFabsForChangeRequest() {
        primaryFab.setVisible()
        secondaryFab.setVisible()

        primaryFab.apply {
            setDrawableResource(R.drawable.ic_assignment_return_white_24dp)
            setColor(R.color.dreamsicleOrange)
            setTooltip(getString(R.string.request_changes))
            setOnClickListener {
                showRequestChangesAlert()
            }
        }

        secondaryFab.apply {
            setDrawableResource(R.drawable.ic_check_white_24dp)
            setColor(R.color.grassGreen)
            setTooltip(getString(R.string.publish))
            setOnClickListener {
                showPublishAlert()
            }
        }
    }

    private fun showPublishAlert() {
        val dialog = AlertDialog.Builder(this)
                .setPositiveButton(R.string.publish, { dialog, _ ->
                    publish()
                    dialog.dismiss()
                })
                .setNegativeButton(R.string.cancel, { dialog, _ -> dialog.dismiss() })

        when (header.resourceType) {
            ResourceType.LESSON -> {
                dialog.setTitle(R.string.do_you_want_to_publish_this_lesson)
                        .setMessage(R.string.publish_lesson_dialog_message)
            }
            ResourceType.CLASSROOM_RESOURCE -> {
                dialog.setTitle(R.string.do_you_want_to_publish_this_classroom_resource)
                        .setMessage(R.string.publish_classroom_resource_dialog_message)
            }
            else -> {
                dialog.setTitle(R.string.do_you_want_to_publish_this_contribution)
                        .setMessage(R.string.publish_resource_dialog_message)
            }
        }

        dialog.show()
    }

    private fun showRequestChangesAlert() {
        val dialog = AlertDialog.Builder(this)
                .setTitle(R.string.do_you_want_to_send_your_feedback_to_the_author)
                .setMessage(R.string.request_changes_dialog_message)
                .setPositiveButton(R.string.request_changes, { dialog, _ ->
                    requestChanges()
                    dialog.dismiss()
                })
                .setNegativeButton(R.string.cancel, { dialog, _ -> dialog.dismiss() })

        dialog.show()
    }

    private fun publish() {
        showToast(R.string.publishing)
        contentRef.update(FirestoreKeys.STATUS, ResourceStatus.PUBLISHED)
                .addOnSuccessListener {
                    showToast(R.string.successfully_published)
                    finish()
                }
    }

    private fun requestChanges() {
        showToast(R.string.sending_feedback_to_author)
        contentRef.update(FirestoreKeys.STATUS, ResourceStatus.CHANGES_REQUESTED)
                .addOnSuccessListener {
                    showToast(R.string.feedback_sent)
                    finish()
                }
    }
}