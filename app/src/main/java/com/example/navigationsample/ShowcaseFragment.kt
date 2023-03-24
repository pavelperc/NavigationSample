package com.example.navigationsample

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import kotlinx.android.synthetic.main.fragment_showcase.*

class ShowcaseFragment: Fragment(R.layout.fragment_showcase) {
    companion object {
        const val TAG = "ShowcaseFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        buttonOpenBook.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.tabContainer, BookFragment.newInstance(1))
                addToBackStack(BookFragment.TAG)
            }
        }
    }
}