package org.jnanaprabodhini.happyteacher.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.FullScreenGalleryViewerActivity
import org.jnanaprabodhini.happyteacher.adapter.viewholder.ImageGalleryItemViewHolder
import org.jnanaprabodhini.happyteacher.extension.loadImage

class ImageGalleryRecyclerAdapter(val imageUrls: List<String>, val context: Context) : RecyclerView.Adapter<ImageGalleryItemViewHolder>() {
    override fun getItemCount(): Int = imageUrls.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ImageGalleryItemViewHolder {
        val cardView = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_gallery_image, parent, false)
        return ImageGalleryItemViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: ImageGalleryItemViewHolder?, position: Int) {
        val imageUrl = imageUrls[position]
        holder?.galleryImageView?.loadImage(imageUrl)

        holder?.galleryImageView?.setOnClickListener {
            val fullscreenImageIntent = Intent(context, FullScreenGalleryViewerActivity::class.java)
            fullscreenImageIntent.putExtra(FullScreenGalleryViewerActivity.IMAGE_URLS, imageUrls.toTypedArray())
            fullscreenImageIntent.putExtra(FullScreenGalleryViewerActivity.SELECTED_IMAGE, position)

            context.startActivity(fullscreenImageIntent)
        }

    }
}

