package org.jnanaprabodhini.happyteacher.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_contribution_header_card.view.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.extension.setDrawableLeft
import org.jnanaprabodhini.happyteacher.extension.setVisible

class ContributionHeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val titleTextView: TextView = itemView.titleTextView
    val subjectTextView: TextView = itemView.subjectTextView
    val dateEditedTextView: TextView = itemView.dateEditedTextView
    val deleteButton: TextView = itemView.deleteButton
    val editButton: TextView = itemView.editButton

    fun showButtonsForDraft() {
        deleteButton.setDrawableLeft(R.drawable.ic_delete_white_24dp)
        editButton.setDrawableLeft(R.drawable.ic_pencil_white_24dp)

        deleteButton.setVisible()
        editButton.setVisible()
    }

}