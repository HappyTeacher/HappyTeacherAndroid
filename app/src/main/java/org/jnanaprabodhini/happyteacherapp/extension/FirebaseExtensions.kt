package org.jnanaprabodhini.happyteacherapp.extension

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageTask
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.service.FirebaseRegistrationTokenService
import org.jnanaprabodhini.happyteacherapp.util.PreferencesManager

fun FileDownloadTask.addOnSuccessListenerIfNotNull(activity: Activity, onSuccessListener: OnSuccessListener<FileDownloadTask.TaskSnapshot>?): StorageTask<FileDownloadTask.TaskSnapshot> {
    return if (onSuccessListener != null) {
        addOnSuccessListener(activity, onSuccessListener)
    } else {
        this
    }
}

fun FileDownloadTask.addOnFailureListenerIfNotNull(activity: Activity, onFailureListener: OnFailureListener?): StorageTask<FileDownloadTask.TaskSnapshot> {
    return if (onFailureListener != null) {
        addOnFailureListener(activity, onFailureListener)
    } else {
        this
    }
}

fun FileDownloadTask.addOnProgressListenerIfNotNull(activity: Activity, onProgressListener: OnProgressListener<FileDownloadTask.TaskSnapshot>?): StorageTask<FileDownloadTask.TaskSnapshot> {
    return if (onProgressListener != null) {
        addOnProgressListener(activity, onProgressListener)
    } else {
        this
    }
}

fun FirebaseUser.hasCompleteContributorProfile(context: Context): Boolean {
    val prefs = PreferencesManager.getInstance(context)

    val hasName = !prefs.getUserName().isEmpty()
    val hasInstitution = !prefs.getUserInstitution().isEmpty()
    val hasLocation = !prefs.getUserLocation().isEmpty()

    return hasName && hasInstitution && hasLocation
}

fun FirebaseStorage.deleteIfAvailable(fileUrl: String) {
    try {
        getReferenceFromUrl(fileUrl).delete()
    } catch (e: IllegalArgumentException) {
        // File was not in our Firebase storage; do nothing.
    }
}

fun DocumentReference.slideOutViewAndDelete(context: Context, itemView: View) {
    // FirebaseUI query refreshes too fast to animate item removals
    //  so for now we run our own animation -- delete item after.
    val exitAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_out_right_quick)

    exitAnimation.onFinish {
        delete()
    }

    itemView.startAnimation(exitAnimation)
}

fun FirebaseAuth.signOutAndCleanup(context: Context) {
    val userId = this.currentUser?.uid

    userId?.let {
        // Remove token for receiving notifications
        FirebaseRegistrationTokenService.removeUserToken(context, userId)
    }

    val prefs = PreferencesManager.getInstance(context)
    prefs.clearUserRole()

    this.signOut()
}

/**
 * Add a snapshot listener that will execute only once, and only for existing documents.
 *  Takes an Activity for managing lifecycle.
 */
fun DocumentReference.addOneTimeExistingSnapshotListener(activity: Activity, listener: (DocumentSnapshot, FirebaseFirestoreException?) -> Unit) {
    val listeners = mutableSetOf<ListenerRegistration>()
    val registration = this.addSnapshotListener(activity, { documentSnapshot, firebaseFirestoreException ->
        if (documentSnapshot?.exists() == true) {
            listener(documentSnapshot, firebaseFirestoreException)
        }
        listeners.forEach(ListenerRegistration::remove)
    })
    listeners.add(registration)
}

/**
 * Add a snapshot listener that will execute only once, and only for existing documents
 */
fun DocumentReference.addOneTimeExistingSnapshotListener(listener: (DocumentSnapshot, FirebaseFirestoreException?) -> Unit) {
    Log.d("GRAHAM", "adding one time existing snapshot listener")
    val listeners = mutableSetOf<ListenerRegistration>()
    val registration = this.addSnapshotListener{ documentSnapshot, firebaseFirestoreException ->
        if (documentSnapshot?.exists() == true) {
            listener(documentSnapshot, firebaseFirestoreException)
        } else {
        }
        listeners.forEach(ListenerRegistration::remove)
    }
    listeners.add(registration)
}

/**
 * Add a snapshot listener that will execute only once
 */
fun DocumentReference.addOneTimeSnapshotListener(activity: Activity, listener: (DocumentSnapshot, FirebaseFirestoreException?) -> Unit) {
    val listeners = mutableSetOf<ListenerRegistration>()
    val registration = this.addSnapshotListener(activity, { documentSnapshot, firebaseFirestoreException ->
        listener(documentSnapshot, firebaseFirestoreException)
        listeners.forEach(ListenerRegistration::remove)
    })
    listeners.add(registration)
}