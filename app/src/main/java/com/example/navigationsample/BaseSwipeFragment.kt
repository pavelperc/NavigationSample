package com.example.navigationsample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.navigationsample.swipe.SwipeView

open class BaseSwipeFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val swipeView = SwipeView(inflater.context)
        val contentView = super.onCreateView(inflater, container, savedInstanceState)
        swipeView.addView(contentView)
        swipeView.onSwipeComplete = {
            requireActivity().onBackPressed()
        }
        return swipeView
    }
}