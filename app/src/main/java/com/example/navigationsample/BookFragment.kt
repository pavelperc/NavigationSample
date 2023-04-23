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
import com.example.navigationsample.databinding.FragmentBookBinding
import kotlin.random.Random

class BookFragment : BaseSwipeFragment() {
    companion object {
        const val TAG = "BookFragment"

        const val EXTRA_NUMBER = "number"

        fun newInstance(number: Int): BookFragment {
            return BookFragment().apply { arguments = bundleOf(EXTRA_NUMBER to number) }
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
        val colorLight = ColorUtils.blendARGB(color, Color.WHITE, 0.5f)
        binding.rootLayout.setBackgroundColor(colorLight)
        binding.toolbar.setBackgroundColor(color)
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

    private fun generateColor(number: Int): Int {
        val random = Random(number + 1)
        return Color.rgb(
            random.nextInt(180, 240),
            random.nextInt(180, 240),
            random.nextInt(180, 240)
        )
    }
}