package org.jnanaprabodhini.happyteacher.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_draft_header_card.view.*

class DraftHeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val titleTextView: TextView = itemView.titleTextView
    val subjectTextView: TextView = itemView.subjectTextView
    val dateEditedTextView: TextView = itemView.dateEditedTextView
    val deleteButton: ImageView = itemView.deleteButton
}