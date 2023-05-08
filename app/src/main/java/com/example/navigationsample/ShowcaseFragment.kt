package com.example.navigationsample

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.commit
import com.example.navigationsample.databinding.FragmentShowcaseBinding
import com.example.navigationsample.utils.generateDarkColor

class ShowcaseFragment: BaseFragment() {
    companion object {
        const val TAG = "ShowcaseFragment"
    }

    private val binding by viewBinding(FragmentShowcaseBinding::inflate)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.bookView1.bind("Книга 1", generateDarkColor(1))
        binding.bookView2.bind("Книга 2", generateDarkColor(2))
        binding.bookView3.bind("Книга 3", generateDarkColor(3))
        binding.bookView4.bind("Книга 4", generateDarkColor(4))

        binding.bookView1.setOnClickListener { openBook(1, binding.bookView1.imageView) }
        binding.bookView2.setOnClickListener { openBook(2, binding.bookView2.imageView) }
        binding.bookView3.setOnClickListener { openBook(3, binding.bookView3.imageView) }
        binding.bookView4.setOnClickListener { openBook(4, binding.bookView4.imageView) }
    }

    private fun openBook(number: Int, imageView: ImageView) {
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.slide_in, 0, 0, R.anim.slide_out)
            add(R.id.tabContainer, BookFragment.newInstance(number, imageView))
            addToBackStack(BookFragment.TAG)
        }
    }
}