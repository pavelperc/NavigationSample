package com.example.navigationsample

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.navigationsample.base.BaseFragment
import com.example.navigationsample.databinding.FragmentTabBinding

class TabFragment : BaseFragment() {
    companion object {
        const val EXTRA_TAG = "start_navigation_tag"
        const val EXTRA_CLASS = "start_navigation_class"

        fun newInstance(clazz: Class<*>, tag: String): TabFragment {
            return TabFragment().apply {
                arguments = bundleOf(
                    EXTRA_CLASS to clazz,
                    EXTRA_TAG to tag,
                )
            }
        }
    }

    private val binding by viewBinding(FragmentTabBinding::inflate)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (childFragmentManager.backStackEntryCount == 0) {
            val clazz = arguments?.getSerializable(EXTRA_CLASS) as Class<out Fragment>
            val tag = arguments?.getString(EXTRA_TAG)!!

            childFragmentManager.commit {
                replace(R.id.tabContainer, clazz, null, tag)
                addToBackStack(tag)
            }
        }
    }
}