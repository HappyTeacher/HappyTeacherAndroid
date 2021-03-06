package org.jnanaprabodhini.happyteacherapp.service

import android.app.*
import android.media.RingtoneManager
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.FirebaseMessagingService
import org.jnanaprabodhini.happyteacherapp.R
import android.support.v4.app.NotificationCompat
import android.support.v4.content.res.ResourcesCompat
import android.util.Log
import org.jnanaprabodhini.happyteacherapp.activity.*
import org.jnanaprabodhini.happyteacherapp.adapter.contribute.ContributeFragmentAdapter
import org.jnanaprabodhini.happyteacherapp.util.FirestoreKeys
import org.jnanaprabodhini.happyteacherapp.util.NotificationType
import org.jnanaprabodhini.happyteacherapp.util.ResourceStatus
import org.jnanaprabodhini.happyteacherapp.util.ResourceType


/**
 * Service for displaying notifications from Firebase Cloud Messaging.
 */
class FirebaseNotificationService : FirebaseMessagingService() {

    companion object {
        const val RESOURCE_STATUS_UPDATE_CHANNEL = "RESOURCE_STATUS_UPDATE_CHANNEL"
        const val MODERATOR_NEW_SUBMISSION_CHANNEL = "MODERATOR_NEW_SUBMISSION_CHANNEL"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        // Ensure channels exist:
        createChannels()

        val notificationType = remoteMessage?.data?.get(FirestoreKeys.NOTIFICATION_TYPE)

        when (notificationType) {
            NotificationType.NEW_SUBMISSION_FOR_MODERATOR -> onSubmissionForModeratorReceived(remoteMessage)
            NotificationType.STATUS_CHANGE_FOR_AUTHOR -> onStatusChangeReceived(remoteMessage)
        }

    }

    private fun onSubmissionForModeratorReceived(remoteMessage: RemoteMessage?) {
        val subjectName = remoteMessage?.data?.get(FirestoreKeys.SUBJECT_NAME) ?: return
        val subjectId = remoteMessage.data?.get(FirestoreKeys.SUBJECT_ID) ?: return
        val parentSubjectId = remoteMessage.data?.get(FirestoreKeys.PARENT_SUBJECT_ID)

        val intent = Intent(this, SubmissionsForReviewActivity::class.java)

        if (parentSubjectId != null) {
            intent.putExtra(SubmissionsForReviewActivity.PARENT_SUBJECT_ID, parentSubjectId)
            intent.putExtra(SubmissionsForReviewActivity.CHILD_SUBJECT_ID, subjectId)
        } else {
            intent.putExtra(SubmissionsForReviewActivity.PARENT_SUBJECT_ID, subjectId)
        }

        // Set action unique to subject to prevent intent extras
        //  from getting overridden by newer notifications:
        intent.action = subjectId

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        sendNotification(getString(R.string.subject_new_submission, subjectName),
                getString(R.string.there_is_a_new_happy_teacher_submission_for_you_to_review),
                pendingIntent,
                subjectName,
                MODERATOR_NEW_SUBMISSION_CHANNEL,
                R.color.deepBlue,
                R.drawable.ic_book_white_24dp)
    }

    private fun onStatusChangeReceived(remoteMessage: RemoteMessage?) {
        val status = remoteMessage?.data?.get(FirestoreKeys.STATUS)
        val refPath = remoteMessage?.data?.get(FirestoreKeys.REFERENCE_PATH)
        val resourceName = remoteMessage?.data?.get(FirestoreKeys.RESOURCE_NAME)
        val resourceType = remoteMessage?.data?.get(FirestoreKeys.RESOURCE_TYPE)

        // Ensure all values exist.
        if (refPath == null || resourceName == null || resourceType == null || status == null) {
            return
        }

        when (status) {
            ResourceStatus.PUBLISHED -> sendPublishedNotification(resourceName, resourceType, refPath)
            ResourceStatus.CHANGES_REQUESTED -> sendChangesRequestedNotification(resourceName, resourceType, refPath)
        }
    }

    private fun sendPublishedNotification(resourceName: String, type: String, refPath: String) {
        val messageTitle = getString(R.string.submission_published)

        // Published notifications lead to the resource viewer for this resource.
        when (type) {
            ResourceType.CLASSROOM_RESOURCE -> {
                val messageBody = getString(R.string.your_classroom_resource_x_was_published, resourceName)
                sendResourcePublishedNotification(messageTitle, messageBody, refPath,
                        R.color.grassGreen, R.drawable.ic_check_white_24dp, ClassroomResourceViewerActivity::class.java)
            }
            ResourceType.LESSON -> {
                val messageBody = getString(R.string.your_lesson_plan_x_was_published, resourceName)
                sendResourcePublishedNotification(messageTitle, messageBody, refPath,
                        R.color.grassGreen, R.drawable.ic_check_white_24dp, LessonViewerActivity::class.java)
            }
        }

    }

    private fun sendChangesRequestedNotification(resourceName: String, type: String, refPath: String) {
        val messageTitle = getString(R.string.changes_requested_sentence_case)

        // Changes requested notifications will lead to the Submitted page of the contribute activity
        val intent = Intent(this, ContributeActivity::class.java)
        intent.putExtra(ContributeActivity.FRAGMENT_PAGE, ContributeFragmentAdapter.SUBMITTED_PAGE)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        when (type) {
            ResourceType.CLASSROOM_RESOURCE -> {
                val messageBody = getString(R.string.your_classroom_resource_x_has_changes_requested, resourceName)
                sendNotification(messageTitle, messageBody, pendingIntent, refPath,
                        RESOURCE_STATUS_UPDATE_CHANNEL,
                        R.color.dreamsicleOrange, R.drawable.ic_assignment_return_white_24dp)
            }
            ResourceType.LESSON -> {
                val messageBody = getString(R.string.your_lesson_plan_x_has_changes_requested, resourceName)
                sendNotification(messageTitle, messageBody, pendingIntent, refPath,
                        RESOURCE_STATUS_UPDATE_CHANNEL,
                        R.color.dreamsicleOrange, R.drawable.ic_assignment_return_white_24dp)
            }
        }

    }

    /**
     * Send a notification that opens a resource that has been published.
     */
    private fun sendResourcePublishedNotification(messageTitle: String, messageBody: String,
                                                  refPath: String,
                                                  @ColorRes color: Int,
                                                  @DrawableRes icon: Int,
                                                  destinationViewerActivity: Class<out ResourceActivity>) {

        val intent = Intent(this, destinationViewerActivity)
        intent.putExtra(ResourceActivity.CONTENT_REF_PATH, refPath)

        // Preserve navigation stack:
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(destinationViewerActivity)
        stackBuilder.addNextIntent(intent)

        val pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        sendNotification(messageTitle, messageBody, pendingIntent, refPath,
                RESOURCE_STATUS_UPDATE_CHANNEL, color, icon)
    }

    private fun sendNotification(messageTitle: String, messageBody: String,
                                 pendingIntent: PendingIntent,
                                 notificationTag: String,
                                 channelId: String,
                                 @ColorRes color: Int,
                                 @DrawableRes icon: Int) {
        val notificationColor = ResourcesCompat.getColor(resources, color, null)

        Log.d("GRAHAM", "Send notification called.")

        val expandableNotificationStyle = NotificationCompat.BigTextStyle()
        expandableNotificationStyle.setBigContentTitle(messageTitle)
        expandableNotificationStyle.bigText(messageBody)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(icon)
                .setStyle(expandableNotificationStyle)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setColor(notificationColor)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // The (tag, id) pair must be unique. We will ignore the ID number (set it to
        //  zero arbitrarily) and only create one notification per tag.
        val notificationIdNumber = 0
        notificationManager.notify(notificationTag, notificationIdNumber, notificationBuilder.build())
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            val statusUpdateChannel = NotificationChannel(RESOURCE_STATUS_UPDATE_CHANNEL,
                    getString(R.string.status_update_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT)
            statusUpdateChannel.apply {
                lightColor = Color.GREEN
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }
            notificationManager.createNotificationChannel(statusUpdateChannel)

            val modNewSubmissionChannel = NotificationChannel(MODERATOR_NEW_SUBMISSION_CHANNEL,
                    getString(R.string.moderator_new_submission_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT)
            modNewSubmissionChannel.apply {
                lightColor = Color.BLUE
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }
            notificationManager.createNotificationChannel(modNewSubmissionChannel)
        }
    }

}