package org.jnanaprabodhini.happyteacherapp.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_card_list_content_viewer.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacherapp.adapter.contentlist.ResourceRecyclerAdapter
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.extension.setDrawableResource
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.extension.toItalicizedSpan
import org.jnanaprabodhini.happyteacherapp.model.ResourceHeader
import org.jnanaprabodhini.happyteacherapp.util.ResourceStatus
import org.jnanaprabodhini.happyteacherapp.util.ResourceType
import java.io.File

abstract class ResourceActivity : HappyTeacherActivity(), FirebaseDataObserver {

    companion object {
        const val WRITE_STORAGE_PERMISSION_CODE = 1

        const val CONTENT_REF_PATH: String = "CONTENT_REF_PATH"
        fun Intent.getContentRefPath(): String = getStringExtra(CONTENT_REF_PATH)
        fun Intent.hasContentRefPath(): Boolean = hasExtra(CONTENT_REF_PATH)

        const val HEADER: String = "HEADER"
        fun Intent.getHeader(): ResourceHeader = getParcelableExtra(HEADER)
        fun Intent.hasHeader(): Boolean = hasExtra(HEADER)
    }

    private val hasHeader by lazy {intent.hasHeader()}
    private val hasContentRefPath by lazy { intent.hasContentRefPath() }

    protected var header: ResourceHeader = ResourceHeader()
    protected val contentRef by lazy { firestoreRoot.document(intent.getContentRefPath()) }
    protected val cardsRef by lazy { contentRef.collection(getString(R.string.cards)) }

    abstract val cardRecyclerAdapter: ResourceRecyclerAdapter

    protected val attachmentDestinationDirectory by lazy {
        // This directory will be used to store any attachments downloaded from this resource.
        File(Environment.getExternalStorageDirectory().path
                + File.separator
                + getString(R.string.app_name)
                + File.separator
                + header.subjectName + File.separator + header.topicName + File.separator + header.name)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_list_content_viewer)

        if (hasHeader) {
            header = intent.getHeader()

            setHeaderView()
            initializeUiForContentFromDatabase()
        } else {
            loadHeader()
        }
    }

    private fun loadHeader() {
        subtopicChoiceProgressBar.setVisible()
        contentRef.get().addOnSuccessListener { documentSnapshot ->
            header = documentSnapshot.toObject(ResourceHeader::class.java)
            setHeaderView()
            initializeUiForContentFromDatabase()
        }
    }

    open fun initializeUiForContentFromDatabase() {
        subtopicChoiceProgressBar.setVisible()
        initializeUiForContent()
    }

    private fun initializeUiForContent() {
        subtopicChoiceProgressBar.setVisibilityGone()
        initializeRecyclerView()
    }

    protected fun updateActionBarHeader() {
        if (header.name.isNotEmpty()) {
            supportActionBar?.title = header.name
        } else {
            supportActionBar?.title = getString(R.string.no_name_parenthesized).toItalicizedSpan()
        }
    }

    open fun setHeaderView() {
        updateActionBarHeader()

        headerView.setVisible()

        subjectTextView.text = header.subjectName
        authorNameTextView.text = header.authorName
        institutionTextView.text = header.authorInstitution
        locationTextView.text = header.authorLocation

        if (header.resourceType == ResourceType.CLASSROOM_RESOURCE) {
            headerView.setBackgroundResource(R.color.deepGrassGreen)
            icon.setDrawableResource(R.drawable.ic_tv_video_white_24dp)
        }

        when (header.status) {
            ResourceStatus.CHANGES_REQUESTED -> supportActionBar?.subtitle = getString(R.string.changes_requested)
            ResourceStatus.AWAITING_REVIEW -> supportActionBar?.subtitle = getString(R.string.submitted_for_review)
        }
    }

    open fun initializeRecyclerView() {
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
        subtopicChoiceProgressBar.setVisible()
        cardRecyclerView.setVisibilityGone()
    }

    override fun onDataLoaded() {
        subtopicChoiceProgressBar.setVisibilityGone()
        cardRecyclerView.setVisible()
    }

}

