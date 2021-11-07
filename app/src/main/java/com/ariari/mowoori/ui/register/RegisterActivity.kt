package com.ariari.mowoori.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Log.e
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.ActivityRegisterBinding
import com.ariari.mowoori.ui.main.MainActivity
import com.ariari.mowoori.util.EventObserver
import com.ariari.mowoori.util.TimberUtil
import com.ariari.mowoori.util.toastMessage
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels()
    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            viewModel.setProfileImage(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        setObservers()
        viewModel.createNickName()
    }

    private fun setObservers() {
        setInvalidNickNameObserver()
        setRegisterSuccessObserver()
        setProfileClickObserver()
    }

    private fun setInvalidNickNameObserver() {
        viewModel.invalidNicknameEvent.observe(this, EventObserver {
            toastMessage(getString(R.string.register_nickname_error_msg))
        })
    }

    private fun setRegisterSuccessObserver() {
        viewModel.registerSuccessEvent.observe(this, EventObserver {
            if (it) {
                moveToMain()
            } else {
                toastMessage(getString(R.string.register_fail_msg))
            }
        })
    }

    private fun setProfileClickObserver() {
        viewModel.profileImageClickEvent.observe(this, EventObserver {
            getContent.launch("image/*")
        })
    }

    private fun moveToMain() {
        startActivity(
            Intent(
                this,
                MainActivity::class.java
            ).apply { Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK })
    }
}
