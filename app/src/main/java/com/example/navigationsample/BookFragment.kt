package com.example.navigationsample

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import kotlinx.android.synthetic.main.fragment_book.buttonOpenBook
import kotlinx.android.synthetic.main.fragment_book.rootLayout
import kotlinx.android.synthetic.main.fragment_book.textViewTitle
import kotlin.random.Random

class BookFragment : BaseSwipeFragment(R.layout.fragment_book) {
    companion object {
        const val TAG = "BookFragment"

        const val EXTRA_NUMBER = "number"

        fun newInstance(number: Int): BookFragment {
            return BookFragment().apply { arguments = bundleOf(EXTRA_NUMBER to number) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val number = arguments?.getInt(EXTRA_NUMBER) ?: error("no number")
        textViewTitle.text = "Книга $number"
        rootLayout.setBackgroundColor(generateColor(number))
        buttonOpenBook.setOnClickListener {
            parentFragmentManager.commit {
                add(R.id.tabContainer, BookFragment.newInstance(number + 1))
                addToBackStack(BookFragment.TAG)
            }
        }
    }

    private fun generateColor(number: Int): Int {
        val random = Random(number + 54)
        return Color.rgb(
            random.nextInt(200, 250),
            random.nextInt(200, 250),
            random.nextInt(200, 250)
        )
    }
}