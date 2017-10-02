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
import org.jnanaprabodhini.happyteacher.extension.showToast
import org.jnanaprabodhini.happyteacher.extension.toMegabyteFromByte
import org.jnanaprabodhini.happyteacher.view.DownloadBarView
import java.io.File

/**
 * Created by grahamearley on 10/1/17.
 */
class AttachmentDownloadManager(attachmentUrl: String, val attachmentDestinationDirectory: File, val activity: Activity) {

    val fileRef = FirebaseStorage.getInstance().getReferenceFromUrl(attachmentUrl)
    lateinit var metadata: StorageMetadata

    fun bindView(downloadBarView: DownloadBarView) {
        downloadBarView.setLoadingWithText(activity.getString(R.string.loading_attachment))
        fileRef.metadata.addOnSuccessListener { storageMetadata ->
            metadata = storageMetadata
            initializeDownloadBarOnClickListener(downloadBarView)
        }.addOnFailureListener { e -> e.printStackTrace() }
        // todo: add metadata failure listener.. but really -- build metadata into the model so we don't have to do this
    }

    private fun initializeDownloadBarOnClickListener(downloadBarView: DownloadBarView) {
        val fileExtension = fileRef.name.split(".").last()
        val fileName = fileRef.name.removeSuffix("." + fileExtension)

        val destinationFile = File(attachmentDestinationDirectory, "${fileName}_${metadata.updatedTimeMillis}.$fileExtension")

        if (destinationFile.exists() && destinationFile.length() == metadata.sizeBytes) {
            // ==> File is downloaded completely:
            setAttachmentOpenable(destinationFile, downloadBarView)
        } else if (destinationFile.exists() && fileRef.activeDownloadTasks.isNotEmpty()) {
            // ==> Download is still in progress:
            syncViewWithDownloadTask(fileRef.activeDownloadTasks.first(), destinationFile, downloadBarView)
        } else {
            setViewToDownload(destinationFile, downloadBarView)
        }
    }

    private fun setViewToDownload(destinationFile: File, downloadBarView: DownloadBarView) {
        downloadBarView.setLoadingWithText(activity.getString(R.string.loading_attachment))
        fileRef.metadata.addOnSuccessListener { storageMetadata ->
            metadata = storageMetadata
            setupDownloadBarWithFileInfo(destinationFile, downloadBarView)
        }
    }

    private fun setupDownloadBarWithFileInfo(destinationFile: File, downloadBarView: DownloadBarView) {
        downloadBarView.setDownloadIconWithText(activity.getString(R.string.file_with_size_in_mb, fileRef.name, metadata.sizeBytes.toMegabyteFromByte()))
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
        downloadBarView.setLoadingWithText(activity.getString(R.string.downloading))

        val onSuccess = OnSuccessListener<FileDownloadTask.TaskSnapshot> { setAttachmentOpenable(destinationFile, downloadBarView) }
        val onFailure = OnFailureListener {
            destinationFile.delete()
            if (!downloadTask.isCanceled) downloadBarView.setErrorWithText(activity.getString(R.string.download_failed))
        }
        val onProgress = OnProgressListener<FileDownloadTask.TaskSnapshot> { snapshot ->
            if (!downloadTask.isCanceled) updateProgressUi(snapshot?.bytesTransferred ?: 0, snapshot?.totalByteCount ?: 1, downloadBarView)
        }

        downloadBarView.setOneTimeOnClickListener {
            downloadTask.removeOnSuccessListener(onSuccess).removeOnFailureListener(onFailure).removeOnProgressListener(onProgress)

            cancelDownload(downloadTask, destinationFile, downloadBarView)
            setupDownloadBarWithFileInfo(destinationFile, downloadBarView)
        }

        downloadTask.addOnSuccessListener(activity, onSuccess)
                .addOnFailureListener(activity, onFailure)
                .addOnProgressListener(activity, onProgress)
    }

    private fun cancelDownload(downloadTask: FileDownloadTask, destinationFile: File, downloadBarView: DownloadBarView) {
        downloadBarView.setText(activity.getString(R.string.canceling))

        downloadTask.cancel()
        destinationFile.delete()

        activity.showToast(R.string.download_canceled)
    }

    private fun setAttachmentOpenable(destinationFile: File, downloadBarView: DownloadBarView) {
        downloadBarView.setProgressComplete()
        downloadBarView.setFolderIconWithText(activity.getString(R.string.open_x,  fileRef.name))
        downloadBarView.setOneTimeOnClickListener {
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
            openFileIntent.setDataAndType(downloadedFileUri, metadata.contentType)
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