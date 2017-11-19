package org.jnanaprabodhini.happyteacherapp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.FullScreenGalleryViewerActivity
import org.jnanaprabodhini.happyteacherapp.adapter.helper.MovableViewContainer
import org.jnanaprabodhini.happyteacherapp.adapter.viewholder.EditableImageItemViewHolder
import org.jnanaprabodhini.happyteacherapp.extension.loadImageToFit
import org.jnanaprabodhini.happyteacherapp.extension.setOneTimeOnClickListener
import org.jnanaprabodhini.happyteacherapp.model.ContentCard

/**
 * A recycler adapter of images for the card editor. Allows
 *  the user to edit the list of images.
 */
class EditableCardImageAdapter(val card: ContentCard, val context: Context):
        RecyclerView.Adapter<EditableImageItemViewHolder>(), MovableViewContainer {

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

        holder?.deleteButton?.setOneTimeOnClickListener {
            val newImageUrls = card.imageUrls.toMutableList()
            newImageUrls.removeAt(holder.adapterPosition)
            card.imageUrls = newImageUrls
            notifyItemRemoved(holder.adapterPosition)
        }
    }

    override fun onViewMoved(oldPosition: Int, newPosition: Int) {
        notifyItemMoved(oldPosition, newPosition)
    }

    override fun onViewSetDown(oldPosition: Int, newPosition: Int) {
        val reorderedImageUrls = card.imageUrls.toMutableList()
        val imageToMove = reorderedImageUrls[oldPosition]
        reorderedImageUrls.removeAt(oldPosition)
        reorderedImageUrls.add(newPosition, imageToMove)

        card.imageUrls = reorderedImageUrls
    }
}