package com.ariari.mowoori.ui.home

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ariari.mowoori.R
import com.ariari.mowoori.base.BaseFragment
import com.ariari.mowoori.databinding.FragmentHomeBinding
import com.ariari.mowoori.ui.home.adapter.DrawerAdapter
import com.ariari.mowoori.ui.home.adapter.DrawerAdapterDecoration
import com.ariari.mowoori.ui.home.animator.WinterAnimatorLv2
import com.ariari.mowoori.ui.home.animator.WinterAnimatorLv3
import com.ariari.mowoori.util.EventObserver
import com.ariari.mowoori.util.TimberUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.random.Random

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var adapter: DrawerAdapter

    // 1단계 눈내리는 애니메이션
    private var snowJob: Job? = null

    // 2단계 눈사람 얼굴 애니메이션
    private val winterAnimatorLv2 by lazy {
        WinterAnimatorLv2(
            binding.ivHomeSnowFace,
            homeViewModel,
            requireContext()
        )
    }

    // 3단계 눈사람 얼굴, 몸통 애니메이션
    private val winterAnimatorLv3 by lazy {
        WinterAnimatorLv3(
            binding.ivHomeSnowmanFace,
            binding.ivHomeSnowmanBody,
            homeViewModel,
            requireContext()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUserInfo()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = homeViewModel
        setUserInfoObserver()
        setCurrentGroupInfoObserver()
        setGroupInfoListObserver()
        setDrawerOpenListener()
        setDrawerAdapter()
        setRecyclerViewDecoration()
        setObserver()
        setSnowmanLevel(SnowmanLevel.SNOW_FACE)
        setClickListener()
        setMenuListener()
    }

    override fun onDestroyView() {
        Timber.d("destroy")
        homeViewModel.cancelSnowAnimList()
        super.onDestroyView()
    }

    private fun setUserInfo() {
        homeViewModel.setUserInfo()
    }

    private fun setUserInfoObserver() {
        homeViewModel.userInfo.observe(viewLifecycleOwner, EventObserver { userInfo ->
            homeViewModel.setGroupInfoList(userInfo)
        })
    }

    private fun setCurrentGroupInfoObserver() {
        homeViewModel.currentGroupInfo.observe(viewLifecycleOwner, { group ->
            adapter.notifyDataSetChanged()
        })
    }

    private fun setGroupInfoListObserver() {
        homeViewModel.groupList.observe(viewLifecycleOwner, { groupList ->
            adapter.groups = groupList
            adapter.notifyDataSetChanged()
        })
    }

    private fun setMenuListener() {
        binding.tbHome.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.alarmFragment -> {
                    findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
                }
                R.id.menu_tb_home_sun -> {
                    TimberUtil.timber("lottie", "sun")
                    binding.lottieHomeSun.apply {
                        isVisible = true
                        addAnimatorListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                super.onAnimationEnd(animation)
                                isVisible = false
                            }
                        })
                        playAnimation()
                    }
                }
            }
            false
        }
    }

    private fun setDrawerOpenListener() {
        binding.tbHome.setNavigationOnClickListener {
            binding.drawerHome.open()
        }
    }

    private fun setDrawerAdapter() {
        adapter = DrawerAdapter(object : DrawerAdapter.OnItemClickListener {
            override fun itemClick(groupId: String) {
                homeViewModel.setCurrentGroupInfo(groupId)
                binding.drawerHome.close()
            }
        })
        binding.rvDrawer.adapter = adapter
    }

    private fun setRecyclerViewDecoration() {
        val itemDecoration = DrawerAdapterDecoration()
        binding.rvDrawer.addItemDecoration(itemDecoration)
    }

    private fun setObserver() {
        homeViewModel.isSnowing.observe(viewLifecycleOwner) {
            updateSnowAnimation(it)
        }
        homeViewModel.snowmanLevel.observe(viewLifecycleOwner) {
            updateSnowmanAnimation(it)
        }
    }

    private fun setClickListener() {
        binding.containerHome.setOnClickListener {
            homeViewModel.updateIsSnowing()
        }
    }

    private fun setSnowmanLevel(level: SnowmanLevel) {
        homeViewModel.updateSnowmanLevel(level)
    }

    private fun updateSnowAnimation(isSnowing: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {
            if (isSnowing) {
                TimberUtil.timber("start", snowJob.toString())
                snowJob = launch {
                    while (isActive) {
                        dropSnow(100L)
                    }
                }
            } else {
                TimberUtil.timber("cancel", snowJob.toString())
                snowJob!!.cancelAndJoin()
            }
        }
    }

    private fun makeSnow() = ImageView(requireContext()).apply {
        setImageResource(R.drawable.ic_snow)
        // snow 크기 설정
        scaleX = Random.nextFloat() * .3f + .2f
        scaleY = scaleX
    }

    private suspend fun dropSnow(delayTime: Long) {
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

        val snowAnimSet = AnimatorSet().apply {
            playTogether(moverX, moverY)
            duration = (Math.random() * 3000 + 3000).toLong()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: Animator?) {
                    super.onAnimationCancel(animation)
                }

                override fun onAnimationEnd(animation: Animator?) {
                    // Timber.d("End")
                    super.onAnimationEnd(animation)
                    binding.containerHome.removeView(snow)
                }
            })
        }
        homeViewModel.addSnowAnim(snowAnimSet)
        snowAnimSet.start()
        delay(delayTime)
    }

    private fun updateSnowmanAnimation(snowmanLevel: SnowmanLevel) {
        viewLifecycleOwner.lifecycleScope.launch {
            when (snowmanLevel) {
                SnowmanLevel.SNOW_NO -> {
                    // TODO: 눈사람이 녹아버리는 애니메이션 추가
                }
                SnowmanLevel.SNOW_FACE -> {
                    // 2단계 눈사람 - 얼굴 통통 애니메이션
                    binding.ivHomeSnowFace.isVisible = true
                    winterAnimatorLv2.start()
                }
                SnowmanLevel.SNOW_BODY -> {
                    // 3단계 눈사람 - 얼굴 몸통 합체 애니메이션
                    binding.ivHomeSnowmanFace.setImageResource(R.drawable.ic_snowman_face_3_rotate)
                    winterAnimatorLv3.start()
                }
                SnowmanLevel.SNOW_CLOTHES -> {
                    // TODO: 4단계(최종) 눈사람 - 팔 등 추가 장식
                }
            }
        }
    }
}
