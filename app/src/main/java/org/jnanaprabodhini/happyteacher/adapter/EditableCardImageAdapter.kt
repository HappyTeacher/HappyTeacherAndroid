package org.jnanaprabodhini.happyteacher.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.FullScreenGalleryViewerActivity
import org.jnanaprabodhini.happyteacher.adapter.viewholder.EditableImageItemViewHolder
import org.jnanaprabodhini.happyteacher.extension.loadImageToFit
import org.jnanaprabodhini.happyteacher.model.ContentCard

/**
 * A recycler adapter of images for the card editor. Allows
 *  the user to edit the list of images.
 */
class EditableCardImageAdapter(val card: ContentCard, val context: Context) : RecyclerView.Adapter<EditableImageItemViewHolder>() {
    override fun getItemCount(): Int = card.imageUrls.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): EditableImageItemViewHolder {
        val cardView = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_editable_image, parent, false)
        return EditableImageItemViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: EditableImageItemViewHolder?, position: Int) {
        val imageUrl = card.imageUrls[position]
        holder?.imageView?.loadImageToFit(imageUrl)

        holder?.imageView?.setOnClickListener {
            FullScreenGalleryViewerActivity.launch(context, card.imageUrls.toTypedArray(), position)
        }

        holder?.deleteButton?.setOnClickListener{
            val newImageUrls = card.imageUrls.toMutableList()
            newImageUrls.removeAt(position)
            card.imageUrls = newImageUrls
            notifyItemRemoved(position)
        }
    }
}