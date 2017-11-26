package org.jnanaprabodhini.happyteacherapp.service

import android.app.NotificationManager
import android.media.RingtoneManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.FirebaseMessagingService
import org.jnanaprabodhini.happyteacherapp.R
import android.support.v4.app.NotificationCompat
import android.support.v4.content.res.ResourcesCompat
import org.jnanaprabodhini.happyteacherapp.activity.*
import org.jnanaprabodhini.happyteacherapp.adapter.contribute.ContributeFragmentAdapter
import org.jnanaprabodhini.happyteacherapp.util.FirestoreKeys
import org.jnanaprabodhini.happyteacherapp.util.ResourceStatus
import org.jnanaprabodhini.happyteacherapp.util.ResourceType


/**
 * Service for displaying notifications from Firebase Cloud Messaging.
 */
class FirebaseNotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        val status = remoteMessage?.data?.get(FirestoreKeys.STATUS)
        val refPath = remoteMessage?.data?.get(FirestoreKeys.REFERENCE_PATH)
        val resourceName = remoteMessage?.data?.get(FirestoreKeys.RESOURCE_NAME)
        val type = remoteMessage?.data?.get(FirestoreKeys.RESOURCE_TYPE)

        // Ensure all values exist.
        if (refPath == null || resourceName == null || type == null || status == null) {
            return
        }

        when (status) {
            ResourceStatus.PUBLISHED -> sendPublishedNotification(resourceName, type, refPath)
            ResourceStatus.CHANGES_REQUESTED -> sendChangesRequestedNotification(resourceName, type, refPath)
        }
    }

    private fun sendPublishedNotification(resourceName: String, type: String, refPath: String) {
        val messageTitle = getString(R.string.submission_published)

        // Published notifications lead to the resource viewer for this resource.
        when (type) {
            ResourceType.CLASSROOM_RESOURCE -> {
                val messageBody = getString(R.string.your_classroom_resource_x_was_published, resourceName)
                sendResourceNotification(messageTitle, messageBody, refPath,
                        R.color.grassGreen, R.drawable.ic_check_white_24dp, ClassroomResourceViewerActivity::class.java)
            }
            ResourceType.LESSON -> {
                val messageBody = getString(R.string.your_lesson_plan_x_was_published, resourceName)
                sendResourceNotification(messageTitle, messageBody, refPath,
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
                        R.color.dreamsicleOrange, R.drawable.ic_assignment_return_white_24dp)
            }
            ResourceType.LESSON -> {
                val messageBody = getString(R.string.your_lesson_plan_x_has_changes_requested, resourceName)
                sendNotification(messageTitle, messageBody, pendingIntent, refPath,
                        R.color.dreamsicleOrange, R.drawable.ic_assignment_return_white_24dp)
            }
        }

    }

    private fun sendResourceNotification(messageTitle: String, messageBody: String,
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

        sendNotification(messageTitle, messageBody, pendingIntent, refPath, color, icon)
    }

    private fun sendNotification(messageTitle: String, messageBody: String,
                                 pendingIntent: PendingIntent,
                                 notificationTag: String,
                                 @ColorRes color: Int,
                                 @DrawableRes icon: Int) {
        val notificationColor = ResourcesCompat.getColor(resources, color, null)

        val expandableNotificationStyle = NotificationCompat.BigTextStyle()
        expandableNotificationStyle.setBigContentTitle(messageTitle)
        expandableNotificationStyle.bigText(messageBody)

        val channelId = getString(R.string.resource_update_notification_channel_id)
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

}