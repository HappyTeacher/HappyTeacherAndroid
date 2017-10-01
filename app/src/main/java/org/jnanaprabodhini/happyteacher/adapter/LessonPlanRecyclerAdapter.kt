package org.jnanaprabodhini.happyteacher.adapter

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.storage.FirebaseStorage
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.FullScreenGalleryViewerActivity
import org.jnanaprabodhini.happyteacher.adapter.helper.AttachmentDownloadManager
import org.jnanaprabodhini.happyteacher.extension.*
import org.jnanaprabodhini.happyteacher.model.LessonCard
import org.jnanaprabodhini.happyteacher.adapter.viewholder.LessonCardViewHolder


/**
 * Created by grahamearley on 9/25/17.
 */
class LessonPlanRecyclerAdapter(val lessonCardMap: Map<String, LessonCard>, val activity: Activity): RecyclerView.Adapter<LessonCardViewHolder>() {

    val lessonCards by lazy {
        lessonCardMap.toSortedMap().values.toList()
    }

    override fun getItemCount(): Int = lessonCards.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): LessonCardViewHolder {
        val cardView = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_lesson_card, parent, false)
        return LessonCardViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: LessonCardViewHolder?, position: Int) {
        val card = lessonCards[position]

        resetViewVisibility(holder)
        setupText(card, holder)

        if (card.youtubeId.isNotEmpty()) {
            setupYoutubePlayer(card.youtubeId, holder)
        } else if (card.imageUrls.isNotEmpty()) {
            setupImages(card.getCardImageUrls(), holder)
        }

        if (card.attachmentUrl.isNotEmpty()) {
            setupAttachmentView(card.attachmentUrl, holder)
        }

    }

    private fun resetViewVisibility(holder: LessonCardViewHolder?) {
        holder?.headerMediaFrame?.setVisibilityGone()
        holder?.youtubeWebView?.setVisibilityGone()
        holder?.headerImageView?.setVisibilityGone()
        holder?.imageScrollArrowView?.setVisibilityGone()
        holder?.imageGalleryRecyclerView?.setVisibilityGone()
        holder?.attachmentDownloadButton?.setVisibilityGone()
    }

    private fun setupText(card: LessonCard, holder: LessonCardViewHolder?) {
        if (card.header.isNotEmpty()) {
            holder?.headerTextView?.setVisible()
            holder?.headerTextView?.text = card.header
        } else {
            holder?.headerTextView?.setVisibilityGone()
        }

        if (card.body.isNotEmpty()) {
            holder?.bodyTextView?.setVisible()
            holder?.bodyTextView?.setHtmlText(card.body)
        } else {
            holder?.bodyTextView?.setVisibilityGone()
        }
    }

    private fun setupYoutubePlayer(youtubeId: String, holder: LessonCardViewHolder?) {
        holder?.headerMediaFrame?.setVisible()
        holder?.youtubeWebView?.setVisible()
        holder?.youtubeWebView?.loadYoutubeVideo(youtubeId)
    }

    private fun setupImages(imageUrls: List<String>, holder: LessonCardViewHolder?) {

        if (imageUrls.size == 1) {
            holder?.headerMediaFrame?.setVisible()
            holder?.headerImageView?.setVisible()
            holder?.headerImageView?.loadImageToFit(imageUrls.first())

            holder?.headerImageView?.setOnClickListener {
                val fullscreenImageIntent = Intent(activity, FullScreenGalleryViewerActivity::class.java)
                fullscreenImageIntent.putExtra(FullScreenGalleryViewerActivity.IMAGE_URLS, imageUrls.toTypedArray())
                fullscreenImageIntent.putExtra(FullScreenGalleryViewerActivity.SELECTED_IMAGE, 0)

                activity.startActivity(fullscreenImageIntent)
            }

        } else {
            holder?.headerMediaFrame?.setVisible()
            setupImageGalleryRecycler(imageUrls, holder)
        }
    }

    private fun setupImageGalleryRecycler(imageUrls: List<String>, holder: LessonCardViewHolder?) {
        val recycler = holder?.imageGalleryRecyclerView
        recycler?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recycler?.adapter = ImageGalleryRecyclerAdapter(imageUrls, activity)

        recycler?.setVisible()

        // Show an arrow to indicate to the user that this is scrollable
        holder?.imageScrollArrowView?.setVisible()
        holder?.imageScrollArrowView?.setOnClickListener {
            // Scroll right by half of a single image's width:
            val dx = activity.resources.getDimensionPixelOffset(R.dimen.card_image_gallery_width) / 2
            val dy = 0
            recycler?.smoothScrollBy(dx, dy)
        }
        recycler?.onHorizontalScroll {
            // Once a user scrolls, hide the arrow
            holder.imageScrollArrowView.setVisibilityGone()
        }
    }

    private fun setupAttachmentView(attachmentUrl: String, holder: LessonCardViewHolder?) {
        val downloadManager = AttachmentDownloadManager(attachmentUrl, activity)

        holder?.attachmentDownloadButton?.setVisible()
        holder?.attachmentDownloadButton?.setAttachmentDownloadManager(downloadManager)
    }
}

