package org.jnanaprabodhini.happyteacher.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_recycler.*

import org.jnanaprabodhini.happyteacher.R

class RecyclerFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater?.inflate(R.layout.fragment_recycler, container, false)
    }

    override fun onStart() {
        super.onStart()
        statusTextView.text = "There's nothing here yet."
    }

}
