package com.example.navigationsample

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import com.example.navigationsample.databinding.FragmentShowcaseBinding

class ShowcaseFragment: BaseFragment() {
    companion object {
        const val TAG = "ShowcaseFragment"
    }

    private val binding by viewBinding(FragmentShowcaseBinding::inflate)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonOpenBook.setOnClickListener {
            parentFragmentManager.commit {
                add(R.id.tabContainer, BookFragment.newInstance(1))
                addToBackStack(BookFragment.TAG)
            }
        }
    }
}