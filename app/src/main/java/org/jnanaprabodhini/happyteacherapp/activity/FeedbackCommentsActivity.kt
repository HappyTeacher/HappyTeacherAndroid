package org.jnanaprabodhini.happyteacherapp.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.InputType
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_feedback_comments.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacherapp.adapter.firestore.CardCommentAdapter
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.dialog.InputTextDialogBuilder
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.model.CardComment
import org.jnanaprabodhini.happyteacherapp.util.FirestoreKeys
import java.util.*

class FeedbackCommentsActivity : HappyTeacherActivity(), FirebaseDataObserver {

    companion object {
        fun launch(context: Context, cardRef: DocumentReference, isReviewer: Boolean) {
            val intent = Intent(context, FeedbackCommentsActivity::class.java)

            intent.apply {
                putExtra(CARD_REF_PATH, cardRef.path)
                putExtra(IS_REVIEWER, isReviewer)
            }

            context.startActivity(intent)
        }

        private const val CARD_REF_PATH = "CARD_REF_PATH"
        fun Intent.getCardRefPath(): String = getStringExtra(CARD_REF_PATH)

        private const val IS_REVIEWER = "IS_REVIEWER"
        fun Intent.isReviewer(): Boolean = getBooleanExtra(IS_REVIEWER, false)
    }

    private val cardRef by lazy {
        firestoreRoot.document(intent.getCardRefPath())
    }

    private val feedbackCollectionRef by lazy {
        cardRef.collection(FirestoreKeys.FEEDBACK)
    }

    private val isReviewer by lazy {
        intent.isReviewer()
    }

    private val adapter by lazy {
        val query = feedbackCollectionRef.orderBy(FirestoreKeys.DATE_UPDATED, Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<CardComment>()
                .setQuery(query, CardComment::class.java)
                .build()

        CardCommentAdapter(options,
                dataObserver = this, activity = this,
                onCommentEdit = this::showUpdateCommentDialog,
                onCommentDelete = this::showDeleteCommentDialog)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback_comments)

        initializeRecycler()
        setupFab()
        setUpdatePreviewCommentListener()
    }

    private fun initializeRecycler() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        commentsRecyclerView.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        dividerItemDecoration.setDrawable(ResourcesCompat.getDrawable(resources, R.drawable.divider_vertical, null)!!)
        commentsRecyclerView.addItemDecoration(dividerItemDecoration)

        adapter.startListening()
        commentsRecyclerView.adapter = adapter
    }

    private fun setupFab() {
        newCommentFab.setOnClickListener {
            showAddCommentDialog()
        }
    }

    private fun showAddCommentDialog() {
        val feedbackDialog = InputTextDialogBuilder(this)

        feedbackDialog.apply {
            setTitle(R.string.add_a_note)
            setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE)
            setPositiveButton(R.string.add, {dialog, commentText ->
                addComment(commentText)
                dialog.dismiss()
            })
            setNegativeButton(R.string.cancel, DialogInterface.OnClickListener {
                dialog, _ -> dialog.dismiss()
            })

            show()
        }
    }

    private fun showUpdateCommentDialog(comment: CardComment, commentRef: DocumentReference) {
        val feedbackDialog = InputTextDialogBuilder(this)

        feedbackDialog.apply {
            setTitle(R.string.edit_note)
            setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE)
            setInputText(comment.commentText)
            setPositiveButton(R.string.update, {dialog, commentText ->
                updateComment(commentText, commentRef)
                dialog.dismiss()
            })
            setNegativeButton(R.string.cancel, DialogInterface.OnClickListener {
                dialog, _ -> dialog.dismiss()
            })

            show()
        }
    }

    private fun showDeleteCommentDialog(commentRef: DocumentReference) {
        AlertDialog.Builder(this)
                .setMessage(R.string.do_you_want_to_delete_this_comment)
                .setPositiveButton(R.string.delete, { dialog, _ ->
                    deleteComment(commentRef)
                    dialog.dismiss()
                })
                .setNegativeButton(R.string.cancel, { dialog, _ ->
                    dialog.dismiss()
                })
                .show()
    }

    private fun addComment(commentText: String) {
        val comment = CardComment(
                commenterId = auth.currentUser?.uid.orEmpty(),
                commenterName = prefs.getUserName(),
                commentText = commentText,
                dateUpdated = Date(),
                reviewerComment = isReviewer
        )
        val newCommentRef = feedbackCollectionRef.document()
        newCommentRef.set(comment)
    }

    private fun updateComment(newCommentText: String, commentRef: DocumentReference) {
        val dateUpdated = Date()
        commentRef.update(mapOf(FirestoreKeys.COMMENT_TEXT to newCommentText,
                FirestoreKeys.DATE_UPDATED to dateUpdated))
        adapter.notifyDataSetChanged()
    }

    private fun deleteComment(commentRef: DocumentReference) {
        commentRef.delete()
        adapter.notifyDataSetChanged()
    }

    private fun setUpdatePreviewCommentListener() {
        // Listen for changes to the most recent non-locked reviewer comment
        //  and update the card's preview comment to show this (or to be empty)
        feedbackCollectionRef.whereEqualTo(FirestoreKeys.REVIEWER_COMMENT, true)
                .whereEqualTo(FirestoreKeys.LOCKED, false)
                .orderBy(FirestoreKeys.DATE_UPDATED, Query.Direction.DESCENDING)
                .addSnapshotListener(this, { querySnapshot, firebaseFirestoreException ->
                    querySnapshot?.documents?.firstOrNull()?.let { doc ->
                        val comment = doc.toObject(CardComment::class.java)
                        updateFeaturedCommentForCard(comment.commentText, doc.reference)
                    } ?: removeFeaturedCommentForCard()
                })
    }

    /**
     * Each card with feedback has a featured/preview comment appear on the card itself.
     *  This comment will be the most recently added comment from a reviewer.
     */
    private fun updateFeaturedCommentForCard(commentText: String, commentRef: DocumentReference) {
        cardRef.update(mapOf(FirestoreKeys.FEEDBACK_PREVIEW_COMMENT to commentText,
                FirestoreKeys.FEEDBACK_PREVIEW_COMMENT_PATH to commentRef.path))
    }

    private fun removeFeaturedCommentForCard() {
        cardRef.update(mapOf(FirestoreKeys.FEEDBACK_PREVIEW_COMMENT to "",
                FirestoreKeys.FEEDBACK_PREVIEW_COMMENT_PATH to ""))
    }

    override fun onRequestNewData() {
        subtopicChoiceProgressBar.setVisible()
        commentsRecyclerView.setVisibilityGone()
        statusTextView.setVisibilityGone()
    }

    override fun onDataLoaded() {
        subtopicChoiceProgressBar.setVisibilityGone()
        statusTextView.setVisibilityGone()
        newCommentFab.setVisible()
    }

    override fun onDataNonEmpty() {
        commentsRecyclerView.setVisible()
        statusTextView.setVisibilityGone()
    }

    override fun onDataEmpty() {
        commentsRecyclerView.setVisibilityGone()
        statusTextView.setVisible()
        statusTextView.setText(R.string.there_are_no_feedback_notes)
    }

    override fun onError(e: FirebaseFirestoreException?) {
        commentsRecyclerView.setVisibilityGone()
        subtopicChoiceProgressBar.setVisibilityGone()
        newCommentFab.setVisibilityGone()

        statusTextView.setVisible()
        statusTextView.setText(R.string.there_was_an_error_loading_feedback_notes)
    }
}
