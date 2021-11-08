package com.ariari.mowoori.ui.register

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.ActivityRegisterBinding
import com.ariari.mowoori.ui.main.MainActivity
import com.ariari.mowoori.util.EventObserver
import com.ariari.mowoori.util.toastMessage
import com.ariari.mowoori.widget.ProgressDialogManager
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
        setLoadingEventObserver()
    }

    private fun setInvalidNickNameObserver() {
        viewModel.invalidNicknameEvent.observe(this, EventObserver {
            toastMessage(getString(R.string.register_nickname_error_msg))
        })
    }

    private fun setRegisterSuccessObserver() {
        viewModel.registerSuccessEvent.observe(this, EventObserver {
            ProgressDialogManager.instance.clear()
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

    private fun setLoadingEventObserver() {
        viewModel.loadingEvent.observe(this, EventObserver {
            if (it) {
                ProgressDialogManager.instance.show(this)
            } else {
                ProgressDialogManager.instance.clear()
            }
        })
    }

    private fun moveToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
    }
}
