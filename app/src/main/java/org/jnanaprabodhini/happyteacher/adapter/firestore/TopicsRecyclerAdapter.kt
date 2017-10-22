package org.jnanaprabodhini.happyteacher.adapter.firestore

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentReference
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.adapter.viewholder.ContentHeaderRecyclerViewHolder
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader
import org.jnanaprabodhini.happyteacher.model.Topic
import org.jnanaprabodhini.happyteacher.view.HorizontalPagerRecyclerView

/**
 * Created by grahamearley on 10/11/17.
 */
abstract class TopicsRecyclerAdapter(topicsAdapterOptions: FirestoreRecyclerOptions<Topic>,
                                     topicsDataObserver: FirebaseDataObserver,
                                     val activity: Activity):
        FirestoreObserverRecyclerAdapter<Topic, ContentHeaderRecyclerViewHolder>(topicsAdapterOptions, topicsDataObserver) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ContentHeaderRecyclerViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_content_header_recycler, parent, false)
        return ContentHeaderRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContentHeaderRecyclerViewHolder?, position: Int, model: Topic?) {
        holder?.titleTextView?.text = model?.name

        // Alternate between these four colors:
        when (position % 4) {
            0 -> holder?.itemView?.setBackgroundResource(R.color.seaTeal)
            1 -> holder?.itemView?.setBackgroundResource(R.color.grassGreen)
            2 -> holder?.itemView?.setBackgroundResource(R.color.bubbleGumPink)
            3 -> holder?.itemView?.setBackgroundResource(R.color.dreamsicleOrange)
        }

        val topicId = snapshots.getSnapshot(position).reference.id
        initializeChildRecyclerView(holder?.horizontalRecyclerView, topicId, model, holder)
    }

    private fun initializeChildRecyclerView(recyclerView: HorizontalPagerRecyclerView?, topicId: String, model: Topic?, holder: ContentHeaderRecyclerViewHolder?) {
        val adapterOptions = getSubtopicAdapterOptions(topicId)
        val adapter = LessonHeaderRecyclerAdapter(model?.name ?: "", adapterOptions, activity, getSubtopicDataObserverForViewHolder(holder))

        adapter.startListening()
        recyclerView?.setAdapter(adapter)
    }

    abstract fun getSubtopicAdapterOptions(topicId: String): FirestoreRecyclerOptions<CardListContentHeader>

    private fun getSubtopicDataObserverForViewHolder(topicViewHolder: ContentHeaderRecyclerViewHolder?, level: Int? = null) = object: FirebaseDataObserver {
        override fun onRequestNewData() {
            topicViewHolder?.progressBar?.setVisible()
        }

        override fun onDataLoaded() {
            topicViewHolder?.progressBar?.setVisibilityGone()
        }

        override fun onDataEmpty() {
            topicViewHolder?.horizontalRecyclerView?.setVisibilityGone()
            topicViewHolder?.emptyView?.setVisible()
            topicViewHolder?.emptyTextView?.setText(R.string.there_are_no_lessons_for_this_topic_yet)

            level?.let { topicViewHolder?.emptyTextView?.text = activity.getString(R.string.no_lessons_at_level_yet, level) }
        }

        override fun onDataNonEmpty() {
            topicViewHolder?.horizontalRecyclerView?.setVisible()
            topicViewHolder?.emptyView?.setVisibilityGone()
        }
    }
}