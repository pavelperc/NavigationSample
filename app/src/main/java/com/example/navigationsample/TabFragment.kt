package com.example.navigationsample

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit

class TabFragment: Fragment(R.layout.fragment_tab) {
    companion object {
        const val TAG = "TabFragment"

        const val EXTRA_START_NAVIGATION = "start_navigation"

        fun newInstance(startNavigation: String): TabFragment {
            return TabFragment().apply { arguments = bundleOf(EXTRA_START_NAVIGATION to startNavigation) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (childFragmentManager.backStackEntryCount == 0) {
            val startNavigation = arguments?.getString(EXTRA_START_NAVIGATION)

            val fragment = when (startNavigation) {
                MyBooksFragment.TAG -> MyBooksFragment()
                ShowcaseFragment.TAG -> ShowcaseFragment()
                ProfileFragment.TAG -> ProfileFragment()
                else -> error("unknown start navigation $startNavigation")
            }
            childFragmentManager.commit {
                replace(R.id.tabContainer, fragment, startNavigation)
                addToBackStack(startNavigation)
            }
        }
    }
}