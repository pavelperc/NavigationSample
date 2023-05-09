package com.example.navigationsample.base

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import java.io.Serializable
import kotlin.properties.ReadOnlyProperty

open class BaseFragment : Fragment() {

    data class SharedViewParams(
        val x: Int,
        val y: Int,
        val width: Int,
        val height: Int
    ) : Serializable {
        companion object {
            fun fromView(view: View): SharedViewParams {
                val loc = intArrayOf(0, 0)
                view.getLocationOnScreen(loc)
                return SharedViewParams(
                    x = loc[0],
                    y = loc[1],
                    width = view.width,
                    height = view.height
                )
            }
        }
    }

    protected var _binding: ViewBinding? = null
    private var bindingInflate: ((inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean) -> ViewBinding)? =
        null

    var sharedViewParams: SharedViewParams? = null

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

    protected fun startSharedViewAnimation(view: View) {
        val params = sharedViewParams ?: return
        sharedViewParams = null

        view.visibility = View.INVISIBLE
        view.post {
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            val tempView = ImageView(view.context)
            tempView.setImageBitmap(bitmap)
            requireActivity().addContentView(tempView, ViewGroup.LayoutParams(view.width, view.height))

            val loc = intArrayOf(0, 0)
            view.getLocationOnScreen(loc)
            val scaleX = view.width / params.width.toFloat()
            val scaleY = view.height / params.height.toFloat()

            tempView.translationX = params.x.toFloat()
            tempView.translationY = params.y.toFloat()
            tempView.scaleX = 1 / scaleX
            tempView.scaleY = 1 / scaleY


            tempView.pivotX = 0f
            tempView.pivotY = 0f

            tempView.animate()
                .translationX(loc[0].toFloat())
                .translationY(loc[1].toFloat())
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(200)
                .withEndAction {
                    (tempView.parent as ViewGroup).removeView(tempView)
                    view.visibility = View.VISIBLE
                }
        }
    }
}