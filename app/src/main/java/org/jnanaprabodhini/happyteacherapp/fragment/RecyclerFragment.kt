package org.jnanaprabodhini.happyteacherapp.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.android.synthetic.main.fragment_recycler.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.util.PreferencesManager

abstract class RecyclerFragment : Fragment(), FirebaseDataObserver {

    abstract val emptyRecyclerText: String
    abstract val errorText: String

    private val firestoreRoot: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val prefs: PreferencesManager by lazy {
        PreferencesManager.getInstance(context)
    }

    val firestoreLocalized: DocumentReference by lazy {
        firestoreRoot.collection(getString(R.string.localized)).document(prefs.getCurrentLanguageCode())
    }

    protected val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater?.inflate(R.layout.fragment_recycler, container, false)
    }

    override fun onStart() {
        super.onStart()
        recyclerView.layoutManager = LinearLayoutManager(context)

        setupAdapter()
    }

    abstract fun setupAdapter()

    override fun onRequestNewData() {
        recyclerView?.setVisibilityGone()
        statusTextView?.setVisibilityGone()
        subtopicChoiceProgressBar?.setVisible()
    }

    override fun onDataLoaded() {
        subtopicChoiceProgressBar?.setVisibilityGone()
    }

    override fun onDataEmpty() {
        subtopicChoiceProgressBar?.setVisibilityGone()
        statusTextView?.setVisible()
        statusTextView?.text = emptyRecyclerText
    }

    override fun onDataNonEmpty() {
        subtopicChoiceProgressBar?.setVisibilityGone()
        statusTextView?.setVisibilityGone()
        recyclerView?.setVisible()
    }

    override fun onError(e: FirebaseFirestoreException?) {
        subtopicChoiceProgressBar?.setVisibilityGone()
        recyclerView?.setVisibilityGone()
        statusTextView?.setVisible()
        statusTextView?.text = errorText
    }

}

