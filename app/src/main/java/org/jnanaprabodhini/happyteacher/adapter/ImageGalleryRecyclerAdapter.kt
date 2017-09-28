package org.jnanaprabodhini.happyteacher.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.viewholder.ImageGalleryItemViewHolder
import org.jnanaprabodhini.happyteacher.extension.loadImage

class ImageGalleryRecyclerAdapter(val imageUrls: List<String>) : RecyclerView.Adapter<ImageGalleryItemViewHolder>() {
    override fun getItemCount(): Int = imageUrls.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ImageGalleryItemViewHolder {
        val cardView = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_gallery_image, parent, false)
        return ImageGalleryItemViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: ImageGalleryItemViewHolder?, position: Int) {
        val imageUrl = imageUrls[position]
        holder?.galleryImageView?.loadImage(imageUrl)
    }
}

