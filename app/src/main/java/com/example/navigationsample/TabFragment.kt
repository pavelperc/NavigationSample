package com.example.navigationsample

import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace

class TabFragment: Fragment(R.layout.fragment_tab) {
    companion object {
        const val TAG = "TabFragment"
    }

    inline fun <reified T: Fragment> setStartNavigation(tag: String) {
        childFragmentManager.commit {
            replace<T>(R.id.container)
            addToBackStack(tag)
        }
    }
}