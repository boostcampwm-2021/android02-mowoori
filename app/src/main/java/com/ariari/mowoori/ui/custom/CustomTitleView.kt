package com.ariari.mowoori.ui.custom

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.CustomTitleViewBinding
import com.ariari.mowoori.ui.custom.TitleViewMode.Companion.TITLE_VIEW_BACK_BUTTON
import com.ariari.mowoori.ui.custom.TitleViewMode.Companion.TITLE_VIEW_CLOSE_BUTTON
import com.ariari.mowoori.ui.custom.TitleViewMode.Companion.TITLE_VIEW_INVITE_BUTTON
import com.ariari.mowoori.ui.custom.TitleViewMode.Companion.TITLE_VIEW_PLUS_BUTTON
import java.lang.IllegalStateException

class CustomTitleView : ConstraintLayout {
    private lateinit var binding: CustomTitleViewBinding
    private var titleViewMode: Int = -1

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
        getAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs) {
        initView()
        getAttrs(attrs, defStyle)
    }

    private fun initView() {
        val layoutInflater = LayoutInflater.from(context)
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.custom_title_view,
            this@CustomTitleView,
            true
        )
        binding.view = this
    }


    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTitleView)
        setTypedArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet, defStyle: Int) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CustomTitleView, defStyle, 0)
        setTypedArray(typedArray)
    }

    private fun setTypedArray(typedArray: TypedArray) {
        val titleText = typedArray.getString(R.styleable.CustomTitleView_text) ?: ""
        setTitleViewText(titleText)
    }


    fun setTitleViewText(title: String?) {
        title?.let {
            binding.tvCustomTitleViewTitle.text = title
        }
    }

    fun setTitleViewMode(mode: Int) {
        titleViewMode = mode
        when (titleViewMode) {
            TITLE_VIEW_BACK_BUTTON -> binding.btnCustomTitleViewBack.visibility = View.VISIBLE
            TITLE_VIEW_CLOSE_BUTTON -> binding.btnCustomTitleViewClose.visibility = View.VISIBLE
            TITLE_VIEW_PLUS_BUTTON -> binding.btnCustomTitleViewPlus.visibility = View.VISIBLE
            TITLE_VIEW_INVITE_BUTTON -> binding.btnCustomTitleViewInvite.visibility = View.VISIBLE
            else -> throw IllegalStateException()
        }
    }

    fun setOnBackClick(clickEvent: () -> Unit) {
        binding.btnCustomTitleViewBack.setOnClickListener { clickEvent() }
    }

    fun setOnCloseClick(clickEvent: () -> Unit) {
        binding.btnCustomTitleViewClose.setOnClickListener { clickEvent() }
    }

    fun setOnPlusClick(clickEvent: () -> Unit) {
        binding.btnCustomTitleViewPlus.setOnClickListener { clickEvent() }
    }

    fun setOnInviteClick(clickEvent: () -> Unit) {
        binding.btnCustomTitleViewInvite.setOnClickListener { clickEvent() }
    }
}
