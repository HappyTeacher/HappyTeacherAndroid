package org.jnanaprabodhini.happyteacher.adapter.contentlist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.adapter.helper.MovableViewContainer
import org.jnanaprabodhini.happyteacher.adapter.viewholder.ContentCardViewHolder
import org.jnanaprabodhini.happyteacher.model.ContentCard
import java.io.File

class EditableLessonRecyclerAdapter(options: FirestoreRecyclerOptions<ContentCard>, attachmentDestinationDirectory: File, subtopicId: String, activity: HappyTeacherActivity, dataObserver: FirebaseDataObserver):
        CardListContentRecyclerAdapter(options, attachmentDestinationDirectory, subtopicId, activity, dataObserver), MovableViewContainer {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int, model: ContentCard?) {
        if (holder is ContentCardViewHolder) {
            onBindContentCardViewHolder(holder, model)

            val cardRef = snapshots.getSnapshot(position).reference
            val parentContentId = cardRef.parent.parent.id
            holder.setupEditButtons(activity, cardRef, model ?: ContentCard(), parentContentId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val cardView = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_content_card, parent, false)
        return ContentCardViewHolder(cardView)
    }

    override fun onViewMoved(oldPosition: Int, newPosition: Int) {
        notifyItemMoved(oldPosition, newPosition)
    }

    override fun onViewSetDown(oldPosition: Int, newPosition: Int) {
        val orderedRefs = (0..snapshots.lastIndex).map { i -> snapshots.getSnapshot(i).reference }.toMutableList()
        val refToMove = orderedRefs[oldPosition]
        orderedRefs.removeAt(oldPosition)
        orderedRefs.add(newPosition, refToMove)

        // Update card order numbers
        orderedRefs.forEachIndexed { index, ref ->
            ref.update(activity.getString(R.string.order_number), index)
        }
    }

}