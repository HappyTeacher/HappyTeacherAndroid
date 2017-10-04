package org.jnanaprabodhini.happyteacher.adapter

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.FullScreenGalleryViewerActivity
import org.jnanaprabodhini.happyteacher.adapter.helper.AttachmentDownloadManager
import org.jnanaprabodhini.happyteacher.extension.*
import org.jnanaprabodhini.happyteacher.model.LessonCard
import org.jnanaprabodhini.happyteacher.adapter.viewholder.LessonCardViewHolder
import org.jnanaprabodhini.happyteacher.model.AttachmentMetadata
import java.io.File


/**
 * Created by grahamearley on 9/25/17.
 */
class LessonPlanRecyclerAdapter(val lessonCardMap: Map<String, LessonCard>, val attachmentDestinationDirectory: File, val activity: Activity): RecyclerView.Adapter<LessonCardViewHolder>() {

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

        if (card.attachmentPath.isNotEmpty() && card.attachmentMetadata.isNotEmpty()) {
            setupAttachmentView(card.attachmentPath, card.attachmentMetadata, holder)
        }

    }

    private fun resetViewVisibility(holder: LessonCardViewHolder?) {
        holder?.headerMediaFrame?.setVisibilityGone()
        holder?.youtubeWebView?.setVisibilityGone()
        holder?.headerImageView?.setVisibilityGone()
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
        recycler?.setAdapter(ImageGalleryRecyclerAdapter(imageUrls, activity))

        recycler?.setVisible()
    }

    private fun setupAttachmentView(attachmentUrl: String, attachmentMetadata: AttachmentMetadata, holder: LessonCardViewHolder?) {
        val downloadManager = AttachmentDownloadManager(attachmentUrl, attachmentDestinationDirectory, attachmentMetadata, activity)

        holder?.attachmentDownloadButton?.setVisible()
        holder?.attachmentDownloadButton?.setAttachmentDownloadManager(downloadManager)
    }

    override fun onViewRecycled(holder: LessonCardViewHolder?) {
        super.onViewRecycled(holder)
        holder?.attachmentDownloadButton?.downloadManager?.removeAllDownloadTaskListeners()
    }

}

