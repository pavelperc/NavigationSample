package com.example.navigationsample

import com.example.navigationsample.base.BaseFragment
import com.example.navigationsample.databinding.FragmentProfileBinding

class ProfileFragment : BaseFragment() {

    private val binding by viewBinding(FragmentProfileBinding::inflate)

    companion object {
        const val TAG = "ProfileFragment"
    }
}