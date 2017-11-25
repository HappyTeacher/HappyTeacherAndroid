package org.jnanaprabodhini.happyteacherapp.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_subtopic_choice_header_recycler.view.*
import org.jnanaprabodhini.happyteacherapp.view.HorizontalPagerRecyclerView

class TopicSubtopicChoiceRecyclerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val titleTextView: TextView = itemView.titleTextView
    val horizontalRecyclerView: HorizontalPagerRecyclerView = itemView.horizontalRecyclerView
    val statusTextView: TextView = itemView.statusTextView
    val progressBar: View = itemView.subtopicChoiceProgressBar
}