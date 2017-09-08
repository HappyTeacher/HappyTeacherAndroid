package org.jnanaprabodhini.happyteacher

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.os.ConfigurationCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_topics_list.*
import org.jnanaprabodhini.happyteacher.models.Subject
import org.jnanaprabodhini.happyteacher.models.Topic

class TopicsListActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_board -> {
                message.setText(R.string.title_board)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_topics -> {
                message.setText(R.string.title_topics)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_contribute -> {
                message.setText(R.string.title_contribute)
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

        // TODO: Do this on application level.
        databaseInstance.setPersistenceEnabled(true)

        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        // TODO: Separate
        val subjectQuery = databaseInstance.getReference("subjects").orderByChild("is_active").equalTo(true)
        val subjectAdapter = object : FirebaseListAdapter<Subject>(this, Subject::class.java, R.layout.spinner_item, subjectQuery) {
            override fun populateView(view: View, subject: Subject, position: Int) {
                val languageCode = ConfigurationCompat.getLocales(resources.configuration)[0].language
                (view as TextView).text = "${subject.names[languageCode]} from language $languageCode"
            }
        }
        subject_spinner.adapter = subjectAdapter

        subject_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
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

        val topicAdapter = object : FirebaseListAdapter<Topic>(this, Topic::class.java, R.layout.spinner_item, topicQuery) {
            override fun populateView(view: View, topic: Topic, position: Int) {
                val languageCode = ConfigurationCompat.getLocales(resources.configuration)[0].language
                (view as TextView).text = "${topic.names[languageCode]}"
            }
        }
        subject_spinner2.adapter = topicAdapter
    }

}
