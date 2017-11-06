package org.jnanaprabodhini.happyteacher.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.android.synthetic.main.activity_card_list_content_viewer.*
import kotlinx.android.synthetic.main.view_recycler_horizontal_pager.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.adapter.contentlist.CardListContentRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader
import java.io.File

abstract class CardListContentViewerActivity : HappyTeacherActivity(), FirebaseDataObserver {

    companion object {
        const val WRITE_STORAGE_PERMISSION_CODE = 1

        const val CONTENT_REF_PATH: String = "CONTENT_REF_PATH"
        fun Intent.getContentRefPath(): String = getStringExtra(CONTENT_REF_PATH)

        const val HEADER: String = "HEADER"
        fun Intent.getHeader(): CardListContentHeader = getParcelableExtra(HEADER)

        const val TOPIC_NAME: String = "TOPIC_NAME"
        fun Intent.getTopicName(): String = getStringExtra(TOPIC_NAME).orEmpty()
    }

    protected val topicName by lazy { intent.getTopicName() }
    protected val header by lazy { intent.getHeader() }
    protected val contentRef by lazy { firestoreRoot.document(intent.getContentRefPath()) }
    protected val cardsRef by lazy { contentRef.collection(getString(R.string.cards)) }

    abstract val cardRecyclerAdapter: CardListContentRecyclerAdapter

    protected val attachmentDestinationDirectory by lazy {
        // This directory will be used to store any attachments downloaded from this contentKey.
        File(Environment.getExternalStorageDirectory().path
                + File.separator
                + getString(R.string.app_name)
                + File.separator
                + header.subjectName + File.separator + topicName + File.separator + header.name)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_list_content_viewer)

        setHeaderView()
        initializeUiForContentFromDatabase()
    }

    private fun initializeUiForContentFromDatabase() {
        progressBar.setVisible()
        initializeUiForContent()
    }

    private fun initializeUiForContent() {
        progressBar.setVisibilityGone()
        initializeRecyclerView()
    }

    open fun setHeaderView() {
        headerView.setVisible()
        supportActionBar?.title = header.name

        subjectTextView.text = header.subjectName
        authorNameTextView.text = header.authorName
        institutionTextView.text = header.authorInstitution
        locationTextView.text = header.authorLocation
    }

    private fun initializeRecyclerView() {
        cardRecyclerView.layoutManager = LinearLayoutManager(this)
        cardRecyclerAdapter.startListening()
        cardRecyclerView?.adapter = cardRecyclerAdapter
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == WRITE_STORAGE_PERMISSION_CODE && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            // Re-draw items to reflect new permissions
            cardRecyclerView.adapter.notifyDataSetChanged()
        }
    }

    override fun onRequestNewData() {
        progressBar.setVisible()
    }

    override fun onDataLoaded() {
        progressBar.setVisibilityGone()
    }

    override fun onError(e: FirebaseFirestoreException?) {
        recyclerView.setVisibilityGone()
        statusTextView.setVisible()
        statusTextView.setText(R.string.there_was_an_error_loading_this_lesson)
    }

}

