package org.jnanaprabodhini.happyteacherapp.service

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.util.FirestoreKeys


/**
 * Service for monitoring changes in user registration token
 *  used for sending notifications. Includes a companion object
 *  with methods for updating/removing user token, used elsewhere
 *  in the app (on sign in, sign out).
 *
 *  See https://github.com/firebase/quickstart-android/blob/master/messaging/app/src/main/java/com/google/firebase/quickstart/fcm/MyFirebaseInstanceIDService.java
 */
class FirebaseRegistrationTokenService : FirebaseInstanceIdService() {

    companion object {
        fun updateUserToken(context: Context) {
            val auth = FirebaseAuth.getInstance()
            val firestoreRoot = FirebaseFirestore.getInstance()
            val usersCollection = firestoreRoot.collection(context.getString(R.string.users))

            val currentUserId = auth.currentUser?.uid
            val token = FirebaseInstanceId.getInstance().token

            if (currentUserId != null && token != null) {
                usersCollection.document(currentUserId)
                        .update(FirestoreKeys.REGISTRATION_TOKEN, token)
            }
        }

        fun removeUserToken(context: Context, userId: String) {
            val firestoreRoot = FirebaseFirestore.getInstance()
            val usersCollection = firestoreRoot.collection(context.getString(R.string.users))

            usersCollection.document(userId)
                    .update(FirestoreKeys.REGISTRATION_TOKEN, null)
        }
    }

    override fun onTokenRefresh() {
        updateUserToken(this)
    }
}