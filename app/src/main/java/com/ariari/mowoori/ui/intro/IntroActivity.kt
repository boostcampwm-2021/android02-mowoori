package com.ariari.mowoori.ui.intro

import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.ActivityIntroBinding
import com.ariari.mowoori.ui.main.MainActivity
import com.ariari.mowoori.util.EventObserver
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroActivity : AppCompatActivity() {

    private val viewModel: IntroViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private val binding by lazy {
        ActivityIntroBinding.inflate(layoutInflater)
    }
    private lateinit var signLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.viewModel = viewModel
        auth = FirebaseAuth.getInstance()

        setListeners()
        setObservers()
        setSignLauncher()
    }

    private fun setSignLauncher() {
        signLauncher = registerForActivityResult(SignInIntentContract()) { tokenId: String? ->
            tokenId?.let {
                firebaseAuthWithGoogle(it)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser == null) {
            val animation = AlphaAnimation(0f, 1f).apply { duration = 2000 }
            binding.btnSplashSignIn.animation = animation
            binding.btnSplashSignIn.isVisible = true
        } else {
            viewModel.checkUserRegistered(currentUser.uid)
        }
    }

    private fun setListeners() {
        binding.btnSplashSignIn.setOnClickListener {
            signIn()
        }
    }

    private fun setObservers() {
        viewModel.isUserRegistered.observe(this, EventObserver {
            if (it) {
                moveToMain()
            } else {
                moveToRegister()
            }
        })
    }

    private fun signIn() {
        signLauncher.launch(getString(R.string.google_auth_client_id))
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    task.result.user?.let {
                        viewModel.checkUserRegistered(it.uid)
                    }
                } else {
                    Toast.makeText(this, "로그인 할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun moveToRegister() {
//        startActivity(Intent(this, RegisterActivity::class.java))
    }

    private fun moveToMain() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}
