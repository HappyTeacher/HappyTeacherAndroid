package org.jnanaprabodhini.happyteacher

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.os.ConfigurationCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.format.DateFormat
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_topics_list.*
import org.jnanaprabodhini.happyteacher.models.LessonHeader
import org.jnanaprabodhini.happyteacher.models.Subject
import org.jnanaprabodhini.happyteacher.models.Topic
import java.util.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper



class TopicsListActivity : HappyTeacherActivity(), TopicsListView {

    private lateinit var presenter: TopicsListPresenter
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_board -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_topics -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_contribute -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics_list)

        presenter = TopicsListPresenter(this)

        topicsRecyclerView.layoutManager = LinearLayoutManager(this)
        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        bottomNavigation.selectedItemId = R.id.navigation_topics

        this.setupSubjectSpinner()
    }

    private fun setupSubjectSpinner() {
        val subjectQuery = databaseInstance.getReference("subjects").orderByChild("isActive").equalTo(true)

        val subjectAdapter = object : FirebaseListAdapter<Subject>(this, Subject::class.java, R.layout.spinner_item, subjectQuery) {
            override fun populateView(view: View, subject: Subject, position: Int) {
                val languageCode = ConfigurationCompat.getLocales(resources.configuration)[0].language
                (view as TextView).text = subject.names[languageCode]
            }
        }

        subjectSpinner.adapter = subjectAdapter

        subjectSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedSubjectKey = subjectAdapter.getRef(position).key
                updateListOfTopics(selectedSubjectKey)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    fun updateListOfTopics(subjectKey: String) {
        val topicQuery = databaseInstance.getReference("topics")
                .child(subjectKey)
                .orderByChild("isActive")
                .equalTo(true)

        val topicAdapter = object: FirebaseRecyclerAdapter<Topic, TopicViewHolder>(Topic::class.java, R.layout.list_item_topic, TopicViewHolder::class.java, topicQuery) {
            override fun populateViewHolder(topicViewHolder: TopicViewHolder?, topicModel: Topic?, topicPosition: Int) {
                topicViewHolder?.topicTextView?.text = topicModel?.names?.get("en")

                // Alternate between these four colors:
                when (topicPosition % 4) {
                    0 -> topicViewHolder?.itemView?.setBackgroundResource(R.color.seaTeal)
                    1 -> topicViewHolder?.itemView?.setBackgroundResource(R.color.grassGreen)
                    2 -> topicViewHolder?.itemView?.setBackgroundResource(R.color.bubbleGumPink)
                    3 -> topicViewHolder?.itemView?.setBackgroundResource(R.color.dreamsicleOrange)
                }

                // TODO: use this (remove dummy query)
                val topicKey = this.getRef(topicPosition).key

                val lessonHeaderQuery = databaseInstance.getReference("lesson_headers") // todo: camelCase lessonHeaders
                        .child("mathematics_addition") // hardcoded just for testing!
                        .orderByChild("name")

                val dateFormat = DateFormat.getDateFormat(this@TopicsListActivity)

                // TODO: remove crazy nesting! hehe
                val lessonHeaderAdapter = object: FirebaseEmptyRecyclerAdapter<LessonHeader, LessonHeaderViewHolder>(LessonHeader::class.java, R.layout.list_item_lesson, LessonHeaderViewHolder::class.java, lessonHeaderQuery) {

                    override fun populateViewHolder(lessonHeaderViewHolder: LessonHeaderViewHolder?, lessonHeaderModel: LessonHeader?, lessonHeaderPosition: Int) {
                        lessonHeaderViewHolder?.lessonTitleTextView?.text = lessonHeaderModel?.name
                        lessonHeaderViewHolder?.authorNameTextView?.text = lessonHeaderModel?.authorName
                        lessonHeaderViewHolder?.institutionTextView?.text = lessonHeaderModel?.authorInstitution
                        lessonHeaderViewHolder?.locationTextView?.text = lessonHeaderModel?.authorLocation

                        if (lessonHeaderModel != null) {
                            lessonHeaderViewHolder?.dateEditedTextView?.text = dateFormat.format(Date(lessonHeaderModel.dateEdited))
                        }
                    }

                    override fun onEmpty() {
                        topicViewHolder?.lessonsRecyclerView?.setVisibilityGone()
                        topicViewHolder?.emptyView?.setVisible()
                    }

                    override fun onNonEmpty() {
                        topicViewHolder?.lessonsRecyclerView?.setVisible()
                        topicViewHolder?.emptyView?.setVisibilityGone()
                    }
                }

                val horizontalLayoutManager = LinearLayoutManager(this@TopicsListActivity, LinearLayoutManager.HORIZONTAL, false)
                topicViewHolder?.lessonsRecyclerView?.layoutManager = horizontalLayoutManager
                topicViewHolder?.lessonsRecyclerView?.adapter = lessonHeaderAdapter
            }
        }
        topicsRecyclerView.adapter = topicAdapter
    }
}
