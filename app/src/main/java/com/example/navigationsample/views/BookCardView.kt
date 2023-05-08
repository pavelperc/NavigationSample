package com.example.navigationsample.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.setPadding
import com.example.navigationsample.databinding.ViewBookCardBinding
import com.example.navigationsample.utils.dp
import com.example.navigationsample.utils.getThemeAttr

class BookCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ViewBookCardBinding.inflate(LayoutInflater.from(context), this)

    val imageView get() = binding.imageView

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        setPadding(16.dp)
    }

    fun bind(text: String, color: Int = Color.BLACK) {
        binding.apply {
            textView.text = text
            textView.setTextColor(color)
            imageView.imageTintList = ColorStateList.valueOf(color)
        }
    }
}