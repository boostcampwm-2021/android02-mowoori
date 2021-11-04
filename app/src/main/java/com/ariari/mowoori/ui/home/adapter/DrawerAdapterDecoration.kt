package com.ariari.mowoori.ui.home.adapter

import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ariari.mowoori.databinding.ItemDrawerHeaderBinding

class DrawerAdapterDecoration : RecyclerView.ItemDecoration() {
    override fun onDrawOver(c: Canvas, recyclerView: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, recyclerView, state)
        val header =
            ItemDrawerHeaderBinding.inflate(LayoutInflater.from(recyclerView.context),
                recyclerView,
                false).root
        measureHeader(recyclerView, header)
        header.draw(c)
    }

    private fun measureHeader(recyclerView: ViewGroup, view: View) {
        // 리사이클러뷰 width, height 측정
        val widthSpec = View.MeasureSpec.makeMeasureSpec(
            recyclerView.width, // size
            View.MeasureSpec.EXACTLY // mode - match parent
        )
        val heightSpec = View.MeasureSpec.makeMeasureSpec(
            recyclerView.height,
            View.MeasureSpec.EXACTLY
        )
        // 헤더 width, height 측정
        val headerWidthSpec = ViewGroup.getChildMeasureSpec(
            widthSpec,
            recyclerView.paddingLeft + recyclerView.paddingRight,
            view.layoutParams.width
        )
        val headerHeightSpec = ViewGroup.getChildMeasureSpec(
            heightSpec,
            recyclerView.paddingTop + recyclerView.paddingBottom,
            view.layoutParams.height
        )
        // 헤더 크기 측정
        view.measure(headerWidthSpec, headerHeightSpec)
        // 헤더 위치 설정
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }
}
