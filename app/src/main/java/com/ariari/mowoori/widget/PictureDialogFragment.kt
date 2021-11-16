package com.ariari.mowoori.widget

import android.os.Bundle
import android.view.View
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.DialogPictureBinding
import com.ariari.mowoori.ui.stamp_detail.entity.PictureType

class PictureDialogFragment(private val onClick: (pictureType: PictureType) -> Unit) :
    BaseDialogFragment<DialogPictureBinding>(R.layout.dialog_picture) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()
    }

    private fun setClickListener() {
        binding.containerDialogPictureGallery.setOnClickListener {
            onClick(PictureType.GALLERY)
            dismiss()
        }
        binding.containerDialogPictureCamera.setOnClickListener {
            onClick(PictureType.CAMERA)
            dismiss()
            // TODO: 사진촬영
        }
    }
}
