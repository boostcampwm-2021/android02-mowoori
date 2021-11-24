package com.ariari.mowoori.ui.intro

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.animation.AlphaAnimation
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.ariari.mowoori.BuildConfig
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.ActivityIntroBinding
import com.ariari.mowoori.ui.main.MainActivity
import com.ariari.mowoori.ui.register.RegisterActivity
import com.ariari.mowoori.util.EventObserver
import com.ariari.mowoori.util.LogUtil
import com.ariari.mowoori.util.isNetWorkAvailable
import com.ariari.mowoori.util.toastMessage
import com.ariari.mowoori.widget.NetworkDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.system.exitProcess

@AndroidEntryPoint
class IntroActivity : AppCompatActivity() {

    private val introViewModel: IntroViewModel by viewModels()
    private val binding by lazy {
        ActivityIntroBinding.inflate(layoutInflater)
    }
    private val signLauncher =
        registerForActivityResult(SignInIntentContract()) { tokenId: String? ->
            tokenId?.let {
                introViewModel.firebaseAuthWithGoogle(it)
            }
        }

    private val permissionList = listOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        introViewModel.setFirebaseAuth()
        autoLogin()
        binding.viewModel = introViewModel
        introViewModel.initFcmToken()
        setListeners()
        setObservers()
        requestPermissions(permissionList)

        //For Test
        if (BuildConfig.DEBUG) {
            binding.test.isVisible = true
            binding.test.setOnClickListener { binding.llTest.isVisible = !binding.llTest.isVisible }
            binding.test1.setOnClickListener { signInTester(1) }
            binding.test2.setOnClickListener { signInTester(2) }
            binding.test3.setOnClickListener { signInTester(3) }
            binding.test4.setOnClickListener { signInTester(4) }
        }
    }

    private fun setListeners() {
        binding.btnSplashSignIn.setOnClickListener {
            signIn()
        }
    }

    private fun setObservers() {
        introViewModel.isUserRegistered.observe(this, EventObserver {
            if (it) {
                introViewModel.updateFcmServerKey()
                introViewModel.updateFcmToken()
                introViewModel.setUserRegistered(true)
                moveToMain()
            } else {
                moveToRegister()
            }
        })

        introViewModel.isTestLoginSuccess.observe(this, {
            if (it) {
                moveToMain()
            } else {
                binding.llTest.isVisible = true
            }
        })
        setNetworkDialogObserver()
    }

    private fun signIn() {
        if (this.isNetWorkAvailable()) {
            signLauncher.launch(getString(R.string.default_web_client_id))
        } else {
            showNetworkDialog()
        }
    }

    private fun signInTester(num: Int) {
        binding.llTest.isVisible = false
        introViewModel.firebaseAuthWithGoogle(null, "testid$num@test.com", "testid$num")
    }

    private fun moveToRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    private fun moveToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
    }

    private fun autoLogin() {
        if (introViewModel.auth.currentUser != null && introViewModel.getUserRegistered()) {
            signIn()
        } else {
            showSignInButton()
        }
    }

    private fun showSignInButton() {
        val animation = AlphaAnimation(0f, 1f).apply { duration = 2000 }
        binding.btnSplashSignIn.animation = animation
        binding.btnSplashSignIn.isVisible = true
    }

    private fun requestPermissions(permissions: List<String>) {
        if (!hasPermissions(permissions)) {
            Timber.d("hasPermission false")
            toastMessage("앱 사용 중에 이미지 저장을 위해 반드시 외부저장소 권한이 필요합니다!!!!!!!")
            activityPermissionLauncher.launch(permissions.toTypedArray())
        }
    }

    private fun hasPermissions(permissions: List<String>): Boolean {
        permissions.forEach {
            if (ContextCompat.checkSelfPermission(
                    this,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@hasPermissions false
            }
        }
        return true
    }

    private val activityPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions: Map<String, Boolean>? ->
            Timber.d("register까지 잘 들어왔음")
            permissions?.let {
                it.entries.forEach { map ->
                    val permissionName = map.key
                    val isGranted = map.value
                    if (isGranted) {
                        LogUtil.log("permissionName", permissionName)
//                    when (permissionName) {
//                        else -> toastMessage("외부 저장소 쓰기/읽기 권한이 추가되었습니다.")
//                    }
                    } else {
                        Timber.d("register 문제발생")
                        // toastMessage("앱 사용 중에 이미지 저장을 위해 반드시 외부저장소 권한이 필요합니다.")
                        //finish()
                    }
                }
            }
        }

    private fun setNetworkDialogObserver() {
        introViewModel.networkDialogEvent.observe(this, {
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
                signIn()
            }
        }).show(supportFragmentManager, "NetworkDialogFragment")
    }
}
