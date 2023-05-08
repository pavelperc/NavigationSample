package com.example.navigationsample.swipe

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import com.example.navigationsample.utils.dp
import com.example.swipeanimationsample.NestedScrollInterceptor
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

    private val nestedScrollInterceptor by lazy { NestedScrollInterceptor(view) }

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

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return dragHelper.shouldInterceptTouchEvent(ev) &&
                nestedScrollInterceptor.shouldInterceptTouchEvent(ev)
    }

    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }
}