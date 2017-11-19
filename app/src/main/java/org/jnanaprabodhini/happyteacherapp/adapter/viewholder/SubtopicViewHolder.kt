package org.jnanaprabodhini.happyteacherapp.adapter.viewholder

import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_subtopic_header_card.view.*

class SubtopicViewHolder(itemView: View): BaseResourceHeaderViewHolder(itemView) {
    val titleTextView: TextView = itemView.titleTextView
    val writeButton: Button = itemView.writeButton
    override val resourceColorBar: View = itemView.resourceColorBar

    fun populateView(title: String, resourceType: String) {
        titleTextView.text = title

        setTextColorForResourceType(resourceType)
    }
}