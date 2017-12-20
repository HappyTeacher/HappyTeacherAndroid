package org.jnanaprabodhini.happyteacherapp.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_subtopic_choice_header_recycler.view.*
import org.jnanaprabodhini.happyteacherapp.view.HorizontalPagerView

class TopicSubtopicChoiceRecyclerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val titleTextView: TextView = itemView.titleTextView
    val horizontalPagerView: HorizontalPagerView = itemView.horizontalPagerView
    val statusTextView: TextView = itemView.statusTextView
    val progressBar: View = itemView.progressBar
}