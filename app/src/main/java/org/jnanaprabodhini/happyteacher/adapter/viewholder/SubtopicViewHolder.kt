package org.jnanaprabodhini.happyteacher.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_subtopic_header_card.view.*

class SubtopicViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val titleTextView: TextView = itemView.titleTextView

    fun populateView(title: String) {
        titleTextView.text = title
    }
}