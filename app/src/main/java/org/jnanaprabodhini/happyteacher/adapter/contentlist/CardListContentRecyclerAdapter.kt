package org.jnanaprabodhini.happyteacher.adapter.contentlist

import android.content.Intent
import android.support.v7.widget.RecyclerView
import org.jnanaprabodhini.happyteacher.activity.FullScreenGalleryViewerActivity
import org.jnanaprabodhini.happyteacher.activity.parent.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.adapter.ImageGalleryRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.helper.AttachmentDownloadManager
import org.jnanaprabodhini.happyteacher.adapter.viewholder.ContentCardViewHolder
import org.jnanaprabodhini.happyteacher.extension.*
import org.jnanaprabodhini.happyteacher.model.AttachmentMetadata
import org.jnanaprabodhini.happyteacher.model.ContentCard
import java.io.File

/**
 * Created by grahamearley on 9/25/17.
 */
abstract class CardListContentRecyclerAdapter(val contentCardMap: Map<String, ContentCard>, val attachmentDestinationDirectory: File, val topicName: String, val topicId: String, val subtopicId: String, val activity: HappyTeacherActivity): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val lessonCards by lazy {
        contentCardMap.toSortedMap().values.toList()
    }

    override fun getItemCount(): Int = lessonCards.size

    protected fun onBindContentCardViewHolder(holder: ContentCardViewHolder, position: Int) {
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

    private fun resetViewVisibility(holder: ContentCardViewHolder?) {
        holder?.headerMediaFrame?.setVisibilityGone()
        holder?.youtubeWebView?.setVisibilityGone()
        holder?.headerImageView?.setVisibilityGone()
        holder?.imageGalleryRecyclerView?.setVisibilityGone()
        holder?.attachmentDownloadButton?.setVisibilityGone()
    }

    private fun setupText(card: ContentCard, holder: ContentCardViewHolder?) {
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

    private fun setupYoutubePlayer(youtubeId: String, holder: ContentCardViewHolder) {
        holder.headerMediaFrame.setVisible()
        holder.youtubeWebView.setVisible()
        holder.youtubeWebView.initializeForYoutubeId(youtubeId)
    }

    private fun setupImages(imageUrls: List<String>, holder: ContentCardViewHolder) {

        if (imageUrls.size == 1) {
            holder.headerMediaFrame.setVisible()
            holder.headerImageView.setVisible()
            holder.headerImageView.loadImageToFit(imageUrls.first())

            holder.headerImageView.setOnClickListener {
                val fullscreenImageIntent = Intent(activity, FullScreenGalleryViewerActivity::class.java)
                fullscreenImageIntent.putExtra(FullScreenGalleryViewerActivity.IMAGE_URLS, imageUrls.toTypedArray())
                fullscreenImageIntent.putExtra(FullScreenGalleryViewerActivity.SELECTED_IMAGE, 0)

                activity.startActivity(fullscreenImageIntent)
            }

        } else {
            holder.headerMediaFrame.setVisible()
            setupImageGalleryRecycler(imageUrls, holder)
        }
    }

    private fun setupImageGalleryRecycler(imageUrls: List<String>, holder: ContentCardViewHolder) {
        val recycler = holder.imageGalleryRecyclerView
        recycler.setAdapter(ImageGalleryRecyclerAdapter(imageUrls, activity))

        recycler.setVisible()
    }

    private fun setupAttachmentView(attachmentUrl: String, attachmentMetadata: AttachmentMetadata, holder: ContentCardViewHolder) {
        val downloadManager = AttachmentDownloadManager(attachmentUrl, attachmentDestinationDirectory, attachmentMetadata, activity)

        holder.attachmentDownloadButton.setVisible()
        holder.attachmentDownloadButton.setAttachmentDownloadManager(downloadManager)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder?) {
        super.onViewRecycled(holder)

        if (holder is ContentCardViewHolder) {
            holder.attachmentDownloadButton.downloadManager?.removeAllDownloadTaskListeners()
        }
    }

}