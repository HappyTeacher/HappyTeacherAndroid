package org.jnanaprabodhini.happyteacher

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.list_item_topic.view.*

class TopicViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)  {
    val topicTextView = itemView.topicTextView
    val lessonsRecyclerView = itemView.lessonsRecyclerView
}