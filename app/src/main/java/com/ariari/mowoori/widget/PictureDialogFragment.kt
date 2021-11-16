package com.ariari.mowoori.widget

import android.os.Bundle
import android.view.View
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.DialogPictureBinding

class PictureDialogFragment(private val onClick: () -> Unit) :
    BaseDialogFragment<DialogPictureBinding>(R.layout.dialog_picture) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()
    }

    private fun setClickListener() {
        binding.containerDialogPictureGallery.setOnClickListener {
            onClick()
            dismiss()
        }
        binding.containerDialogPictureCamera.setOnClickListener {
            // TODO: 사진촬영
        }
    }
}
