package com.example.navigationsample

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.commit
import com.example.navigationsample.databinding.FragmentBookBinding
import com.example.navigationsample.utils.darkerColor
import com.example.navigationsample.utils.generateColor
import com.example.navigationsample.utils.lighterColor
import java.io.Serializable

class BookFragment : BaseSwipeFragment() {
    companion object {
        const val TAG = "BookFragment"
        const val EXTRA_NUMBER = "number"

        data class SharedViewParams(
            val x: Int,
            val y: Int,
            val width: Int,
            val height: Int
        ) : Serializable

        fun newInstance(number: Int, imageView: ImageView? = null): BookFragment {
            return BookFragment().apply {
                arguments = bundleOf(EXTRA_NUMBER to number)
                sharedViewParams = imageView?.let { getSharedViewParams(it) }
            }
        }

        private fun getSharedViewParams(view: View): SharedViewParams {
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

    var sharedViewParams: SharedViewParams? = null

    private val binding by viewBinding(FragmentBookBinding::inflate)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        fitToolbarInInsets()
        val number = arguments?.getInt(EXTRA_NUMBER) ?: error("no number")
        binding.textViewTitle.text = "Книга $number"
        val color = generateColor(number)
        val colorLight = lighterColor(color)
        val colorDark = darkerColor(color)
        binding.rootLayout.setBackgroundColor(colorLight)
        binding.toolbar.setBackgroundColor(color)
        binding.imageViewBook.imageTintList = ColorStateList.valueOf(colorDark)
        binding.buttonOpenBook.setOnClickListener {
            parentFragmentManager.commit {
                setCustomAnimations(R.anim.slide_in, 0, 0, R.anim.slide_out)
                add(R.id.tabContainer, BookFragment.newInstance(number + 1))
                addToBackStack(BookFragment.TAG)
            }
        }
        startSharedViewAnimation(binding.imageViewBook)
    }

    private fun startSharedViewAnimation(view: View) {
        val params = sharedViewParams ?: return
        sharedViewParams = null

        view.visibility = View.INVISIBLE
        view.post {
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            val tempView = ImageView(view.context)
            tempView.setImageBitmap(bitmap)
            requireActivity().addContentView(tempView, ViewGroup.LayoutParams(params.width, params.height))

            tempView.translationX = params.x.toFloat()
            tempView.translationY = params.y.toFloat()

            val loc = intArrayOf(0, 0)
            view.getLocationOnScreen(loc)

            val scaleX = view.width / params.width.toFloat()
            val scaleY = view.height / params.height.toFloat()
            tempView.pivotX = 0f
            tempView.pivotY = 0f

            tempView.animate()
                .translationX(loc[0].toFloat())
                .translationY(loc[1].toFloat())
                .scaleX(scaleX)
                .scaleY(scaleY)
                .setDuration(200)
                .withEndAction {
                    (tempView.parent as ViewGroup).removeView(tempView)
                    view.visibility = View.VISIBLE
                }
        }
    }

    private fun fitToolbarInInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbar.updatePadding(
                top = insets.top,
                left = insets.left,
                right = insets.right
            )
            WindowInsetsCompat.CONSUMED
        }
    }
}