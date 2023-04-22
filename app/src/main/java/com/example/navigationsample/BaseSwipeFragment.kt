package com.example.navigationsample

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.example.navigationsample.swipe.model.SliderConfig
import com.example.navigationsample.swipe.model.SliderInterface
import com.example.navigationsample.swipe.model.SliderListener
import com.example.navigationsample.swipe.slider.SliderPanel

open class BaseSwipeFragment(@LayoutRes layout: Int) : Fragment(layout), SliderListener {

    private var rootFrameLayout: FrameLayout? = null
    private var contentView: View? = null

    protected var sliderInterface: SliderInterface? = null
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contentView = super.onCreateView(inflater, container, savedInstanceState)
        rootFrameLayout = FrameLayout(requireContext()).also {
            it.setBackgroundColor(Color.TRANSPARENT)
            it.removeAllViews()
            it.addView(contentView)
        }
        return rootFrameLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSliderPanel()
    }

    private fun setUpSliderPanel() {
        contentView?.let { content ->
            if (sliderInterface == null) {
                val parent = content.parent as? ViewGroup ?: return
                val params = content.layoutParams
                parent.removeView(content)

                // Setup the slider panel and attach it
                val panel = SliderPanel(requireContext(), content, getSliderConfig())

                panel.addView(content)
                parent.addView(panel, 0, params)

                panel.setOnPanelSlideListener(
                    FragmentPanelSlideListener(
                        { requireActivity().onBackPressed() },
                        getSliderConfig()
                    )
                )

                sliderInterface = panel.defaultInterface
            }
        }
    }

    override fun onDestroyView() {
        rootFrameLayout?.removeAllViews()
        rootFrameLayout = null
        sliderInterface = null
        super.onDestroyView()
    }

    protected open fun getSliderConfig(): SliderConfig {
        return SliderConfig.Builder()
            .listener(this)
            .edgeSize(0.5f)
            .edge(false)
            .touchDisabledViews(getTouchDisabledViews())
            .build()
    }

    protected open fun getTouchDisabledViews(): List<View> {
        return emptyList()
    }

    override fun onSlideStateChanged(state: Int) {}

    override fun onSlideChange(percent: Float) {}

    override fun onSlideOpened() {}

    override fun onSlideClosed(): Boolean {
        return false
    }

}

internal class FragmentPanelSlideListener(
    private val onBackPressed: () -> Unit,
    private val config: SliderConfig
) : SliderPanel.OnPanelSlideListener {
    override fun onStateChanged(state: Int) {
        config.listener?.onSlideStateChanged(state)
    }

    override fun onClosed() {
        if (config.listener != null) {
            if (config.listener.onSlideClosed()) {
                return
            }
        }
        onBackPressed()
    }

    override fun onOpened() {
        config.listener?.onSlideOpened()
    }

    override fun onSlideChange(percent: Float) {
        config.listener?.onSlideChange(percent)
    }
}