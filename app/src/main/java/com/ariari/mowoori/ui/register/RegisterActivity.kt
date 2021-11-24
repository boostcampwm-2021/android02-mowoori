package com.ariari.mowoori.ui.register

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.ActivityRegisterBinding
import com.ariari.mowoori.ui.main.MainActivity
import com.ariari.mowoori.util.EventObserver
import com.ariari.mowoori.util.hideKeyBoard
import com.ariari.mowoori.util.isNetWorkAvailable
import com.ariari.mowoori.util.toastMessage
import com.ariari.mowoori.widget.BaseDialogFragment
import com.ariari.mowoori.widget.ConfirmDialogFragment
import com.ariari.mowoori.widget.NetworkDialogFragment
import com.ariari.mowoori.widget.ProgressDialogManager
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private val registerViewModel: RegisterViewModel by viewModels()
    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            registerViewModel.setProfileImage(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.viewModel = registerViewModel
        binding.lifecycleOwner = this
        setObservers()
        setRootClick()
        setCompleteClick()
        registerViewModel.createNickName()
        registerViewModel.initFcmToken()
    }

    private fun setObservers() {
        setInvalidNickNameObserver()
        setRegisterSuccessObserver()
        setProfileClickObserver()
        setLoadingEventObserver()
        setNetworkDialogObserver()
    }

    private fun setRootClick() {
        binding.root.setOnClickListener {
            this.hideKeyBoard(it)
            currentFocus?.clearFocus()
        }
    }

    private fun setCompleteClick() {
        binding.btnRegisterComplete.setOnClickListener {
            ConfirmDialogFragment(object : BaseDialogFragment.NoticeDialogListener {
                override fun onDialogPositiveClick(dialog: DialogFragment) {
                    dialog.dismiss()
                    registerUserInfo()
                }

                override fun onDialogNegativeClick(dialog: DialogFragment) {
                    dialog.dismiss()
                }
            }).show(supportFragmentManager, "ConfirmFragment")
        }
    }

    private fun registerUserInfo() {
        if (this.isNetWorkAvailable()) {
            registerViewModel.initFcmServerKey()
            registerViewModel.registerUserInfo()
        } else {
            showNetworkDialog()
        }
    }

    private fun setInvalidNickNameObserver() {
        registerViewModel.invalidNicknameEvent.observe(this, {
            toastMessage(it.message)
        })
    }

    private fun setRegisterSuccessObserver() {
        registerViewModel.registerSuccessEvent.observe(this, EventObserver {
            if (it) {
                registerViewModel.setUserRegistered(true)
                moveToMain()
            } else {
                toastMessage(getString(R.string.register_fail_msg))
            }
        })
    }

    private fun setProfileClickObserver() {
        registerViewModel.profileImageClickEvent.observe(this, EventObserver {
            getContent.launch("image/*")
        })
    }

    private fun setLoadingEventObserver() {
        registerViewModel.loadingEvent.observe(this, EventObserver {
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

    private fun setNetworkDialogObserver() {
        registerViewModel.networkDialogEvent.observe(this, {
            if (it) {
                showNetworkDialog()
            }
        })
    }

    private fun showNetworkDialog() {
        NetworkDialogFragment(object : NetworkDialogFragment.NetworkDialogListener {
            override fun onCancelClick(dialog: DialogFragment) {
                dialog.dismiss()
                finishAffinity()
                System.runFinalization()
                exitProcess(0)
            }

            override fun onRetryClick(dialog: DialogFragment) {
                dialog.dismiss()
                registerUserInfo()
            }
        }).show(supportFragmentManager, "NetworkDialogFragment")
    }
}
