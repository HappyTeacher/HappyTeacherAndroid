package org.jnanaprabodhini.happyteacher.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_content_header_recycler.view.*
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.view.HorizontalPagerRecyclerView

class ContentHeaderRecyclerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)  {
    val titleTextView: TextView = itemView.titleTextView
    val horizontalRecyclerView: HorizontalPagerRecyclerView = itemView.horizontalRecyclerView
    val emptyTextView: TextView = itemView.emptyTextView
    val contributeButton: Button = itemView.contributeButton
    val progressBar: View = itemView.progressBar

    fun hideEmptyViews() {
        emptyTextView.setVisibilityGone()
        contributeButton.setVisibilityGone()
    }

    fun showEmptyViews() {
        emptyTextView.setVisible()
        contributeButton.setVisible()
    }
}