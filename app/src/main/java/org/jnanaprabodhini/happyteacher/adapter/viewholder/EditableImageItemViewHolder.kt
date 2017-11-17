package org.jnanaprabodhini.happyteacher.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import kotlinx.android.synthetic.main.list_item_editable_image.view.*

class EditableImageItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val imageView: ImageView = itemView.imageView
    val deleteButton: ImageButton = itemView.deleteButton
}