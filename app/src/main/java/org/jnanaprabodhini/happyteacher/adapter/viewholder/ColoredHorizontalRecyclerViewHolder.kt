package org.jnanaprabodhini.happyteacher.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_colored_horizontal_recycler.view.*
import org.jnanaprabodhini.happyteacher.view.HorizontalPagerRecyclerView

class ColoredHorizontalRecyclerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)  {
    val titleTextView: TextView = itemView.titleTextView
    val horizontalRecyclerView: HorizontalPagerRecyclerView = itemView.horizontalRecyclerView
    val emptyView: LinearLayout = itemView.emptyView
    val emptyTextView: TextView = itemView.emptyTextView
    val contributeButton: Button = itemView.contributeButton
    val progressBar: View = itemView.progressBarFrame
}