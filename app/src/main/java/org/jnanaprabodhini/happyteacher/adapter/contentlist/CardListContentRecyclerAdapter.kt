package org.jnanaprabodhini.happyteacher.adapter.contentlist

import android.content.Intent
import android.support.v7.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import org.jnanaprabodhini.happyteacher.activity.FullScreenGalleryViewerActivity
import org.jnanaprabodhini.happyteacher.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.adapter.ImageGalleryRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.firestore.FirestoreObserverRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.helper.AttachmentDownloadManager
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.adapter.viewholder.ContentCardViewHolder
import org.jnanaprabodhini.happyteacher.extension.loadImageToFit
import org.jnanaprabodhini.happyteacher.extension.setHtmlText
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.AttachmentMetadata
import org.jnanaprabodhini.happyteacher.model.ContentCard
import java.io.File

/**
 * Created by grahamearley on 9/25/17.
 */
abstract class CardListContentRecyclerAdapter(options: FirestoreRecyclerOptions<ContentCard>, val attachmentDestinationDirectory: File, val subtopicId: String, val activity: HappyTeacherActivity, dataObserver: FirebaseDataObserver):
        FirestoreObserverRecyclerAdapter<ContentCard, RecyclerView.ViewHolder>(options, dataObserver) {

    protected fun onBindContentCardViewHolder(holder: ContentCardViewHolder, model: ContentCard?) {
        resetViewVisibility(holder)

        model?.let {
            setupText(model, holder)

            if (model.youtubeId.isNotEmpty()) {
                setupYoutubePlayer(model.youtubeId, holder)
            } else if (model.imageUrls.isNotEmpty()) {
                setupImages(model.imageUrls, holder)
            }

            if (model.attachmentPath.isNotEmpty() && model.attachmentMetadata.isNotEmpty()) {
                setupAttachmentView(model.attachmentPath, model.attachmentMetadata, holder)
            }
        }
    }

    private fun resetViewVisibility(holder: ContentCardViewHolder?) {
        holder?.headerMediaFrame?.setVisibilityGone()
        holder?.youtubeWebView?.setVisibilityGone()
        holder?.headerImageView?.setVisibilityGone()
        holder?.loadButton?.setVisibilityGone()
        holder?.headerProgressBar?.setVisibilityGone()
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

        holder.youtubeWebView.initializeForYoutubeIdWithControlViews(youtubeId, holder.headerImageView, holder.loadButton, holder.headerProgressBar)
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