package org.jnanaprabodhini.happyteacherapp.adapter.contribute

import android.app.Activity
import android.app.AlertDialog
import android.view.View
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentReference
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.ClassroomResourceViewerActivity
import org.jnanaprabodhini.happyteacherapp.activity.CommentableResourceContentViewerActivity
import org.jnanaprabodhini.happyteacherapp.activity.LessonViewerActivity
import org.jnanaprabodhini.happyteacherapp.activity.ResourceContentEditorActivity
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
class SubmissionHeaderAdapter(adapterOptions: FirestoreRecyclerOptions<ResourceHeader>, dataObserver: FirebaseDataObserver, activity: Activity):
        ContributionAdapter(adapterOptions, dataObserver, activity) {

    override fun setCardButtons(holder: ContributionHeaderViewHolder, contributionDocumentRef: DocumentReference, model: ResourceHeader?) {
        holder.apply {
            showButtonsForSubmittedContent()

            editButton.setOnClickListener{
                showUnsubmitAlert(contributionDocumentRef, holder.adapterPosition, model?.resourceType)
            }
        }
    }

    private fun showUnsubmitAlert(documentReference: DocumentReference, documentPosition: Int, type: String?) {
        val dialog = AlertDialog.Builder(activity)
                .setPositiveButton(R.string.unsubmit, { dialog, _ ->
                    unsubmitDocument(documentReference, documentPosition)
                    dialog.dismiss()
                })
                .setNegativeButton(R.string.cancel, { dialog, _ -> dialog.dismiss() })

        // Set title and message based on type
        when (type) {
            ResourceType.LESSON -> {
                dialog.setTitle(R.string.do_you_want_to_unsubmit_this_lesson)
                    .setMessage(R.string.the_lesson_will_return_to_your_drafts)
            }
            ResourceType.CLASSROOM_RESOURCE -> {
                dialog.setTitle(R.string.do_you_want_to_unsubmit_this_classroom_resource)
                        .setMessage(R.string.the_classroom_resource_will_return_to_your_drafts)
            }
            else -> {
                dialog.setTitle(R.string.do_you_want_to_unsubmit_this_contribution)
                        .setMessage(R.string.it_will_return_to_your_drafts)
            }
        }

        dialog.show()
    }

    private fun unsubmitDocument(documentReference: DocumentReference, position: Int) {
        documentReference.update(FirestoreKeys.STATUS, ResourceStatus.DRAFT)
                .addOnSuccessListener(activity, {
                    activity.showToast(R.string.submission_returned_to_drafts)
                })
        notifyItemRemoved(position)
    }

    override fun getViewResourceOnClickListener(model: ResourceHeader?, contributionDocumentRef: DocumentReference): View.OnClickListener {
        return View.OnClickListener {
            val modelOrEmpty = model ?: ResourceHeader()
            when (model?.status) {
                ResourceStatus.CHANGES_REQUESTED -> ResourceContentEditorActivity.launch(activity, contributionDocumentRef, modelOrEmpty)
                else -> when (model?.resourceType) {
                    ResourceType.LESSON -> LessonViewerActivity.launch(activity, contributionDocumentRef,
                            modelOrEmpty, showSubmissionCount = false)
                    ResourceType.CLASSROOM_RESOURCE -> ClassroomResourceViewerActivity.launch(activity,
                            contributionDocumentRef, modelOrEmpty)
                }
            }
        }
    }
}