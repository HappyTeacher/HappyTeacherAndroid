package org.jnanaprabodhini.happyteacher.adapter.helper

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageMetadata
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.LessonViewerActivity
import org.jnanaprabodhini.happyteacher.extension.*
import org.jnanaprabodhini.happyteacher.model.AttachmentMetadata
import org.jnanaprabodhini.happyteacher.view.DownloadBarView
import java.io.File

/**
 * Created by grahamearley on 10/1/17.
 */
class AttachmentDownloadManager(attachmentPath: String, val attachmentDestinationDirectory: File, val attachmentMetadata: AttachmentMetadata, val activity: Activity) {

    val fileRef = FirebaseStorage.getInstance().getReference(attachmentPath)

    private var onSuccessListener: OnSuccessListener<FileDownloadTask.TaskSnapshot>? = null
    private var onFailureListener: OnFailureListener? = null
    private var onProgressListener: OnProgressListener<FileDownloadTask.TaskSnapshot>? = null

    fun bindView(downloadBarView: DownloadBarView) {
        initializeDownloadBarOnClickListener(downloadBarView)
    }

    private fun initializeDownloadBarOnClickListener(downloadBarView: DownloadBarView) {
        val fileExtension = fileRef.name.split(".").last()
        val fileName = fileRef.name.removeSuffix("." + fileExtension)

        val destinationFile = File(attachmentDestinationDirectory, "${fileName}_${attachmentMetadata.timeCreated}.$fileExtension")

        if (destinationFile.exists() && destinationFile.length() == attachmentMetadata.size) {
            // ==> File is downloaded completely.
            setAttachmentOpenable(destinationFile, downloadBarView)
        } else if (destinationFile.exists() && fileRef.activeDownloadTasks.isNotEmpty()) {
            // ==> Download is still in progress.
            syncViewWithDownloadTask(fileRef.activeDownloadTasks.first(), destinationFile, downloadBarView)
        } else {
            setupDownloadBarWithFileInfo(destinationFile, downloadBarView)
        }
    }

    private fun setupDownloadBarWithFileInfo(destinationFile: File, downloadBarView: DownloadBarView) {
        downloadBarView.setDownloadIconWithText(activity.getString(R.string.file_with_size_in_mb, fileRef.name, attachmentMetadata.size.toMegabyteFromByte()))
        downloadBarView.resetProgress()

        downloadBarView.setOneTimeOnClickListener {
            downloadFileIfPermitted(destinationFile, downloadBarView)
        }
    }

    private fun downloadFileIfPermitted(destinationFile: File, downloadBarView: DownloadBarView) {
        doIfPermitted {
            attachmentDestinationDirectory.mkdirs()
            destinationFile.createNewFile()
            beginDownload(destinationFile, downloadBarView)
        }
    }

    private fun beginDownload(destinationFile: File, downloadBarView: DownloadBarView) {
        // Calling `getFile(..)` starts the download:
        val downloadTask = fileRef.getFile(destinationFile)
        syncViewWithDownloadTask(downloadTask, destinationFile, downloadBarView)
    }

    private fun syncViewWithDownloadTask(downloadTask: FileDownloadTask, destinationFile: File, downloadBarView: DownloadBarView) {
        downloadBarView.setCancelIconWithText(activity.getString(R.string.downloading))

        onSuccessListener = OnSuccessListener<FileDownloadTask.TaskSnapshot> { setAttachmentOpenable(destinationFile, downloadBarView) }
        onFailureListener = OnFailureListener {
            destinationFile.delete()
            if (!downloadTask.isCanceled) downloadBarView.setErrorWithText(activity.getString(R.string.download_failed))
        }
        onProgressListener = OnProgressListener<FileDownloadTask.TaskSnapshot> { snapshot ->
            if (!downloadTask.isCanceled) updateProgressUi(snapshot?.bytesTransferred ?: 0, snapshot?.totalByteCount ?: 1, downloadBarView)
        }

        downloadBarView.setOneTimeOnClickListener {
            removeAllDownloadTaskListeners()
            cancelDownload(downloadTask, destinationFile, downloadBarView)
            setupDownloadBarWithFileInfo(destinationFile, downloadBarView)
        }

        downloadTask.addOnSuccessListenerIfNotNull(activity, onSuccessListener)
        downloadTask.addOnFailureListenerIfNotNull(activity, onFailureListener)
        downloadTask.addOnProgressListenerIfNotNull(activity, onProgressListener)
    }

    private fun cancelDownload(downloadTask: FileDownloadTask, destinationFile: File, downloadBarView: DownloadBarView) {
        downloadBarView.setText(activity.getString(R.string.canceling))

        downloadTask.cancel()
        destinationFile.delete()

        activity.showToast(R.string.download_canceled)
    }

    private fun setAttachmentOpenable(destinationFile: File, downloadBarView: DownloadBarView) {
        downloadBarView.resetProgress()
        downloadBarView.setFolderIconWithText(activity.getString(R.string.open_x,  fileRef.name))
        downloadBarView.setOnClickListener {
            openFileIfExists(destinationFile, downloadBarView)
        }

        downloadBarView.setOneTimeOnLongClickListener {
            showDeleteFileAlertDialog(destinationFile, downloadBarView)
        }
    }

    private fun openFileIfExists(destinationFile: File, downloadBarView: DownloadBarView) {
        if (destinationFile.exists()) {
            val downloadedFileUri = Uri.fromFile(destinationFile)
            val openFileIntent = Intent(Intent.ACTION_VIEW)
            openFileIntent.setDataAndType(downloadedFileUri, attachmentMetadata.contentType)
            activity.startActivity(openFileIntent)
        } else {
            activity.showToast(R.string.the_file_is_no_longer_available_try_downloading_it_again)
            setupDownloadBarWithFileInfo(destinationFile, downloadBarView)
        }
    }

    private fun updateProgressUi(bytesTransferred: Long, totalBytes: Long, downloadBarView: DownloadBarView) {
        val progressRatio = bytesTransferred / totalBytes.toDouble()
        val percent = Math.abs(progressRatio)

        downloadBarView.setProgress(percent)
    }

    fun removeAllDownloadTaskListeners() {
        fileRef.activeDownloadTasks.forEach { downloadTask ->
            onFailureListener?.let { downloadTask.removeOnFailureListener(it) }
            onSuccessListener?.let { downloadTask.removeOnSuccessListener(it) }
            onProgressListener?.let { downloadTask.removeOnProgressListener(it) }
        }
    }

    private fun showDeleteFileAlertDialog(destinationFile: File, downloadBarView: DownloadBarView) {
        AlertDialog.Builder(activity)
                .setMessage(activity.getString(R.string.do_you_want_to_delete_this_file_from_your_device))
                .setPositiveButton(activity.getString(R.string.yes), { _, _ ->
                    doIfPermitted{ destinationFile.delete() }
                    setupDownloadBarWithFileInfo(destinationFile, downloadBarView)
                }).setNegativeButton(activity.getString(R.string.no), { dialogInterface, _ ->
            dialogInterface.cancel()
        }).show()
    }

    private fun doIfPermitted(action: () -> Unit) {
        val writePermission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (writePermission != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    LessonViewerActivity.WRITE_STORAGE_PERMISSION_CODE)

        } else {
            action()
        }
    }
}