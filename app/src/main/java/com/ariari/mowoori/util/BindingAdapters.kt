package com.ariari.mowoori.util

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.ariari.mowoori.R
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

    @BindingAdapter("setSrcFromUrl")
    @JvmStatic
    fun setSrcFromUrl(imageView: ImageView, url: String?) {
        if (url == null) {
            imageView.setImageResource(R.drawable.ic_empty)
        } else {
            Glide.with(imageView.context)
                .load(url)
                .into(imageView)
        }
    }

    @BindingAdapter("missionDate")
    @JvmStatic
    fun TextView.setText(date: Int) {
        this.text = getMissionStringFormatDate(date)
    }
}
