package org.jnanaprabodhini.happyteacherapp.adapter.contribute

import android.app.Activity
import android.app.AlertDialog
import android.view.View
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentReference
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.ClassroomResourceViewerActivity
import org.jnanaprabodhini.happyteacherapp.activity.LessonViewerActivity
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.adapter.viewholder.ContributionHeaderViewHolder
import org.jnanaprabodhini.happyteacherapp.extension.showToast
import org.jnanaprabodhini.happyteacherapp.model.ResourceHeader
import org.jnanaprabodhini.happyteacherapp.util.FirestoreKeys
import org.jnanaprabodhini.happyteacherapp.util.ResourceStatus
import org.jnanaprabodhini.happyteacherapp.util.ResourceType

/**
 * Created by grahamearley on 11/17/17.
 */
class PublishedSubmissionHeaderAdapter(adapterOptions: FirestoreRecyclerOptions<ResourceHeader>, dataObserver: FirebaseDataObserver, activity: Activity):
        ContributionAdapter(adapterOptions, dataObserver, activity) {

    override fun setCardButtons(holder: ContributionHeaderViewHolder, contributionDocumentRef: DocumentReference, model: ResourceHeader?) {
        holder.apply {
            showButtonsForPublishedContent()

            editButton.setOnClickListener{
                showUnpublishAlert(contributionDocumentRef, holder.adapterPosition, model?.resourceType)
            }
        }
    }

    private fun showUnpublishAlert(documentReference: DocumentReference, documentPosition: Int, type: String?) {
        val dialog = AlertDialog.Builder(activity)
                .setPositiveButton(R.string.unpublish, { dialog, _ ->
                    unpublishDocument(documentReference, documentPosition)
                    dialog.dismiss()
                })
                .setNegativeButton(R.string.cancel, { dialog, _ -> dialog.dismiss() })

        // Set title and message based on type
        when (type) {
            ResourceType.LESSON -> {
                dialog.setTitle(R.string.do_you_want_to_unpublish_this_lesson)
                        .setMessage(R.string.the_lesson_will_return_to_your_drafts_and_will_no_longer_be_publicly_visible)
            }
            ResourceType.CLASSROOM_RESOURCE -> {
                dialog.setTitle(R.string.do_you_want_to_unpublish_this_classroom_resource)
                        .setMessage(R.string.the_classroom_resource_will_return_to_your_drafts_and_will_no_longer_be_publicly_visible)
            }
            else -> {
                dialog.setTitle(R.string.do_you_want_to_unpublish_this_contribution)
                        .setMessage(R.string.it_will_return_to_your_drafts_and_will_no_longer_be_publicly_visible)
            }
        }

        dialog.show()
    }

    private fun unpublishDocument(documentReference: DocumentReference, position: Int) {
        documentReference.update(FirestoreKeys.STATUS, ResourceStatus.DRAFT).addOnSuccessListener(activity, {
            activity.showToast(R.string.submission_returned_to_drafts)
        })
        notifyItemRemoved(position)
    }

    override fun getViewResourceOnClickListener(model: ResourceHeader?, contributionDocumentRef: DocumentReference): View.OnClickListener {
        return View.OnClickListener {
            val modelOrEmpty = model ?: ResourceHeader()

            when (model?.resourceType) {
                ResourceType.LESSON -> LessonViewerActivity.launch(activity, contributionDocumentRef, modelOrEmpty, shouldShowSubmissionCount = true)
                ResourceType.CLASSROOM_RESOURCE -> ClassroomResourceViewerActivity.launch(activity, contributionDocumentRef, modelOrEmpty)
            }
        }
    }

}