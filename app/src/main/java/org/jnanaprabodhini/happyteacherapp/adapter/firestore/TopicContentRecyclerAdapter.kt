package org.jnanaprabodhini.happyteacherapp.adapter.firestore

import android.support.annotation.StringRes
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
import org.jnanaprabodhini.happyteacherapp.view.HorizontalPagerView

/**
 * An adapter for displaying Topics and the lessons for those topics.
 */
abstract class TopicContentRecyclerAdapter(topicsAdapterOptions: FirestoreRecyclerOptions<Topic>,
                                           topicsDataObserver: FirebaseDataObserver,
                                           val showSubmissionCount: Boolean,
                                           activity: HappyTeacherActivity,
                                           @StringRes val emptyTextStringRes: Int = R.string.there_are_no_lessons_for_this_topic_yet,
                                           private val standardLevel: Int? = null):
        TopicsRecyclerAdapter<ResourceHeaderRecyclerViewHolder>(topicsAdapterOptions, topicsDataObserver, activity) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ResourceHeaderRecyclerViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_content_header_recycler, parent, false)
        return ResourceHeaderRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResourceHeaderRecyclerViewHolder?, position: Int, model: Topic?) {
        holder?.titleTextView?.text = model?.name

        setBackgroundColor(holder?.itemView, position)

        val topicId = snapshots.getSnapshot(position).reference.id
        initializeLessonRecyclerView(holder?.horizontalPagerView, topicId, holder)
    }

    private fun initializeLessonRecyclerView(horizontalPagerView: HorizontalPagerView?, topicId: String,
                                             holder: ResourceHeaderRecyclerViewHolder?) {
        val adapterOptions = getSubtopicAdapterOptions(topicId)

        val adapter = ResourceHeaderRecyclerAdapter(adapterOptions, showSubmissionCount, activity,
                getSubtopicDataObserverForViewHolder(holder, topicId))

        adapter.startListening()
        horizontalPagerView?.setAdapter(adapter)
    }

    private fun getSubtopicDataObserverForViewHolder(viewHolder: ResourceHeaderRecyclerViewHolder?,
                                                     topicId: String) = object: FirebaseDataObserver {
        override fun onRequestNewData() {
            viewHolder?.horizontalPagerView?.setVisibilityGone()
            viewHolder?.hideEmptyViews()
            viewHolder?.progressBar?.setVisible()
        }

        override fun onDataLoaded() {
            viewHolder?.progressBar?.setVisibilityGone()
        }

        override fun onDataEmpty() {
            viewHolder?.horizontalPagerView?.setVisibilityGone()
            viewHolder?.showEmptyViewWithContributeButton(ResourceType.LESSON, topicId, activity)
            viewHolder?.statusTextView?.setText(emptyTextStringRes)

            standardLevel?.let { viewHolder?.statusTextView?.text = activity.getString(R.string.no_lessons_at_level_yet, it) }
        }

        override fun onDataNonEmpty() {
            viewHolder?.horizontalPagerView?.setVisible()
            viewHolder?.hideEmptyViews()
        }

        override fun onError(e: FirebaseFirestoreException?) {
            viewHolder?.horizontalPagerView?.setVisibilityGone()
            viewHolder?.contributeButton?.setInvisible()
            viewHolder?.statusTextView?.setVisible()
            viewHolder?.progressBar?.setVisibilityGone()
            viewHolder?.statusTextView?.setText(R.string.there_was_an_error_loading_lessons_for_this_topic)
        }
    }

    abstract fun getSubtopicAdapterOptions(topicId: String): FirestoreRecyclerOptions<ResourceHeader>
}