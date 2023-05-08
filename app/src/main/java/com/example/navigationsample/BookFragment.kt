package com.example.navigationsample

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.commit
import androidx.transition.AutoTransition
import com.example.navigationsample.databinding.FragmentBookBinding
import com.example.navigationsample.utils.darkerColor
import com.example.navigationsample.utils.generateColor
import com.example.navigationsample.utils.lighterColor

class BookFragment : BaseSwipeFragment() {
    companion object {
        const val TAG = "BookFragment"
        const val EXTRA_NUMBER = "number"
        const val SHARED_VIEW_TRANSITION_NAME = "book"

        fun newInstance(number: Int): BookFragment {
            return BookFragment().apply { arguments = bundleOf(EXTRA_NUMBER to number) }
        }
    }

    private val binding by viewBinding(FragmentBookBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = AutoTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageViewBook.transitionName = SHARED_VIEW_TRANSITION_NAME
        binding.toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        fitToolbarInInsets()
        val number = arguments?.getInt(EXTRA_NUMBER) ?: error("no number")
        binding.textViewTitle.text = "Книга $number"
        val color = generateColor(number)
        val colorLight = lighterColor(color)
        val colorDark = darkerColor(color)
        binding.rootLayout.setBackgroundColor(colorLight)
        binding.toolbar.setBackgroundColor(color)
        binding.imageViewBook.imageTintList = ColorStateList.valueOf(colorDark)
        binding.buttonOpenBook.setOnClickListener {
            parentFragmentManager.commit {
                setCustomAnimations(R.anim.slide_in, 0, 0, R.anim.slide_out)
                add(R.id.tabContainer, BookFragment.newInstance(number + 1))
                addToBackStack(BookFragment.TAG)
            }
        }
    }

    private fun fitToolbarInInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbar.updatePadding(
                top = insets.top,
                left = insets.left,
                right = insets.right
            )
            WindowInsetsCompat.CONSUMED
        }
    }
}