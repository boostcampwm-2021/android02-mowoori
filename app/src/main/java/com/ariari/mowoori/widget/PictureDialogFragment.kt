package com.ariari.mowoori.widget

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.DialogPictureBinding
import com.ariari.mowoori.ui.stamp_detail.entity.PictureType

class PictureDialogFragment :
    BaseDialogFragment<DialogPictureBinding>(R.layout.dialog_picture) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()
    }

    private fun setClickListener() {
        binding.containerDialogPictureGallery.setOnClickListener {
            setFragmentResult(
                PICTURE_DIALOG,
                bundleOf(
                    PICTURE_DIALOG to PictureType.GALLERY
                )
            )
            dismiss()
        }
        binding.containerDialogPictureCamera.setOnClickListener {
            setFragmentResult(
                PICTURE_DIALOG,
                bundleOf(
                    PICTURE_DIALOG to PictureType.CAMERA
                )
            )
            dismiss()
        }
    }

    companion object {
        const val PICTURE_DIALOG = "picture dialog"
    }
}
