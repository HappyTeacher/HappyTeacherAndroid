package org.jnanaprabodhini.happyteacher.adapter

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.FullScreenGalleryViewerActivity
import org.jnanaprabodhini.happyteacher.activity.parent.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.adapter.helper.AttachmentDownloadManager
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.adapter.viewholder.LessonCardViewHolder
import org.jnanaprabodhini.happyteacher.adapter.viewholder.ColoredHorizontalRecyclerViewHolder
import org.jnanaprabodhini.happyteacher.extension.*
import org.jnanaprabodhini.happyteacher.model.AttachmentMetadata
import org.jnanaprabodhini.happyteacher.model.LessonCard
import java.io.File


/**
 * Created by grahamearley on 9/25/17.
 */
class LessonPlanRecyclerAdapter(val lessonCardMap: Map<String, LessonCard>, val attachmentDestinationDirectory: File, val topicName: String, val topicId: String, val subtopicId: String, val activity: HappyTeacherActivity): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object { val LESSON_CARD_VIEW_TYPE = 0; val CLASSROOM_RESOURCES_FOOTER_VIEW_TYPE = 1 }

    val lessonCards by lazy {
        lessonCardMap.toSortedMap().values.toList()
    }

    override fun getItemCount(): Int = lessonCards.size + 1 // + 1 for footer view (classroom resources section)

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == CLASSROOM_RESOURCES_FOOTER_VIEW_TYPE) {
            val classroomResourcesView = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_colored_horizontal_recycler, parent, false)
            return ColoredHorizontalRecyclerViewHolder(classroomResourcesView)
        } else {
            val cardView = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_lesson_card, parent, false)
            return LessonCardViewHolder(cardView)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == itemCount - 1) {
            return CLASSROOM_RESOURCES_FOOTER_VIEW_TYPE
        } else {
            return LESSON_CARD_VIEW_TYPE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is LessonCardViewHolder) {
            onBindLessonCardViewHolder(holder, position)
        } else if (holder is ColoredHorizontalRecyclerViewHolder) {
            onBindClassRoomResourcesViewHolder(holder, position)
        }
    }

    private fun onBindLessonCardViewHolder(holder: LessonCardViewHolder, position: Int) {
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

    private fun onBindClassRoomResourcesViewHolder(holder: ColoredHorizontalRecyclerViewHolder, position: Int) {
        holder.itemView.setBackgroundResource(R.color.colorPrimaryDark)
        holder.titleTextView.text = activity.getString(R.string.classroom_resources)
//        holder.emptyView.setVisible()
//        holder.emptyTextView.text = "No resources here yo!" // todo: extract strings, firebase keys
        holder.progressBar.setVisibilityGone()

        val classroomResourceQuery = activity.databaseReference.child("classroom_resources_headers").child(topicId).child(subtopicId)
        val observer = object: FirebaseDataObserver{} // todo: fill in

        holder.horizontalRecyclerView.setAdapter(SubtopicLessonHeaderRecyclerAdapter(topicName, classroomResourceQuery, activity, observer))
        holder.horizontalRecyclerView.setVisible()
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

    private fun setupYoutubePlayer(youtubeId: String, holder: LessonCardViewHolder) {
        holder.headerMediaFrame.setVisible()
        holder.youtubeWebView.setVisible()
        holder.youtubeWebView.loadYoutubeVideo(youtubeId)
    }

    private fun setupImages(imageUrls: List<String>, holder: LessonCardViewHolder) {

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

    private fun setupImageGalleryRecycler(imageUrls: List<String>, holder: LessonCardViewHolder) {
        val recycler = holder.imageGalleryRecyclerView
        recycler.setAdapter(ImageGalleryRecyclerAdapter(imageUrls, activity))

        recycler.setVisible()
    }

    private fun setupAttachmentView(attachmentUrl: String, attachmentMetadata: AttachmentMetadata, holder: LessonCardViewHolder) {
        val downloadManager = AttachmentDownloadManager(attachmentUrl, attachmentDestinationDirectory, attachmentMetadata, activity)

        holder.attachmentDownloadButton.setVisible()
        holder.attachmentDownloadButton.setAttachmentDownloadManager(downloadManager)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder?) {
        super.onViewRecycled(holder)

        if (holder is LessonCardViewHolder) {
            holder.attachmentDownloadButton.downloadManager?.removeAllDownloadTaskListeners()
        }
    }

}

