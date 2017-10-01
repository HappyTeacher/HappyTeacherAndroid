package org.jnanaprabodhini.happyteacher.adapter.helper

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.support.v4.app.ActivityCompat
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
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
class AttachmentDownloadManager(attachmentUrl: String, val activity: Activity) {

    val fileRef = FirebaseStorage.getInstance().getReferenceFromUrl(attachmentUrl)
    lateinit var metadata: StorageMetadata

    fun initializeViewForDownload(downloadBarView: DownloadBarView) {
        downloadBarView.setLoadingWithText(activity.getString(R.string.loading_attachment))
        fileRef.metadata.addOnSuccessListener { storageMetadata ->
            metadata = storageMetadata
            setupDownloadBarWithFileInfo(downloadBarView)
        }
    }

    private fun setupDownloadBarWithFileInfo(downloadBarView: DownloadBarView) {
        val fileExtension = "." + fileRef.name.split(".").last()
        val fileName = fileRef.name.removeSuffix(fileExtension)

        downloadBarView.setDownloadIconWithText(activity.getString(R.string.file_with_size_in_mb, fileRef.name, metadata.sizeBytes.toMegabyteFromByte()))
        downloadBarView.resetProgress()

        downloadBarView.setOneTimeOnClickListener {
            downloadFileWithPermission(fileName, fileExtension, downloadBarView)
        }
    }

    private fun downloadFileWithPermission(fileName: String, fileExtension: String, downloadBarView: DownloadBarView) {
        val writePermission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (writePermission != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    LessonViewerActivity.WRITE_STORAGE_PERMISSION_CODE)

        } else {
            beginDownload(fileName, fileExtension, downloadBarView)
        }
    }

    private fun beginDownload(fileName: String, fileExtension: String, downloadBarView: DownloadBarView) {
        val destinationDirectory = File(Environment.getExternalStorageDirectory().path + File.separator + activity.getString(R.string.app_name))
        destinationDirectory.mkdirs()
        val destinationFile = File.createTempFile(fileName, fileExtension, destinationDirectory)

        // Calling `getFile(..)` starts the download:
        val downloadTask = fileRef.getFile(destinationFile)

        downloadBarView.setDownloadIconWithText(activity.getString(R.string.downloading))

        downloadBarView.setOneTimeOnClickListener {
            cancelDownload(downloadTask, destinationFile, downloadBarView)
            setupDownloadBarWithFileInfo(downloadBarView)
        }

        downloadTask.addOnSuccessListener {
            setAttachmentOpenable(destinationFile, downloadBarView)
        }.addOnFailureListener {
            destinationFile.delete()
        }.addOnProgressListener { snapshot ->
            updateProgressUi(snapshot?.bytesTransferred ?: 0, snapshot?.totalByteCount ?: 1, downloadBarView)
        }.addOnFailureListener {
            downloadBarView.setErrorWithText(activity.getString(R.string.download_failed))
        }
    }

    private fun cancelDownload(downloadTask: FileDownloadTask, destinationFile: File, downloadBarView: DownloadBarView) {
        downloadBarView.setText(activity.getString(R.string.cancelling))

        downloadTask.cancel()
        destinationFile.delete()
        activity.showToast(R.string.download_cancelled)

        downloadBarView.resetProgress()
    }

    private fun setAttachmentOpenable(destinationFile: File, downloadBarView: DownloadBarView) {
        downloadBarView.setFolderIconWithText(activity.getString(R.string.open_x,  fileRef.name))
        downloadBarView.setOneTimeOnClickListener {
            val downloadedFileUri = Uri.fromFile(destinationFile)
            val openFileIntent = Intent(Intent.ACTION_VIEW)
            openFileIntent.setDataAndType(downloadedFileUri, metadata.contentType)
            activity.startActivity(openFileIntent)
        }
    }

    private fun updateProgressUi(bytesTransferred: Long, totalBytes: Long, downloadBarView: DownloadBarView) {
        val progressRatio = bytesTransferred / totalBytes.toDouble()
        val percent = Math.abs(progressRatio)

        downloadBarView.setProgress(percent)
    }

}