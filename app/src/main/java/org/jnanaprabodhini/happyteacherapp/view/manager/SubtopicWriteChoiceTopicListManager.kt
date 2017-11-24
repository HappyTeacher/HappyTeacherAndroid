package org.jnanaprabodhini.happyteacherapp.view.manager

import android.support.v7.widget.RecyclerView
import org.jnanaprabodhini.happyteacherapp.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacherapp.adapter.contribute.TopicSubtopicsWriteChoiceAdapter
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver

/**
 * A [TopicListManager] for displaying subtopics that belong to each topic,
 *  and providing a button to contribute a resource to that subtopic.
 */
class SubtopicWriteChoiceTopicListManager(topicListRecycler: RecyclerView, val resourceType: String, activity: HappyTeacherActivity, dataObserver: FirebaseDataObserver):
        TopicListManager(topicListRecycler, activity, dataObserver) {

    override fun updateListOfTopicsForSubject(subjectKey: String) {
        val topicAdapterOptions = getTopicAdapterOptionsForSubject(subjectKey)

        val topicAdapter = TopicSubtopicsWriteChoiceAdapter(topicAdapterOptions, resourceType, dataObserver, activity)

        topicAdapter.startListening()
        topicListRecycler.adapter = topicAdapter
    }
}