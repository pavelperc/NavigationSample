package com.example.swipeanimationsample

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup


class NestedScrollInterceptor(private val view: View) {

    companion object {
        private const val TAG = "NestedScrollInterceptor"

    }

    private var lastX = 0f

    fun shouldInterceptTouchEvent(ev: MotionEvent): Boolean {
        return when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastX = ev.x
                false
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = (ev.x - lastX).toInt()
                if (dx < 0) return false // support only right swipe

                val canScroll = canScroll(view, false, dx, ev.x.toInt(), ev.y.toInt())
                lastX = ev.x
//                Log.d(TAG, "canScroll $canScroll, dx $dx, x ${ev.x.toInt()}, y ${ev.y.toInt()}")
                !canScroll
            }

            else -> false
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
     * @param x X coordinate of the active touch point
     * @param y Y coordinate of the active touch point
     * @return true if child views of v can be scrolled by delta of dx.
     */
    private fun canScroll(v: View, checkV: Boolean, dx: Int, x: Int, y: Int): Boolean {
        if (v is ViewGroup) {
            val group = v
            val scrollX = v.getScrollX()
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