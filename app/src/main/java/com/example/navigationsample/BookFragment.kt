package com.example.navigationsample

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import kotlinx.android.synthetic.main.fragment_book.*

class BookFragment : Fragment(R.layout.fragment_book) {
    companion object {
        const val TAG = "BookFragment"

        const val EXTRA_NUMBER = "number"

        fun newInstance(number: Int): BookFragment {
            return BookFragment().apply { arguments = bundleOf(EXTRA_NUMBER to number) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val number = arguments?.getInt(EXTRA_NUMBER) ?: error("no number")
        textViewTitle.text = "Книга $number"
        buttonOpenBook.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.tabContainer, BookFragment.newInstance(number + 1))
                addToBackStack(BookFragment.TAG)
            }
        }
    }
}