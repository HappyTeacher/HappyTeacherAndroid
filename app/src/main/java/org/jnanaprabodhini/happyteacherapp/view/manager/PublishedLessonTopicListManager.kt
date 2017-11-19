package org.jnanaprabodhini.happyteacherapp.view.manager

import android.support.v7.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacherapp.adapter.firestore.TopicLessonsRecyclerAdapter
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.model.ResourceHeader
import org.jnanaprabodhini.happyteacherapp.util.ResourceStatus

/**
 * A [TopicListManager] for displaying a list of published lesson resources
 *  that belong to a topic.
 */
class PublishedLessonTopicListManager(topicListRecycler: RecyclerView, activity: HappyTeacherActivity, dataObserver: FirebaseDataObserver):
        TopicListManager(topicListRecycler, activity, dataObserver) {

    override fun updateListOfTopicsForSubject(subjectKey: String) {
        val topicAdapterOptions = getTopicAdapterOptionsForSubject(subjectKey)

        val topicAdapter = object: TopicLessonsRecyclerAdapter(topicAdapterOptions, showSubmissionCount = true,
                topicsDataObserver = dataObserver, activity = activity) {
            override fun getSubtopicAdapterOptions(topicId: String): FirestoreRecyclerOptions<ResourceHeader> {
                val query: Query = firestoreLocalized.collection(activity.getString(R.string.resources))
                        .whereEqualTo(activity.getString(R.string.resource_type), activity.getString(R.string.lesson))
                        .whereEqualTo(activity.getString(R.string.topic), topicId)
                        .whereEqualTo(activity.getString(R.string.status), ResourceStatus.PUBLISHED)
                        .whereEqualTo(activity.getString(R.string.is_featured), true)

                return FirestoreRecyclerOptions.Builder<ResourceHeader>().setQuery(query, ResourceHeader::class.java).build()
            }
        }

        topicAdapter.startListening()
        topicListRecycler.adapter = topicAdapter
    }
}