package org.jnanaprabodhini.happyteacher.adapter

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.storage.FirebaseStorage
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.FullScreenGalleryViewerActivity
import org.jnanaprabodhini.happyteacher.extension.*
import org.jnanaprabodhini.happyteacher.model.LessonCard
import org.jnanaprabodhini.happyteacher.adapter.viewholder.LessonCardViewHolder
import java.io.File
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import org.jnanaprabodhini.happyteacher.activity.LessonViewerActivity


/**
 * Created by grahamearley on 9/25/17.
 */
class LessonPlanRecyclerAdapter(val lessonCards: List<LessonCard>, val activity: Activity): RecyclerView.Adapter<LessonCardViewHolder>() {

    val storageRef by lazy {
        FirebaseStorage.getInstance()
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
        val fileRef = storageRef.getReferenceFromUrl(attachmentUrl)

        holder?.attachmentDownloadButton?.setVisible()
        holder?.attachmentDownloadButton?.setLoadingWithText(activity.getString(R.string.loading_attachment))

        fileRef.metadata.addOnSuccessListener { storageMetadata ->
            setupDownloadBarWithMetadata(holder, storageMetadata, fileRef)
        }
    }

    private fun setupDownloadBarWithMetadata(holder: LessonCardViewHolder?, storageMetadata: StorageMetadata, fileRef: StorageReference) {
        val fileExtension = "." + fileRef.name.split(".").last()
        val fileName = fileRef.name.removeSuffix(fileExtension)

        holder?.attachmentDownloadButton?.setDownloadIconWithText(activity.getString(R.string.file_with_size_in_mb, fileRef.name, storageMetadata.sizeBytes.toMegabyteFromByte()))

        holder?.attachmentDownloadButton?.setOnClickListener {
            downloadFileWithPermission(holder, fileRef, fileName, fileExtension, storageMetadata)
        }
    }

    private fun downloadFileWithPermission(holder: LessonCardViewHolder, fileRef: StorageReference, fileName: String, fileExtension: String, storageMetadata: StorageMetadata) {
        val writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (writePermission != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    LessonViewerActivity.WRITE_STORAGE_PERMISSION_CODE)

        } else {
            holder.attachmentDownloadButton.removeOnClickListener()
            val destinationDirectory = File(Environment.getExternalStorageDirectory().path + File.separator + activity.getString(R.string.app_name))
            destinationDirectory.mkdirs()
            val destinationFile = File.createTempFile(fileName, fileExtension, destinationDirectory)

            val downloadTask = fileRef.getFile(destinationFile)
            downloadTask.addOnSuccessListener({
                setAttachmentOpenable(holder, fileRef, destinationFile, storageMetadata.contentType)
            }).addOnFailureListener({
                if (!downloadTask.isCanceled) setDownloadBarError(holder)
            }).addOnProgressListener { snapshot ->
                val progressRatio = snapshot.bytesTransferred.toDouble() / snapshot.totalByteCount
                val percent = Math.abs(progressRatio)
                updateDownloadBarProgress(percent, holder, downloadTask, storageMetadata, fileRef, destinationFile)
            }
        }
    }

    private fun updateDownloadBarProgress(percent: Double, holder: LessonCardViewHolder, downloadTask: FileDownloadTask, storageMetadata: StorageMetadata, fileRef: StorageReference, destinationFile: File) {
        holder.attachmentDownloadButton.setProgress(percent)

        holder.attachmentDownloadButton.setCancelIconWithText(activity.getString(R.string.downloading))

        holder.attachmentDownloadButton.setOnClickListener {
            holder.attachmentDownloadButton.removeOnClickListener()
            holder.attachmentDownloadButton.setText(activity.getString(R.string.cancelling))

            downloadTask.cancel()
            holder.attachmentDownloadButton.resetProgress()

            destinationFile.delete()

            setupDownloadBarWithMetadata(holder, storageMetadata, fileRef)
        }
    }

    private fun setAttachmentOpenable(holder: LessonCardViewHolder, fileRef: StorageReference, destinationFile: File, type: String) {
        holder.attachmentDownloadButton.setFolderIconWithText(activity.getString(R.string.open_x,  fileRef.name))
        holder.attachmentDownloadButton.setOnClickListener {
            val downloadedFileUri = Uri.fromFile(destinationFile)
            val openFileIntent = Intent(Intent.ACTION_VIEW)
            openFileIntent.setDataAndType(downloadedFileUri, type)
            activity.startActivity(openFileIntent)
        }
    }

    private fun setDownloadBarError(holder: LessonCardViewHolder) {
        holder.attachmentDownloadButton.setErrorWithText(activity.getString(R.string.download_failed))
    }
}

