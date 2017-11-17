package org.jnanaprabodhini.happyteacher.adapter.contribute

import android.app.Activity
import android.app.AlertDialog
import android.view.View
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentReference
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.LessonEditorActivity
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.adapter.viewholder.ContributionHeaderViewHolder
import org.jnanaprabodhini.happyteacher.model.ResourceHeader

/**
 * A [ContributionAdapter] implementation for drafts.
 *  Drafts have two buttons: edit and delete.
 *  A draft card opens the editor on click.
 */
class DraftHeaderAdapter(adapterOptions: FirestoreRecyclerOptions<ResourceHeader>, dataObserver: FirebaseDataObserver, activity: Activity):
        ContributionAdapter(adapterOptions, dataObserver, activity) {

    override fun setCardButtons(holder: ContributionHeaderViewHolder, contributionDocumentRef: DocumentReference, model: ResourceHeader?) {
        holder.apply {
            showButtonsForDraft()

            val viewResourceOnClickListener = getViewResourceOnClickListener(model, contributionDocumentRef)
            editButton.setOnClickListener(viewResourceOnClickListener)
            deleteButton.setOnClickListener {
                showDeleteAlert(contributionDocumentRef, holder.adapterPosition)
            }
        }
    }

    private fun showDeleteAlert(documentReference: DocumentReference, documentPosition: Int) {
        AlertDialog.Builder(activity)
                .setTitle(R.string.do_you_want_to_delete_this_draft)
                .setMessage(R.string.this_cannot_be_undone)
                .setPositiveButton(R.string.delete, {dialog, _ ->
                    deleteDocument(documentReference, documentPosition)
                    dialog.dismiss()
                })
                .setNegativeButton(R.string.cancel, {dialog, _ -> dialog.dismiss() })
                .show()
    }

    private fun deleteDocument(documentReference: DocumentReference, position: Int) {
        documentReference.delete()
        notifyItemRemoved(position)
    }

    override fun getViewResourceOnClickListener(model: ResourceHeader?, contributionDocumentRef: DocumentReference): View.OnClickListener {
        return View.OnClickListener {
            val modelOrEmpty = model ?: ResourceHeader()
            LessonEditorActivity.launch(activity, contributionDocumentRef, modelOrEmpty)
        }
    }
}

