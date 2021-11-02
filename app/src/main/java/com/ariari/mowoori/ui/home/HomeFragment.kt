package com.ariari.mowoori.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.FragmentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.random.Random


class HomeFragment : Fragment(), Handler.Callback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding ?: error(getString(R.string.binding_error))

    private val SNOWING_MESSAGE_ID = 10
    private val delayedSnowing: Handler = Handler(this@HomeFragment)
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
        delayedSnowing.sendEmptyMessageDelayed(SNOWING_MESSAGE_ID, 100)
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
        super.onDestroyView()
        isSnowing = false
        delayedSnowing.removeCallbacksAndMessages(null)
        _binding = null
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

    override fun handleMessage(msg: Message): Boolean {
        if (msg.what == SNOWING_MESSAGE_ID && isSnowing) {
            dropSnow()
            delayedSnowing.sendEmptyMessageDelayed(SNOWING_MESSAGE_ID, 100)
        }
        return true
    }
}
