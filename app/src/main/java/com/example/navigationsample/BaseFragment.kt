package com.example.navigationsample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty

open class BaseFragment : Fragment() {
    protected var _binding: ViewBinding? = null
    private var bindingInflate: ((inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean) -> ViewBinding)? =
        null

    @Suppress("UNCHECKED_CAST")
    protected fun <VB : ViewBinding> viewBinding(
        inflate: (inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean) -> VB
    ): ReadOnlyProperty<Fragment, VB> {
        bindingInflate = inflate
        return ReadOnlyProperty { _, _ ->
            _binding!! as VB
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = bindingInflate?.invoke(inflater, container, false)
        return _binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}