package com.ariari.mowoori.ui.home.entity

import android.widget.ImageView
import com.ariari.mowoori.databinding.LayoutSnowmanFaceLv4Binding

data class Lv4Component(
    val face: LayoutSnowmanFaceLv4Binding,
    val hands: List<ImageView>,
    val body: ImageView,
    val exclamations: List<ImageView>,
    val hearts: List<ImageView>,
)
