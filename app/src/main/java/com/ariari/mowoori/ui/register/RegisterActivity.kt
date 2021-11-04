package com.ariari.mowoori.ui.register

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.ActivityRegisterBinding
import com.ariari.mowoori.ui.main.MainActivity
import com.ariari.mowoori.util.EventObserver
import com.ariari.mowoori.util.toastMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels()
    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.viewModel = viewModel
        setObservers()
        viewModel.createNickName()
    }

    private fun setObservers() {
        setInvalidNickNameObserver()
        setRegisterSuccessObserver()
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

    private fun moveToMain() {
        startActivity(
            Intent(
                this,
                MainActivity::class.java
            ).apply { Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK })
    }
}
