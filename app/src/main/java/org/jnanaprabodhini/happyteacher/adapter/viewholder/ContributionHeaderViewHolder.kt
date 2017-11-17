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

    fun showButtonsForDraft() {
        deleteButton.setDrawableLeft(R.drawable.ic_delete_white_24dp)
        editButton.setDrawableLeft(R.drawable.ic_pencil_white_24dp)

        deleteButton.setVisible()
        editButton.setVisible()
    }

    fun showAwaitingReviewStatus(context: Context) {
        statusTextView.setBackgroundColorCompat(R.color.colorAccent)
        statusTextView.text = context.getString(R.string.submitted_for_review)
        statusTextView.setDrawableLeft(R.drawable.ic_assignment_ind_white_24dp)

        statusTextView.setVisible()
    }

    fun showChangesRequestedStatus(context: Context) {
        statusTextView.setBackgroundColorCompat(R.color.dreamsicleOrange)
        statusTextView.text = context.getString(R.string.changes_requested)
        statusTextView.setDrawableLeft(R.drawable.ic_assignment_returned_white_24dp)

        statusTextView.setVisible()
    }

    fun showPublishedStatus(context: Context) {
        statusTextView.setBackgroundColorCompat(R.color.grassGreen)
        statusTextView.text = context.getString(R.string.published_status)
        statusTextView.setDrawableLeft(R.drawable.ic_assignment_checkmark_white_24dp)

        statusTextView.setVisible()
    }

    fun hideStatusView() {
        statusTextView.setVisibilityGone()
    }

}