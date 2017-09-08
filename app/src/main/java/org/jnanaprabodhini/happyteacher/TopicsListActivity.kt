package org.jnanaprabodhini.happyteacher

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.os.ConfigurationCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_topics_list.*
import org.jnanaprabodhini.happyteacher.models.Subject

class TopicsListActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics_list)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val databaseInstance = FirebaseDatabase.getInstance()

        val subjectQuery = databaseInstance.getReference("subjects").orderByChild("is_active").equalTo(true)


        val subjectAdapter = object : FirebaseListAdapter<Subject>(this, Subject::class.java, R.layout.support_simple_spinner_dropdown_item, subjectQuery) {
            override fun populateView(view: View, subject: Subject, position: Int) {
                val languageCode = ConfigurationCompat.getLocales(resources.configuration)[0].language
                (view as TextView).text = "${subject.names[languageCode]} from language $languageCode"
            }
        }

        subject_spinner.adapter = subjectAdapter

    }

}
