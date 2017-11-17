package org.jnanaprabodhini.happyteacher.adapter.viewholder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_contribution_header_card.view.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.extension.*

class ContributionHeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val titleTextView: TextView = itemView.titleTextView
    val subjectTextView: TextView = itemView.subjectTextView
    val dateEditedTextView: TextView = itemView.dateEditedTextView
    val deleteButton: TextView = itemView.deleteButton
    val editButton: TextView = itemView.editButton
    val statusTextView: TextView = itemView.statusTextView
    val statusColorBar: View = itemView.statusColorBar

    fun showButtonsForDraft() {
        deleteButton.setDrawableLeft(R.drawable.ic_delete_white_24dp)
        editButton.setDrawableLeft(R.drawable.ic_pencil_white_24dp)

        deleteButton.setVisible()
        editButton.setVisible()
    }

    fun showButtonsForSubmittedContent() {
        deleteButton.setVisibilityGone()
        editButton.setDrawableLeft(R.drawable.ic_undo_white_24dp)
        editButton.setText(R.string.unsubmit)
        editButton.setVisible()
    }

    fun showButtonsForPublishedContent() {
        deleteButton.setVisibilityGone()
        editButton.setDrawableLeft(R.drawable.ic_undo_white_24dp)
        editButton.setText(R.string.unpublish)
        editButton.setVisible()
    }

    fun showAwaitingReviewStatus(context: Context) {
        statusColorBar.setVisible()
        statusColorBar.setBackgroundColorRes(R.color.colorAccent)

        statusTextView.text = context.getString(R.string.submitted_for_review)
        statusTextView.setDrawableLeft(R.drawable.ic_assignment_ind_accent_24dp)
        statusTextView.setTextColorRes(R.color.colorAccent)

        statusTextView.setVisible()
    }

    fun showChangesRequestedStatus(context: Context) {
        statusColorBar.setVisible()
        statusColorBar.setBackgroundColorRes(R.color.dreamsicleOrange)

        statusTextView.text = context.getString(R.string.changes_requested)
        statusTextView.setDrawableLeft(R.drawable.ic_assignment_returned_orange_24dp)
        statusTextView.setTextColorRes(R.color.dreamsicleOrange)

        statusTextView.setVisible()
    }

    fun showPublishedStatus(context: Context) {
        statusColorBar.setVisible()
        statusColorBar.setBackgroundColorRes(R.color.grassGreen)

        statusTextView.text = context.getString(R.string.published_status)
        statusTextView.setDrawableLeft(R.drawable.ic_assignment_checkmark_green_24dp)
        statusTextView.setTextColorRes(R.color.grassGreen)

        statusTextView.setVisible()
    }

    fun hideStatusView() {
        statusTextView.setVisibilityGone()
    }

}