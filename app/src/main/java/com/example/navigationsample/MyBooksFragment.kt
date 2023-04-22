package com.example.navigationsample

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import kotlinx.android.synthetic.main.fragment_my_books.*

class MyBooksFragment: Fragment(R.layout.fragment_my_books) {
    companion object {
        const val TAG = "MyBooksFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        buttonOpenBook.setOnClickListener {
            parentFragmentManager.commit {
                add(R.id.tabContainer, BookFragment.newInstance(1))
                addToBackStack(BookFragment.TAG)
            }
        }
    }
}