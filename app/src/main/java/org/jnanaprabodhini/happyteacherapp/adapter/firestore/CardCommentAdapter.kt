package org.jnanaprabodhini.happyteacherapp.adapter.firestore

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerOptions
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
class CardCommentAdapter(options: FirestoreRecyclerOptions<CardComment>, dataObserver: FirebaseDataObserver, activity: Activity):
        FirestoreObserverRecyclerAdapter<CardComment, CardCommentViewHolder>(options, dataObserver) {

    private val dateFormat by lazy {
        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, LocaleManager.getCurrentLocale(activity))
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CardCommentViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_comment, parent, false)
        return CardCommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardCommentViewHolder?, position: Int, model: CardComment?) {
        // TODO: Allow editing!

        holder?.apply {
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
        }
    }
}