package org.jnanaprabodhini.happyteacher.extension

import android.app.Activity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageTask
import org.jnanaprabodhini.happyteacher.prefs

fun FileDownloadTask.addOnSuccessListenerIfNotNull(activity: Activity, onSuccessListener: OnSuccessListener<FileDownloadTask.TaskSnapshot>?): StorageTask<FileDownloadTask.TaskSnapshot> {
    if (onSuccessListener != null) {
        return addOnSuccessListener(activity, onSuccessListener)
    } else {
        return this
    }
}

fun FileDownloadTask.addOnFailureListenerIfNotNull(activity: Activity, onFailureListener: OnFailureListener?): StorageTask<FileDownloadTask.TaskSnapshot> {
    if (onFailureListener != null) {
        return addOnFailureListener(activity, onFailureListener)
    } else {
        return this
    }
}

fun FileDownloadTask.addOnProgressListenerIfNotNull(activity: Activity, onProgressListener: OnProgressListener<FileDownloadTask.TaskSnapshot>?): StorageTask<FileDownloadTask.TaskSnapshot> {
    if (onProgressListener != null) {
        return addOnProgressListener(activity, onProgressListener)
    } else {
        return this
    }
}