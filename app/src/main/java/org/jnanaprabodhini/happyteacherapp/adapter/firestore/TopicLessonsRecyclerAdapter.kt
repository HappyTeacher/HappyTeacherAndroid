package org.jnanaprabodhini.happyteacherapp.adapter.firestore

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.adapter.viewholder.ResourceHeaderRecyclerViewHolder
import org.jnanaprabodhini.happyteacherapp.extension.setInvisible
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.model.ResourceHeader
import org.jnanaprabodhini.happyteacherapp.model.Topic
import org.jnanaprabodhini.happyteacherapp.util.ResourceType
import org.jnanaprabodhini.happyteacherapp.view.HorizontalPagerRecyclerView

/**
 * An adapter for displaying Topics and the lessons for those topics.
 */
abstract class TopicLessonsRecyclerAdapter(topicsAdapterOptions: FirestoreRecyclerOptions<Topic>,
                                           topicsDataObserver: FirebaseDataObserver,
                                           val showSubmissionCount: Boolean,
                                           activity: HappyTeacherActivity): TopicsRecyclerAdapter<ResourceHeaderRecyclerViewHolder>(topicsAdapterOptions, topicsDataObserver, activity) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ResourceHeaderRecyclerViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_content_header_recycler, parent, false)
        return ResourceHeaderRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResourceHeaderRecyclerViewHolder?, position: Int, model: Topic?) {
        holder?.titleTextView?.text = model?.name

        setBackgroundColor(holder?.itemView, position)

        val topicId = snapshots.getSnapshot(position).reference.id
        initializeLessonRecyclerView(holder?.horizontalRecyclerView, topicId, holder)
    }

    private fun initializeLessonRecyclerView(recyclerView: HorizontalPagerRecyclerView?, topicId: String, holder: ResourceHeaderRecyclerViewHolder?) {
        val adapterOptions = getSubtopicAdapterOptions(topicId)

        val adapter = ResourceHeaderRecyclerAdapter(adapterOptions, showSubmissionCount, activity, getSubtopicDataObserverForViewHolder(holder))

        adapter.startListening()
        recyclerView?.setAdapter(adapter)
    }

    override fun getSubtopicDataObserverForViewHolder(viewHolder: ResourceHeaderRecyclerViewHolder?, level: Int?) = object: FirebaseDataObserver {
        override fun onRequestNewData() {
            viewHolder?.horizontalRecyclerView?.setVisibilityGone()
            viewHolder?.hideEmptyViews()
            viewHolder?.progressBar?.setVisible()
        }

        override fun onDataLoaded() {
            viewHolder?.progressBar?.setVisibilityGone()
        }

        override fun onDataEmpty() {
            viewHolder?.horizontalRecyclerView?.setVisibilityGone()
            viewHolder?.showEmptyViewWithContributeButton(ResourceType.LESSON, activity)
            viewHolder?.statusTextView?.setText(R.string.there_are_no_lessons_for_this_topic_yet)

            level?.let { viewHolder?.statusTextView?.text = activity.getString(R.string.no_lessons_at_level_yet, level) }
        }

        override fun onDataNonEmpty() {
            viewHolder?.horizontalRecyclerView?.setVisible()
            viewHolder?.hideEmptyViews()
        }

        override fun onError(e: FirebaseFirestoreException?) {
            viewHolder?.horizontalRecyclerView?.setVisibilityGone()
            viewHolder?.contributeButton?.setInvisible()
            viewHolder?.statusTextView?.setVisible()
            viewHolder?.progressBar?.setVisibilityGone()
            viewHolder?.statusTextView?.setText(R.string.there_was_an_error_loading_lessons_for_this_topic)
        }
    }

    abstract fun getSubtopicAdapterOptions(topicId: String): FirestoreRecyclerOptions<ResourceHeader>
}