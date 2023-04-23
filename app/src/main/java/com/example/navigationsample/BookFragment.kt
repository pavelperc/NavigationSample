package com.example.navigationsample

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.graphics.ColorUtils
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.commit
import kotlinx.android.synthetic.main.fragment_book.buttonOpenBook
import kotlinx.android.synthetic.main.fragment_book.rootLayout
import kotlinx.android.synthetic.main.fragment_book.textViewTitle
import kotlinx.android.synthetic.main.fragment_book.toolbar
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
        toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        fitToolbarInInsets()
        val number = arguments?.getInt(EXTRA_NUMBER) ?: error("no number")
        textViewTitle.text = "Книга $number"
        val color = generateColor(number)
        val colorLight = ColorUtils.blendARGB(color, Color.WHITE, 0.5f)
        rootLayout.setBackgroundColor(colorLight)
        toolbar.setBackgroundColor(color)
        buttonOpenBook.setOnClickListener {
            parentFragmentManager.commit {
                add(R.id.tabContainer, BookFragment.newInstance(number + 1))
                addToBackStack(BookFragment.TAG)
            }
        }
    }

    private fun fitToolbarInInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            toolbar.updatePadding(
                top = insets.top,
                left = insets.left,
                right = insets.right
            )
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun generateColor(number: Int): Int {
        val random = Random(number + 1)
        return Color.rgb(
            random.nextInt(180, 240),
            random.nextInt(180, 240),
            random.nextInt(180, 240)
        )
    }
}