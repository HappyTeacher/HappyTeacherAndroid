package org.jnanaprabodhini.happyteacher

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_topics_list.*
import org.jnanaprabodhini.happyteacher.models.Subject

class TopicsListActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                message.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                message.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                message.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics_list)

        val navigation = findViewById(R.id.navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        // Read from the database
        val subjectsRef = FirebaseDatabase.getInstance().reference.child("subjects")

        subjectsRef.child("environmental_science").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                val subject = dataSnapshot?.getValue(Subject::class.java)
                message.text = subject?.names?.get("en")
            }

            override fun onCancelled(p0: DatabaseError?) {
                message.text = "Meh it didn't work"
            }
        })

        subjectsRef.orderByChild("is_active").equalTo(false).addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                message.text = "CANCELLED I GUESS>>"
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot?, p1: String?) {
                val subject = dataSnapshot?.getValue(Subject::class.java)
                message.text = "${subject?.names?.get("en")} onChildMoved"
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot?, p1: String?) {
                val subject = dataSnapshot?.getValue(Subject::class.java)
                message.text = "${subject?.names?.get("en")} onChildChanged"
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot?, p1: String?) {
                val subject = dataSnapshot?.getValue(Subject::class.java)
                message.text = "${subject?.names?.get("en")} onChildAdded"
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot?) {
                val subject = dataSnapshot?.getValue(Subject::class.java)
                message.text = "${subject?.names?.get("en")} onChildRemoved"
            }

        })

    }

}
