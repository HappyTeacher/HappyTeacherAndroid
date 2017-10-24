package org.jnanaprabodhini.happyteacher.adapter.contentlist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.adapter.firestore.ClassroomResourcesHeaderRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.adapter.viewholder.ContentCardViewHolder
import org.jnanaprabodhini.happyteacher.adapter.viewholder.ContentHeaderRecyclerViewHolder
import org.jnanaprabodhini.happyteacher.extension.setInvisible
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader
import org.jnanaprabodhini.happyteacher.model.ContentCard
import java.io.File

class LessonPlanRecyclerAdapter(options: FirestoreRecyclerOptions<ContentCard>, attachmentDestinationDirectory: File, topicName: String, topicId: String, subtopicId: String, activity: HappyTeacherActivity, dataObserver: FirebaseDataObserver):
        CardListContentRecyclerAdapter(options, attachmentDestinationDirectory, topicName, topicId, subtopicId, activity, dataObserver) {

    companion object { val LESSON_CARD_VIEW_TYPE = 0; val CLASSROOM_RESOURCES_FOOTER_VIEW_TYPE = 1 }

    override fun getItemCount(): Int = super.getItemCount() + 1 // + 1 for footer view (classroom resources section)

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        if (viewType == CLASSROOM_RESOURCES_FOOTER_VIEW_TYPE) {
            val classroomResourcesView = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_content_header_recycler, parent, false)

            // Add margin to top of view:
            val margins = classroomResourcesView.layoutParams as ViewGroup.MarginLayoutParams
            margins.topMargin = activity.resources.getDimensionPixelSize(R.dimen.classroom_resources_list_top_padding)
            classroomResourcesView.layoutParams = margins

            return ContentHeaderRecyclerViewHolder(classroomResourcesView)
        } else {
            val cardView = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_content_card, parent, false)
            return ContentCardViewHolder(cardView)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == itemCount - 1) {
            return CLASSROOM_RESOURCES_FOOTER_VIEW_TYPE
        } else {
            return LESSON_CARD_VIEW_TYPE
        }
    }

    /**
     * We override this method (in addition to the abstract onBindViewHolder(holder, position, model) method)
     *  because this base method calls `getItem(position`, and this throws an error for the last item in the
     *  list because it is not an item in the list but rather the footer view.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (getItemViewType(position)) {
            LESSON_CARD_VIEW_TYPE -> onBindViewHolder(holder, position, getItem(position))
            CLASSROOM_RESOURCES_FOOTER_VIEW_TYPE -> onBindClassroomResourcesViewHolder(holder as ContentHeaderRecyclerViewHolder)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int, model: ContentCard?) {
        if (holder is ContentCardViewHolder) {
            onBindContentCardViewHolder(holder, model)
        }
    }

    private fun onBindClassroomResourcesViewHolder(holder: ContentHeaderRecyclerViewHolder) {
        holder.itemView.setBackgroundResource(R.color.colorPrimaryDark)
        holder.titleTextView.setText(R.string.classroom_resources)

        val classroomResourceQuery = activity.firestoreLocalized.collection(activity.getString(R.string.classroom_resources_key))
                .whereEqualTo(activity.getString(R.string.subtopic), subtopicId)
                .orderBy(activity.getString(R.string.name))

        val adapterOptions = FirestoreRecyclerOptions.Builder<CardListContentHeader>()
                .setQuery(classroomResourceQuery, CardListContentHeader::class.java)
                .build()

        val adapter = ClassroomResourcesHeaderRecyclerAdapter(topicName, adapterOptions, activity, getClassroomResourcesDataObserver(holder))
        adapter.startListening()

        holder.horizontalRecyclerView.setAdapter(adapter)
    }

    private fun getClassroomResourcesDataObserver(holder: ContentHeaderRecyclerViewHolder): FirebaseDataObserver = object: FirebaseDataObserver {
        override fun onRequestNewData() {
            holder.progressBar.setVisible()
            holder.hideEmptyViews()
            holder.horizontalRecyclerView.setVisibilityGone()
        }

        override fun onDataLoaded() {
            holder.progressBar.setVisibilityGone()
        }

        override fun onDataNonEmpty() {
            holder.hideEmptyViews()
            holder.horizontalRecyclerView.setVisible()
        }

        override fun onDataEmpty() {
            holder.horizontalRecyclerView.setVisibilityGone()

            holder.showEmptyViews()
            holder.statusTextView.setText(R.string.there_are_no_classroom_resources_for_this_lesson_yet)
        }

        override fun onError(e: FirebaseFirestoreException?) {
            holder.horizontalRecyclerView.setVisibilityGone()
            holder.contributeButton.setInvisible()

            holder.statusTextView.setVisible()
            holder.statusTextView.setText(R.string.there_was_an_error_loading_classroom_resources_for_this_lesson)
        }
    }

}

