package com.example.navigationsample

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.navigationsample.base.BaseFragment
import com.example.navigationsample.databinding.FragmentShowcaseBinding
import com.example.navigationsample.views.BookRowView

class ShowcaseFragment : BaseFragment() {
    companion object {
        const val TAG = "ShowcaseFragment"
    }

    private val binding by viewBinding(FragmentShowcaseBinding::inflate)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                object : ViewHolder(BookRowView(parent.context)) {}

            override fun getItemCount() = 5

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                (holder.itemView as BookRowView).apply {
                    setTitleText("Секция ${position + 1}")
                    initialPosition = position * 10 + 1
                    onBookClick = ::openBook
                }
            }
        }
    }

    private fun openBook(number: Int, sharedView: View) {
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.slide_in, 0, 0, R.anim.slide_out)
            add(R.id.tabContainer, BookFragment.newInstance(number, sharedView))
            addToBackStack(BookFragment.TAG)
        }
    }
}