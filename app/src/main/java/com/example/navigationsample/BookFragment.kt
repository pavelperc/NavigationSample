package com.example.navigationsample

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.example.navigationsample.base.BaseSwipeFragment
import com.example.navigationsample.databinding.FragmentBookBinding
import com.example.navigationsample.utils.darkerColor
import com.example.navigationsample.utils.generateColor
import com.example.navigationsample.utils.lighterColor
import kotlinx.coroutines.delay

class BookFragment : BaseSwipeFragment() {
    companion object {
        const val TAG = "BookFragment"
        const val EXTRA_NUMBER = "number"

        fun newInstance(number: Int, sharedView: View? = null): BookFragment {
            return BookFragment().apply {
                arguments = bundleOf(EXTRA_NUMBER to number)
                sharedViewParams = sharedView?.let { SharedViewParams.fromView(it) }
            }
        }
    }

    private val binding by viewBinding(FragmentBookBinding::inflate)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        binding.bookRowView.apply {
            initialPosition = number + 1
            setTitleText("Похожие книги")
            onBookClick = ::openBook
        }
        startSharedViewAnimation(binding.layoutBookWithText)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            var x = 0
            while (true) {
                binding.textViewDescription.text = "Описание ${x++}"
                delay(500)
            }
        }
    }

    private fun openBook(number: Int, sharedView: View?) {
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.slide_in, 0, 0, R.anim.slide_out)
            add(R.id.tabContainer, BookFragment.newInstance(number, sharedView))
            addToBackStack(BookFragment.TAG)
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