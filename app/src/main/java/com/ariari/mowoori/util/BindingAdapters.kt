package com.ariari.mowoori.util

import androidx.databinding.BindingAdapter
import com.ariari.mowoori.ui.custom.CustomTitleView

object BindingAdapters {
    @BindingAdapter("setTitleViewMode")
    @JvmStatic
    fun CustomTitleView.setTitleViewMode(mode: Int) {
        this.setTitleViewMode(mode)
    }

    @BindingAdapter("onBackClick")
    @JvmStatic
    fun CustomTitleView.onBackClick(clickEvent: () -> Unit) {
        this.setBackButtonClickListener(clickEvent)
    }

    @BindingAdapter("onCloseClick")
    @JvmStatic
    fun CustomTitleView.onCloseClick(clickEvent: () -> Unit) {
        this.setCloseButtonClickListener(clickEvent)
    }

    @BindingAdapter("onPlusClick")
    @JvmStatic
    fun CustomTitleView.onPlusClick(clickEvent: () -> Unit) {
        this.setPlusButtonClickListener(clickEvent)
    }
}
