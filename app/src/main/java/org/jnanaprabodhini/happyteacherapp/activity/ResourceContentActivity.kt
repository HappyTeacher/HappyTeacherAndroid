package org.jnanaprabodhini.happyteacherapp.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_card_list_content_viewer.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacherapp.adapter.contentlist.ResourceContentRecyclerAdapter
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.extension.setDrawableResource
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.extension.showToast
import org.jnanaprabodhini.happyteacherapp.model.ResourceHeader
import org.jnanaprabodhini.happyteacherapp.model.User
import org.jnanaprabodhini.happyteacherapp.util.FirestoreKeys
import org.jnanaprabodhini.happyteacherapp.util.ResourceStatus
import org.jnanaprabodhini.happyteacherapp.util.ResourceType
import org.jnanaprabodhini.happyteacherapp.util.UserRoles
import java.io.File

abstract class ResourceContentActivity : HappyTeacherActivity(), FirebaseDataObserver {

    companion object {
        const val WRITE_STORAGE_PERMISSION_CODE = 1

        const val CONTENT_REF_PATH: String = "CONTENT_REF_PATH"
        fun Intent.getContentRefPath(): String = getStringExtra(CONTENT_REF_PATH)

        const val HEADER: String = "HEADER"
        fun Intent.getHeader(): ResourceHeader = getParcelableExtra(HEADER)
    }

    protected val header by lazy { intent.getHeader() }
    protected val contentRef by lazy { firestoreRoot.document(intent.getContentRefPath()) }
    protected val cardsRef by lazy { contentRef.collection(getString(R.string.cards)) }

    abstract val cardRecyclerAdapter: ResourceContentRecyclerAdapter

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

    protected fun updateActionBarHeader() {
        if (header.name.isNotEmpty()) {
            supportActionBar?.title = header.name
        } else {
            supportActionBar?.title = getString(R.string.no_name)
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

        if (header.status != ResourceStatus.PUBLISHED) {
            unpublishedTextView.setVisible()
        } else {
            unpublishedTextView.setVisibilityGone()
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
        progressBar.setVisible()
    }

    override fun onDataLoaded() {
        progressBar.setVisibilityGone()
    }

}

