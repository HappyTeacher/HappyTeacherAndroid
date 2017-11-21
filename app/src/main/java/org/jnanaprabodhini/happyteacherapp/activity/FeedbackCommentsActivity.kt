package org.jnanaprabodhini.happyteacherapp.activity

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.activity_feedback_comments.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacherapp.adapter.firestore.CardCommentAdapter
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.model.CardComment
import org.jnanaprabodhini.happyteacherapp.util.FirestoreKeys

class FeedbackCommentsActivity : HappyTeacherActivity(), FirebaseDataObserver {
    //todo: data observer!

    companion object {
        fun launch(from: Activity, resourceRef: DocumentReference) {
            val intent = Intent(from, FeedbackCommentsActivity::class.java)

            intent.apply {
                putExtra(RESOURCE_REF_PATH, resourceRef.path)
            }

            from.startActivity(intent)
        }

        private const val RESOURCE_REF_PATH = "RESOURCE_REF_PATH"
        fun Intent.getResourceRefPath(): String = getStringExtra(RESOURCE_REF_PATH)
    }

    private val resourceRef by lazy {
        firestoreRoot.document(intent.getResourceRefPath())
    }

    private val commentsCollectionRef by lazy {
        resourceRef.collection(FirestoreKeys.COMMENTS)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback_comments)

        initializeRecycler()
    }

    private fun initializeRecycler() {
        val layoutManager = LinearLayoutManager(this)
        commentsRecyclerView.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        dividerItemDecoration.setDrawable(ResourcesCompat.getDrawable(resources, R.drawable.divider_vertical, null)!!)
        commentsRecyclerView.addItemDecoration(dividerItemDecoration)

        val options = FirestoreRecyclerOptions.Builder<CardComment>()
                .setQuery(commentsCollectionRef, CardComment::class.java)
                .build()
        val adapter = CardCommentAdapter(options, this, this)
        adapter.startListening()

        commentsRecyclerView.adapter = adapter
    }
}
