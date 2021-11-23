package com.ariari.mowoori.ui.intro

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.ariari.mowoori.BuildConfig
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.ActivityIntroBinding
import com.ariari.mowoori.ui.main.MainActivity
import com.ariari.mowoori.ui.register.RegisterActivity
import com.ariari.mowoori.util.EventObserver
import com.ariari.mowoori.util.LogUtil
import com.ariari.mowoori.util.toastMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class IntroActivity : AppCompatActivity() {

    private val introViewModel: IntroViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private val binding by lazy {
        ActivityIntroBinding.inflate(layoutInflater)
    }
    private val signLauncher =
        registerForActivityResult(SignInIntentContract()) { tokenId: String? ->
            tokenId?.let {
                firebaseAuthWithGoogle(it)
            }
        }

    private val permissionList = listOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
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
                introViewModel.updateFcmToken()
                introViewModel.setUserRegistered(true)
                moveToMain()
            } else {
                moveToRegister()
            }
        })
    }

    private fun signIn() {
        signLauncher.launch(getString(R.string.default_web_client_id))
    }

    private fun signInTester(num: Int) {
        binding.llTest.isVisible = false
        auth.signInWithEmailAndPassword("testid$num@test.com", "testid$num")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.user?.let {
                        moveToMain()
                    }
                } else {
                    Toast.makeText(this, "로그인 할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    binding.llTest.isVisible = true
                }
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    task.result.user?.let {
                        introViewModel.checkUserRegistered(it.uid)
                    }
                } else {
                    Toast.makeText(this, "로그인 할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
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
        if (auth.currentUser != null && introViewModel.getUserRegistered()) {
            signIn()
        }
        else{
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
                it.entries.forEach { it ->
                    val permissionName = it.key
                    val isGranted = it.value
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
}
