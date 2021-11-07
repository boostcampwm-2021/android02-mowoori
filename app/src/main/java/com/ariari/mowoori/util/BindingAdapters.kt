package com.ariari.mowoori.util

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

object BindingAdapters {

    @BindingAdapter(value = ["imageUri", "isCircle"], requireAll = false)
    @JvmStatic
    fun bindImageUri(
        imageView: ImageView,
        uri: Uri?,
        isCircle: Boolean = false,
    ) {
        uri?.let {
            var builder = Glide.with(imageView).load(it)
            if (isCircle)
                builder = builder.apply(RequestOptions.circleCropTransform())
            builder.into(imageView)
        }
    }
}
