package org.jnanaprabodhini.happyteacher.adapter.firestore

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.adapter.viewholder.ContentHeaderRecyclerViewHolder
import org.jnanaprabodhini.happyteacher.extension.setInvisible
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader
import org.jnanaprabodhini.happyteacher.model.Topic
import org.jnanaprabodhini.happyteacher.view.HorizontalPagerRecyclerView

/**
 * An adapter for displaying Topics and the lessons for those topics.
 */
abstract class TopicLessonsRecyclerAdapter(topicsAdapterOptions: FirestoreRecyclerOptions<Topic>,
                                           topicsDataObserver: FirebaseDataObserver,
                                           activity: Activity): TopicsRecyclerAdapter<CardListContentHeader, ContentHeaderRecyclerViewHolder>(topicsAdapterOptions, topicsDataObserver, activity) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ContentHeaderRecyclerViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_content_header_recycler, parent, false)
        return ContentHeaderRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContentHeaderRecyclerViewHolder?, position: Int, model: Topic?) {
        holder?.titleTextView?.text = model?.name

        setBackgroundColor(holder?.itemView, position)

        val topicId = snapshots.getSnapshot(position).reference.id
        initializeChildRecyclerView(holder?.horizontalRecyclerView, topicId, model, holder)
    }

    private fun initializeChildRecyclerView(recyclerView: HorizontalPagerRecyclerView?, topicId: String, model: Topic?, holder: ContentHeaderRecyclerViewHolder?) {
        val adapterOptions = getSubtopicAdapterOptions(topicId)

        val shouldShowSubmissionsCount = true
        val adapter = LessonHeaderRecyclerAdapter(model?.name ?: "", shouldShowSubmissionsCount, adapterOptions, activity, getSubtopicDataObserverForViewHolder(holder))

        adapter.startListening()
        recyclerView?.setAdapter(adapter)
    }

    override fun getSubtopicDataObserverForViewHolder(viewHolder: ContentHeaderRecyclerViewHolder?, level: Int?) = object: FirebaseDataObserver {
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
            viewHolder?.showEmptyViews()
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
}