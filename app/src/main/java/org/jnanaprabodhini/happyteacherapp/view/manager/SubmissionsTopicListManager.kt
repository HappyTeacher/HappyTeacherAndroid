package org.jnanaprabodhini.happyteacherapp.view.manager

import android.support.v7.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacherapp.adapter.firestore.TopicContentRecyclerAdapter
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.model.ResourceHeader
import org.jnanaprabodhini.happyteacherapp.util.FirestoreKeys
import org.jnanaprabodhini.happyteacherapp.util.ResourceStatus

/**
 * A [TopicListManager] for displaying submissions (for moderator viewing only)
 *  for each topic.
 */
class SubmissionsTopicListManager(topicListRecycler: RecyclerView, activity: HappyTeacherActivity, dataObserver: FirebaseDataObserver):
        TopicListManager(topicListRecycler, activity, dataObserver) {

    override fun updateListOfTopicsForSubject(subjectKey: String) {
        val topicAdapterOptions = getTopicAdapterOptionsForSubject(subjectKey)

        val topicAdapter = object: TopicContentRecyclerAdapter(topicAdapterOptions, showSubmissionCount = false,
                topicsDataObserver = dataObserver, activity = activity) {
            override fun getSubtopicAdapterOptions(topicId: String): FirestoreRecyclerOptions<ResourceHeader> {
                val query: Query = firestoreLocalized.collection(activity.getString(R.string.resources))
                        .whereEqualTo(activity.getString(R.string.topic), topicId)
                        .whereEqualTo(FirestoreKeys.STATUS, ResourceStatus.AWAITING_REVIEW)

                return FirestoreRecyclerOptions.Builder<ResourceHeader>().setQuery(query, ResourceHeader::class.java).build()
            }
        }

        topicAdapter.startListening()
        topicListRecycler.adapter = topicAdapter
    }
}