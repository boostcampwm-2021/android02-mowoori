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

    @BindingAdapter(value = ["imageUrl", "isCircle"], requireAll = false)
    @JvmStatic
    fun ImageView.bindImageUrl(url: String?, isCircle: Boolean) {
        if (url.isNullOrEmpty()) {
            this.setImageResource(R.drawable.ic_empty)
        } else {
            if (isCircle) {
                Glide.with(this.context)
                    .load(url)
                    .circleCrop()
                    .into(this)
            } else {
                Glide.with(this.context)
                    .load(url)
                    .into(this)
            }
        }
    }

    @BindingAdapter("missionDate")
    @JvmStatic
    fun TextView.setText(date: Int) {
        this.text = getMissionStringFormatDate(date)
    }
}
