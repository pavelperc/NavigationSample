package com.example.navigationsample.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.navigationsample.databinding.ViewBookRowBinding

class BookRowView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ViewBookRowBinding.inflate(LayoutInflater.from(context), this)

    var rowPosition = 0
        set(value) {
            field = value
            binding.recyclerView.adapter?.notifyDataSetChanged()
        }

    fun setTitleText(text: String) {
        binding.textViewTitle.text = text
        binding.textViewTitle.isVisible = true
    }

    var onBookClick: ((number: Int, itemView: View) -> Unit)? = null

    init {
        orientation = VERTICAL
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                    object : RecyclerView.ViewHolder(BookCardView(parent.context)) {}

                override fun getItemCount() = 10

                override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                    val number = rowPosition * itemCount + position + 1
                    (holder.itemView as BookCardView).bind(number)
                    holder.itemView.setOnClickListener {
                        onBookClick?.invoke(number, holder.itemView)
                    }
                }
            }
        }
    }


}