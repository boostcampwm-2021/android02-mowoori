package com.ariari.mowoori.ui.register

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.ariari.mowoori.R
import com.ariari.mowoori.data.local.datasource.MoWooriPrefDataSource
import com.ariari.mowoori.databinding.ActivityRegisterBinding
import com.ariari.mowoori.ui.main.MainActivity
import com.ariari.mowoori.util.EventObserver
import com.ariari.mowoori.util.isNetWorkAvailable
import com.ariari.mowoori.util.toastMessage
import com.ariari.mowoori.widget.BaseDialogFragment
import com.ariari.mowoori.widget.ConfirmDialogFragment
import com.ariari.mowoori.widget.NetworkDialogFragment
import com.ariari.mowoori.widget.ProgressDialogManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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
            hideKeyboard(it)
            currentFocus?.clearFocus()
        }
    }

    private fun hideKeyboard(v: View) {
        // InputMethodManager 를 통해 가상 키보드를 숨길 수 있다.
        // 현재 focus 되어있는 뷰의 windowToken 을 hideSoftInputFromWindow 메서드의 매개변수로 넘겨준다.
        val inputMethodManager =
            this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
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
        registerViewModel.invalidNicknameEvent.observe(this, EventObserver {
            toastMessage(getString(R.string.register_nickname_error_msg))
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
            }

            override fun onRetryClick(dialog: DialogFragment) {
                dialog.dismiss()
                registerUserInfo()
            }
        }).show(supportFragmentManager, "NetworkDialogFragment")
    }
}
