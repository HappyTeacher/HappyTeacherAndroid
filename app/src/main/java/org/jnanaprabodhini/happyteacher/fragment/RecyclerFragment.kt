package org.jnanaprabodhini.happyteacher.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.android.synthetic.main.fragment_recycler.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible

abstract class RecyclerFragment : Fragment(), FirebaseDataObserver {

    abstract val emptyRecyclerText: String
    abstract val errorText: String

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater?.inflate(R.layout.fragment_recycler, container, false)
    }

    override fun onRequestNewData() {
        recyclerView?.setVisibilityGone()
        statusTextView?.setVisibilityGone()
        progressBar?.setVisible()
    }

    override fun onDataLoaded() {
        progressBar?.setVisibilityGone()
    }

    override fun onDataEmpty() {
        progressBar?.setVisibilityGone()
        statusTextView?.setVisible()
        statusTextView?.text = emptyRecyclerText
    }

    override fun onDataNonEmpty() {
        progressBar?.setVisibilityGone()
        statusTextView?.setVisibilityGone()
        recyclerView?.setVisible()
    }

    override fun onError(e: FirebaseFirestoreException?) {
        progressBar?.setVisibilityGone()
        recyclerView?.setVisibilityGone()
        statusTextView?.setVisible()
        statusTextView?.text = errorText
    }

}

