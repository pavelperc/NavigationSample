package com.example.navigationsample.swipe

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import com.example.navigationsample.utils.dp
import kotlin.math.abs
import kotlin.math.max

class SwipeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private val VELOCITY_THRESHOLD = 400.dp.toFloat()
        private val MIN_VELOCITY = 400.dp.toFloat()
        private const val DISTANCE_THRESHOLD = 0.3
        private const val START_SCRIM_ALPHA = 0.8

        private const val TAG = "SwipeView"
    }

    var onSwipeComplete: (() -> Unit)? = null

    private val dragHelperCallback = object : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int) = child === view
        override fun getViewHorizontalDragRange(child: View) = width
        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int) = max(left, 0)

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            if (xvel > VELOCITY_THRESHOLD || releasedChild.x > width * DISTANCE_THRESHOLD) {
                dragHelper.settleCapturedViewAt(width, 0)
            } else {
                dragHelper.settleCapturedViewAt(0, 0)
            }
            ViewCompat.postInvalidateOnAnimation(this@SwipeView)
        }

        override fun onViewPositionChanged(
            changedView: View, left: Int, top: Int, dx: Int, dy: Int
        ) {
            if (width <= 0) return
            val scrimAlpha = ((width - left) / width.toFloat()) * 255 * START_SCRIM_ALPHA
            val scrimColor = ColorUtils.setAlphaComponent(Color.BLACK, scrimAlpha.toInt())
            setBackgroundColor(scrimColor)
        }

        override fun onViewDragStateChanged(state: Int) {
            if (state == ViewDragHelper.STATE_IDLE && view.x != 0f) {
                onSwipeComplete?.invoke()
            }
        }
    }

    private val dragHelper: ViewDragHelper = ViewDragHelper.create(this, dragHelperCallback).apply {
        minVelocity = MIN_VELOCITY
    }

    private val view: View get() = getChildAt(0)

    private var lastX = 0f
    private var lastY = 0f
    private var dx = 0f
    private var dy = 0f

    fun reset() {
        view.offsetLeftAndRight(-view.left)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (dragHelper.viewDragState == ViewDragHelper.STATE_IDLE && view.x != 0f) {
            return false
        }
        dragHelper.processTouchEvent(event)
        return true
    }

    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        dx = ev.x - lastX
        dy = ev.y - lastY
        val result = when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                dragHelper.shouldInterceptTouchEvent(ev)
            }

            MotionEvent.ACTION_MOVE -> {
                val canScroll = canScroll(view, false, dx.toInt(), ev.x.toInt(), ev.y.toInt())
                val isRightDirection = isRightDirection()
                Log.d(
                    TAG, "canScroll $canScroll, isRightDirection $isRightDirection, " +
                            "dx $dx, dy $dy, x ${ev.x.toInt()}, y ${ev.y.toInt()}"
                )
                !canScroll && isRightDirection && dragHelper.shouldInterceptTouchEvent(ev)
            }

            else -> dragHelper.shouldInterceptTouchEvent(ev)
        }

        lastX = ev.x
        lastY = ev.y
        return result
    }

    private fun isRightDirection(): Boolean {
        return dx > 0 && abs(dx) > abs(dy) * 3
    }


    // inspired by https://android.googlesource.com/platform/frameworks/support/+/5b614a46f6ffb3e9ca5ab6321c12412550a4e13a/viewpager/src/main/java/androidx/viewpager/widget/ViewPager.java#2711
    /**
     * Tests scrollability within child views of v given a delta of dx.
     *
     * @param v View to test for horizontal scrollability
     * @param checkV Whether the view v passed should itself be checked for scrollability (true),
     *               or just its children (false).
     * @param dx Delta scrolled in pixels
     * @param x X coordinate of the active touch point
     * @param y Y coordinate of the active touch point
     * @return true if child views of v can be scrolled by delta of dx.
     */
    private fun canScroll(v: View, checkV: Boolean, dx: Int, x: Int, y: Int): Boolean {
        if (v is ViewGroup) {
            val group = v
            val scrollX = v.getScrollX() // maybe problems here
            val scrollY = v.getScrollY()
            val count = group.childCount
            // Count backwards - let topmost views consume scroll distance first.
            for (i in count - 1 downTo 0) {
                // This will not work for transformed views in Honeycomb+
                val child = group.getChildAt(i)
                if (x + scrollX >= child.left &&
                    x + scrollX < child.right &&
                    y + scrollY >= child.top &&
                    y + scrollY < child.bottom &&
                    canScroll(child, true, dx, x + scrollX - child.left, y + scrollY - child.top)
                ) {
                    return true
                }
            }
        }
        return checkV && v.canScrollHorizontally(-dx)
    }
}
