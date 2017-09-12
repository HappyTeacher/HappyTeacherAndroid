package org.jnanaprabodhini.happyteacher.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_topic.view.*

class TopicViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)  {
    val topicTextView: TextView = itemView.topicTextView
    val lessonsRecyclerView: RecyclerView = itemView.lessonsRecyclerView
    val emptyView: LinearLayout = itemView.emptyView
    val contributeButton: Button = itemView.contributeButton
}