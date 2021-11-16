package com.ariari.mowoori.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setNavController()
        setBottomNavVisibility()
    }

    private fun setNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container_main) as? NavHostFragment
                ?: return
        navController = navHostFragment.findNavController()
        binding.bottomNavMain.setupWithNavController(navController)
    }

    private fun setBottomNavVisibility() {
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            when (destination.id) {
                R.id.homeFragment, R.id.membersFragment -> showBottomNav()
                R.id.missionsFragment -> {
                    if (arguments != null) {
                        // 구성원의 미션
                        hideBottomNav()
                    } else {
                        // 나의 미션
                        showBottomNav()
                    }
                }
                else -> hideBottomNav()
            }
        }
    }

    private fun showBottomNav() {
        binding.bottomNavMain.isVisible = true
    }

    private fun hideBottomNav() {
        binding.bottomNavMain.isGone = true
    }
}
