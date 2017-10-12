package org.jnanaprabodhini.happyteacher.adapter.contentlist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerOptions
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.parent.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.adapter.ClassroomResourcesHeaderRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.adapter.viewholder.ContentCardViewHolder
import org.jnanaprabodhini.happyteacher.adapter.viewholder.ContentHeaderRecyclerViewHolder
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader
import org.jnanaprabodhini.happyteacher.model.ContentCard
import java.io.File

class LessonPlanRecyclerAdapter(contentCardMap: Map<String, ContentCard>, attachmentDestinationDirectory: File, topicName: String, topicId: String, subtopicId: String, activity: HappyTeacherActivity):
        CardListContentRecyclerAdapter(contentCardMap, attachmentDestinationDirectory, topicName, topicId, subtopicId, activity) {
    companion object { val LESSON_CARD_VIEW_TYPE = 0; val CLASSROOM_RESOURCES_FOOTER_VIEW_TYPE = 1 }

    override fun getItemCount(): Int = super.getItemCount() + 1 // + 1 for footer view (classroom resources section)

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is ContentCardViewHolder) {
            onBindContentCardViewHolder(holder, position)
        } else if (holder is ContentHeaderRecyclerViewHolder) {
            onBindClassroomResourcesViewHolder(holder, position)
        }
    }

    private fun onBindClassroomResourcesViewHolder(holder: ContentHeaderRecyclerViewHolder, position: Int) {
        holder.itemView.setBackgroundResource(R.color.colorPrimaryDark)
        holder.titleTextView.setText(R.string.classroom_resources)

        val classroomResourceQuery = activity.databaseReference.child(activity.getString(R.string.classroom_resources_headers)).child(topicId).child(subtopicId)
        val adapterOptions = FirebaseRecyclerOptions.Builder<CardListContentHeader>()
                .setQuery(classroomResourceQuery, CardListContentHeader::class.java)
                .build()

        val adapter = ClassroomResourcesHeaderRecyclerAdapter(topicName, adapterOptions, activity, getClassroomResourcesDataObserver(holder))
        adapter.startListening()

        holder.horizontalRecyclerView.setAdapter(adapter)
    }

    private fun getClassroomResourcesDataObserver(holder: ContentHeaderRecyclerViewHolder): FirebaseDataObserver = object: FirebaseDataObserver {
        override fun onRequestNewData() {
            holder.progressBar.setVisible()
            holder.emptyView.setVisibilityGone()
            holder.horizontalRecyclerView.setVisibilityGone()
        }

        override fun onDataLoaded() {
            holder.progressBar.setVisibilityGone()
            holder.emptyView.setVisibilityGone()
            holder.horizontalRecyclerView.setVisible()
        }

        override fun onDataEmpty() {
            holder.progressBar.setVisibilityGone()
            holder.horizontalRecyclerView.setVisibilityGone()

            holder.emptyView.setVisible()
            holder.emptyTextView.setText(R.string.there_are_no_classroom_resources_for_this_lesson_yet)
        }
    }

}

