package org.jnanaprabodhini.happyteacher.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_card_list_content_viewer.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.parent.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.adapter.contentlist.CardListContentRecyclerAdapter
import org.jnanaprabodhini.happyteacher.extension.onSingleValueEvent
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.CardListContent
import org.jnanaprabodhini.happyteacher.model.ContentCard
import java.io.File

abstract class CardListContentViewerActivity : HappyTeacherActivity() {

    companion object Constants {
        val WRITE_STORAGE_PERMISSION_CODE = 1

        fun launchLessonViewerActivity(from: Activity, contentId: String, subtopicId: String, subjectName: String, topicName: String, topicId: String, subtopicName: String, submissionCount: Int) {
            val lessonViewerIntent = Intent(from, LessonViewerActivity::class.java)
            launchIntentWithExtras(lessonViewerIntent, from, contentId, subtopicId, subjectName, topicName, topicId, subtopicName, submissionCount)
        }

        fun launchClassroomResourcesActivity(from: Activity, contentId: String, subtopicId: String, subjectName: String, topicName: String, topicId: String, subtopicName: String, submissionCount: Int) {
            val classroomResourcesViewerIntent = Intent(from, ClassroomResourceViewerActivity::class.java)
            launchIntentWithExtras(classroomResourcesViewerIntent, from, contentId, subtopicId, subjectName, topicName, topicId, subtopicName, submissionCount)
        }

        private fun launchIntentWithExtras(intent: Intent, activity: Activity, contentId: String, subtopicId: String, subjectName: String, topicName: String, topicId: String, subtopicName: String, submissionCount: Int) {
            intent.apply {
                putExtra(CardListContentViewerActivity.CONTENT_ID, contentId)
                putExtra(CardListContentViewerActivity.SUBTOPIC_ID, subtopicId)
                putExtra(CardListContentViewerActivity.SUBJECT, subjectName)
                putExtra(CardListContentViewerActivity.TOPIC_NAME, topicName)
                putExtra(CardListContentViewerActivity.TOPIC_ID, topicId)
                putExtra(CardListContentViewerActivity.SUBTOPIC_NAME, subtopicName)
                putExtra(CardListContentViewerActivity.SUBMISSION_COUNT, submissionCount)
            }
            activity.startActivity(intent)
        }

        val CONTENT_ID: String = "CONTENT_ID"
        fun Intent.hasContentId(): Boolean = hasExtra(CONTENT_ID)
        fun Intent.getContentId(): String = getStringExtra(CONTENT_ID)

        val SUBTOPIC_ID: String = "SUBTOPIC_ID"
        fun Intent.hasSubtopicId(): Boolean = hasExtra(SUBTOPIC_ID)
        fun Intent.getSubtopicId(): String = getStringExtra(SUBTOPIC_ID)

        val SUBJECT: String = "SUBJECT"
        fun Intent.hasSubject(): Boolean = hasExtra(SUBJECT)
        fun Intent.getSubject(): String = getStringExtra(SUBJECT)

        val TOPIC_NAME: String = "TOPIC_NAME"
        fun Intent.hasTopicName(): Boolean = hasExtra(TOPIC_NAME)
        fun Intent.getTopicName(): String = getStringExtra(TOPIC_NAME)

        val TOPIC_ID: String = "TOPIC_ID"
        fun Intent.hasTopicId(): Boolean = hasExtra(TOPIC_ID)
        fun Intent.getTopicId(): String = getStringExtra(TOPIC_ID)

        val SUBTOPIC_NAME: String = "SUBTOPIC_NAME"
        fun Intent.hasSubtopicName(): Boolean = hasExtra(SUBTOPIC_NAME)
        fun Intent.getSubtopicName(): String = getStringExtra(SUBTOPIC_NAME)

        val SUBMISSION_COUNT: String = "SUBMISSION_COUNT"
        fun Intent.hasSubmissionCount(): Boolean = hasExtra(SUBMISSION_COUNT)
        fun Intent.getSubmissionCount(): Int = getIntExtra(SUBMISSION_COUNT, 0)

        fun Intent.hasAllExtras(): Boolean = hasContentId() && hasSubtopicId() && hasSubject() && hasTopicName() && hasSubtopicName() && hasTopicId() && hasSubmissionCount()
    }

    val contentId by lazy { intent.getContentId() }
    val subtopicId by lazy { intent.getSubtopicId() }
    val subject by lazy { intent.getSubject() }
    val topicName by lazy { intent.getTopicName() }
    val topicId by lazy { intent.getTopicId() }
    val subtopicName by lazy { intent.getSubtopicName() }
    val submissionCount by lazy { intent.getSubmissionCount() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_list_content_viewer)

        if (!intent.hasAllExtras()) {
            showErrorToastAndFinish()
        }

        initializeUiForContentFromDatabase()
    }

    abstract fun getContentRef(): DatabaseReference

    private fun initializeUiForContentFromDatabase() {
        progressBar.setVisible()

        // This directory will be used to store any attachments downloaded from this contentKey.
        val attachmentDestinationDirectory = File(Environment.getExternalStorageDirectory().path
                                                        + File.separator
                                                        + getString(R.string.app_name)
                                                        + File.separator
                                                        + subject + File.separator + topicName + File.separator + subtopicName)


        getContentRef().onSingleValueEvent { dataSnapshot ->
            val content = dataSnapshot?.getValue(CardListContent::class.java)
            initializeUiForContent(content, attachmentDestinationDirectory)
        }
    }

    private fun initializeUiForContent(content: CardListContent?, attachmentDestinationDirectory: File) {
        progressBar.setVisibilityGone()
        setHeaderViewForContent(content)
        initializeRecyclerView(content, attachmentDestinationDirectory)
    }

    open fun setHeaderViewForContent(content: CardListContent?) {
        headerView.setVisible()
        supportActionBar?.title = content?.name

        val authorName = content?.authorName
        val institutionName = content?.authorInstitution
        val location = content?.authorLocation

        subjectTextView.text = subject
        authorNameTextView.text = authorName
        institutionTextView.text = institutionName
        locationTextView.text = location
    }

    private fun initializeRecyclerView(content: CardListContent?, attachmentDestinationDirectory: File) {
        cardRecyclerView.layoutManager = LinearLayoutManager(this)

        if (content == null) {
            showErrorToastAndFinish()
        } else {
            cardRecyclerView?.adapter = getCardRecyclerAdapter(content.cards, attachmentDestinationDirectory)
            cardRecyclerView?.setHasFixedSize(true)
        }
    }

    abstract fun getCardRecyclerAdapter(cards: Map<String, ContentCard>, attachmentDestinationDirectory: File): CardListContentRecyclerAdapter

    private fun showErrorToastAndFinish() {
        // TODO: Log error to analytics.
        Toast.makeText(this, R.string.there_was_an_error_loading_the_lesson, Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == WRITE_STORAGE_PERMISSION_CODE && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            // Re-draw items to reflect new permissions
            cardRecyclerView.adapter.notifyDataSetChanged()
        }
    }
}

