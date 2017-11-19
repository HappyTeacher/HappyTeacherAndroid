package org.jnanaprabodhini.happyteacherapp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.FullScreenGalleryViewerActivity
import org.jnanaprabodhini.happyteacherapp.adapter.viewholder.ImageGalleryItemViewHolder
import org.jnanaprabodhini.happyteacherapp.extension.loadImageToFit

/**
 * A recycler adapter for the in-card image gallery, a horizontal
 *  list of images in cards that feature more than one image.
 */
class ImageGalleryRecyclerAdapter(val imageUrls: List<String>, val context: Context) : RecyclerView.Adapter<ImageGalleryItemViewHolder>() {
    override fun getItemCount(): Int = imageUrls.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ImageGalleryItemViewHolder {
        val cardView = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_gallery_image, parent, false)
        return ImageGalleryItemViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: ImageGalleryItemViewHolder?, position: Int) {
        val imageUrl = imageUrls[position]
        holder?.galleryImageView?.loadImageToFit(imageUrl)

        holder?.galleryImageView?.setOnClickListener {
            FullScreenGalleryViewerActivity.launch(context, imageUrls.toTypedArray(), position)
        }

    }
}

