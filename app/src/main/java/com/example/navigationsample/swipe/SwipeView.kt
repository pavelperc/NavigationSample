package com.example.navigationsample.swipe

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
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
    private val isVertical: Boolean = true
) : FrameLayout(context) {

    companion object {
        private val VELOCITY_THRESHOLD = 400.dp.toFloat()
        private val MIN_VELOCITY = 400.dp.toFloat()
        private const val DISTANCE_THRESHOLD = 0.3
        private const val START_SCRIM_ALPHA = 0.8

        private const val TAG = "SwipeView"
    }

    var onSwipeComplete: (() -> Unit)? = null

    private val maxOffset get() = if (isVertical) height else width
    private val maxHeight get() = if (isVertical) height else 0
    private val maxWidth get() = if (isVertical) 0 else width
    private val View.offset get() = if (isVertical) y else x

    private val dragHelperCallback = object : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int) = child === view
        override fun getViewHorizontalDragRange(child: View) = maxWidth
        override fun getViewVerticalDragRange(child: View) = maxHeight
        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return if (isVertical) 0 else max(left, 0)
        }
        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return if (isVertical) max(top, 0) else 0
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val vel = if (isVertical) yvel else xvel

            if (vel > VELOCITY_THRESHOLD || releasedChild.offset > maxOffset * DISTANCE_THRESHOLD) {
                dragHelper.settleCapturedViewAt(maxWidth, maxHeight)
            } else {
                dragHelper.settleCapturedViewAt(0, 0)
            }
            ViewCompat.postInvalidateOnAnimation(this@SwipeView)
        }

        override fun onViewPositionChanged(
            changedView: View, left: Int, top: Int, dx: Int, dy: Int
        ) {
            if (maxOffset <= 0) return
            val offset = if (isVertical) top else left
            val scrimAlpha = ((maxOffset - offset) / maxOffset.toFloat()) * 255 * START_SCRIM_ALPHA
            val scrimColor = ColorUtils.setAlphaComponent(Color.BLACK, scrimAlpha.toInt())
            setBackgroundColor(scrimColor)
        }

        override fun onViewDragStateChanged(state: Int) {
            if (state == ViewDragHelper.STATE_IDLE && view.offset != 0f) {
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
        if (isVertical) {
            view.offsetTopAndBottom(-view.top)
        } else {
            view.offsetLeftAndRight(-view.left)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (dragHelper.viewDragState == ViewDragHelper.STATE_IDLE && view.offset != 0f) {
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
                val canScroll = canScroll(view, false, dx.toInt(), dy.toInt(), ev.x.toInt(), ev.y.toInt())
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
        return if (isVertical) {
            dy > 0 && abs(dy) > abs(dx) * 3
        } else {
            dx > 0 && abs(dx) > abs(dy) * 3
        }
    }


    // inspired by https://android.googlesource.com/platform/frameworks/support/+/5b614a46f6ffb3e9ca5ab6321c12412550a4e13a/viewpager/src/main/java/androidx/viewpager/widget/ViewPager.java#2711
    /**
     * Tests scrollability within child views of v given a delta of dx.
     *
     * @param v View to test for horizontal scrollability
     * @param checkV Whether the view v passed should itself be checked for scrollability (true),
     *               or just its children (false).
     * @param dx Delta scrolled in pixels
     * @param dy Delta scrolled in pixels
     * @param x X coordinate of the active touch point
     * @param y Y coordinate of the active touch point
     * @return true if child views of v can be scrolled by delta of dx.
     */
    private fun canScroll(v: View, checkV: Boolean, dx: Int, dy: Int, x: Int, y: Int): Boolean {
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
                    canScroll(child, true, dx, dy, x + scrollX - child.left, y + scrollY - child.top)
                ) {
                    return true
                }
            }
        }
        return if (isVertical) {
            checkV && v.canScrollVertically(-dy)
        } else {
            checkV && v.canScrollHorizontally(-dx)
        }
    }
}
