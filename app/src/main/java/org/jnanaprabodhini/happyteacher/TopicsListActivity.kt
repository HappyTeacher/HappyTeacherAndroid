package org.jnanaprabodhini.happyteacher

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

class TopicsListActivity : AppCompatActivity(), TopicsListView {

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

    // TODO: bring to parent activity class.
    private val databaseInstance = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics_list)

        presenter = TopicsListPresenter(this)

        topicsRecyclerView.layoutManager = LinearLayoutManager(this)
        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        this.setupSubjectSpinner()
    }

    private fun setupSubjectSpinner() {
        val subjectQuery = databaseInstance.getReference("subjects").orderByChild("is_active").equalTo(true)

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
                .orderByChild("is_active")
                .equalTo(true)

        val topicAdapter = object: FirebaseRecyclerAdapter<Topic, TopicViewHolder>(Topic::class.java, R.layout.list_item_topic, TopicViewHolder::class.java, topicQuery) {
            override fun populateViewHolder(viewHolder: TopicViewHolder?, model: Topic?, position: Int) {
                viewHolder?.topicTextView?.text = model?.names?.get("en")

                // Alternate between these four colors:
                when (position % 4) {
                    0 -> viewHolder?.itemView?.setBackgroundResource(R.color.seaTeal)
                    1 -> viewHolder?.itemView?.setBackgroundResource(R.color.grassGreen)
                    2 -> viewHolder?.itemView?.setBackgroundResource(R.color.bubbleGumPink)
                    3 -> viewHolder?.itemView?.setBackgroundResource(R.color.dreamsicleOrange)
                }

                val topicKey = this.getRef(position).key

                val lessonHeaderQuery = databaseInstance.getReference("lesson_headers")
                        .child("mathematics_addition") // hardcoded just for testing!
                        .orderByChild("name")

                val dateFormat = DateFormat.getDateFormat(this@TopicsListActivity)

                // TODO: remove crazy nesting! hehe
                val lessonHeaderAdapter = object: FirebaseRecyclerAdapter<LessonHeader, LessonHeaderViewHolder>(LessonHeader::class.java, R.layout.list_item_lesson, LessonHeaderViewHolder::class.java, lessonHeaderQuery) {
                    override fun populateViewHolder(lessonHeaderViewHolder: LessonHeaderViewHolder?, header: LessonHeader?, headerPosition: Int) {
                        lessonHeaderViewHolder?.lessonTitleTextView?.text = header?.name
                        lessonHeaderViewHolder?.authorNameTextView?.text = header?.author_name
                        lessonHeaderViewHolder?.institutionTextView?.text = header?.author_institution
                        lessonHeaderViewHolder?.locationTextView?.text = header?.author_location

                        if (header != null) {
                            lessonHeaderViewHolder?.dateEditedTextView?.text = dateFormat.format(Date(header.dateEdited))
                        }
                    }
                }

                val horizontalLayoutManager = LinearLayoutManager(this@TopicsListActivity, LinearLayoutManager.HORIZONTAL, false)
                viewHolder?.lessonsRecyclerView?.layoutManager = horizontalLayoutManager
                viewHolder?.lessonsRecyclerView?.adapter = lessonHeaderAdapter

            }
        }

        topicsRecyclerView.adapter = topicAdapter
    }

}
