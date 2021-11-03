package com.ariari.mowoori.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.FragmentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.random.Random


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding ?: error(getString(R.string.binding_error))

    private var isSnowing = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        showBottomNavigation()
        setDrawerOpenListener()
        setGroupAddClickListener()
        setAnimator()
    }

    private fun showBottomNavigation() {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_main).visibility =
            View.VISIBLE
    }

    private fun setDrawerOpenListener() {
        binding.toolbarHome.setNavigationOnClickListener {
            binding.drawerHome.open()
        }
    }

    private fun setGroupAddClickListener() {
        binding.navViewDrawer.getHeaderView(0).findViewById<TextView>(R.id.tv_drawer_header_add)
            .setOnClickListener {
                it.findNavController().navigate(R.id.action_homeFragment_to_inviteCheckFragment)
            }
    }

    override fun onDestroyView() {
        Timber.d("destroy")
        super.onDestroyView()
        isSnowing = false
        _binding = null
        Timber.d("$isSnowing")
    }

    private fun setAnimator() {
        Timber.d("animator")
        viewLifecycleOwner.lifecycleScope.launch {
            while (isSnowing) {
                Timber.d("scope")
                dropSnow()
                delay(100)
            }
        }
    }

    private fun makeSnow() = ImageView(requireContext()).apply {
        setImageResource(R.drawable.ic_snow)
        // snow 크기 설정
        scaleX = Random.nextFloat() * .7f + .2f
        scaleY = scaleX
    }

    private fun dropSnow() {
        val snow = makeSnow()
        binding.containerHome.addView(snow)

        // snow 좌표 설정
        val snowStartHeight = snow.scaleY + 100f
        val startX = Random.nextFloat() * binding.containerHome.width
        val endX = Random.nextFloat() * binding.containerHome.width

        val moverX = ObjectAnimator.ofFloat(snow, View.TRANSLATION_X, startX, endX)
        val moverY = ObjectAnimator.ofFloat(
            snow,
            View.TRANSLATION_Y,
            -snowStartHeight,
            binding.containerHome.height + snowStartHeight
        ).apply {
            interpolator = AccelerateInterpolator(1f)
        }

        val set = AnimatorSet().apply {
            playTogether(moverX, moverY)
            duration = (Math.random() * 3000 + 3000).toLong()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    _binding?.containerHome?.removeView(snow)
                }
            })
        }
        set.start()
    }
}
