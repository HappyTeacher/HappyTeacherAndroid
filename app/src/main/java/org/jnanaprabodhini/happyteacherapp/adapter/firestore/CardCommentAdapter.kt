package org.jnanaprabodhini.happyteacherapp.adapter.firestore

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.adapter.viewholder.CardCommentViewHolder
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.model.CardComment
import org.jnanaprabodhini.happyteacherapp.util.LocaleManager
import java.text.DateFormat

/**
 * Created by grahamearley on 11/21/17.
 */
class CardCommentAdapter(options: FirestoreRecyclerOptions<CardComment>,
                         dataObserver: FirebaseDataObserver,
                         activity: Activity,
                         private val onCommentEdit: (comment: CardComment, ref: DocumentReference) -> Unit,
                         private val onCommentDelete: (ref: DocumentReference) -> Unit):
        FirestoreObservableRecyclerAdapter<CardComment, CardCommentViewHolder>(options, dataObserver) {

    private val dateFormat by lazy {
        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, LocaleManager.getCurrentLocale(activity))
    }

    private val currentUserId by lazy {
        FirebaseAuth.getInstance().currentUser?.uid
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CardCommentViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_comment, parent, false)
        return CardCommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardCommentViewHolder?, position: Int, model: CardComment?) {
        // TODO: Allow editing!

        holder?.apply {
            val commentRef = snapshots.getSnapshot(position).reference

            commenterNameTextView.text = model?.commenterName

            if (model?.reviewerComment == true) {
                commenterRoleTextView.setVisible()
                setTextColorForReviewer()
            } else {
                commenterRoleTextView.setVisibilityGone()
                setTextColorForAuthor()
            }

            model?.dateUpdated?.let {
                commentTimeTextView.setVisible()
                commentTimeTextView.text = dateFormat.format(it)
            } ?: commentTimeTextView.setVisibilityGone()

            commentTextView.text = model?.commentText

            if (!currentUserId.isNullOrEmpty() && model != null
                    && currentUserId == model.commenterId
                    && !model.locked) {
                showEditButtons()

                holder.editButton.setOnClickListener {
                    onCommentEdit(model, commentRef)
                }

                holder.deleteButton.setOnClickListener {
                    onCommentDelete(commentRef)
                }
            } else {
                hideEditButtons()
            }
        }
    }
}