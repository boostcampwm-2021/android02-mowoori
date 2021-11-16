package com.ariari.mowoori.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ariari.mowoori.R
import com.ariari.mowoori.base.BaseFragment
import com.ariari.mowoori.databinding.FragmentHomeBinding
import com.ariari.mowoori.ui.home.adapter.DrawerAdapter
import com.ariari.mowoori.ui.home.adapter.DrawerAdapterDecoration
import com.ariari.mowoori.ui.home.animator.SnowAnimator
import com.ariari.mowoori.ui.home.animator.SnowmanLv2Animator
import com.ariari.mowoori.ui.home.animator.SnowmanLv3Animator
import com.ariari.mowoori.ui.home.animator.SnowmanLv4Animator
import com.ariari.mowoori.ui.home.entity.Lv4Component
import com.ariari.mowoori.ui.home.entity.ViewInfo
import com.ariari.mowoori.util.EventObserver
import com.ariari.mowoori.util.LogUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var adapter: DrawerAdapter

    // 1단계 눈내리는 애니메이션
    private var snowJob: Job? = null
    private val snowAnimator by lazy {
        SnowAnimator(
            binding.containerHome,
            homeViewModel,
            requireContext()
        )
    }

    // 2단계 눈사람 얼굴 애니메이션
    private val snowmanLv2Animator by lazy {
        SnowmanLv2Animator(
            binding.ivHomeSnowmanFaceLv2,
            homeViewModel,
            requireContext()
        )
    }

    // 3단계 눈사람 얼굴, 몸통 애니메이션
    private val snowmanLv3Animator by lazy {
        SnowmanLv3Animator(
            binding.ivHomeSnowmanFaceLv3,
            binding.ivHomeSnowmanBody,
            arrayOf(
                binding.ivHomeSnowmanButtonTop,
                binding.ivHomeSnowmanButtonMiddle,
                binding.ivHomeSnowmanButtonBottom
            ),
            homeViewModel,
            requireContext()
        )
    }

    // 4단계 눈사람 팔 애니메이션
    private val snowmanLv4Animator by lazy {
        SnowmanLv4Animator(Lv4Component(binding.layoutHomeSnowmanFaceLv4,
            listOf(binding.ivHomeSnowmanLeftHand, binding.ivHomeSnowmanRightHand),
            binding.ivHomeSnowmanBody,
            listOf(binding.ivHomeFirstExclamation, binding.ivHomeSecondExclamation),
            listOf(binding.ivHomeLeftHeart, binding.ivHomeRightHeart)),
            homeViewModel,
            viewLifecycleOwner,
            requireContext())
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
        // 임시로 3단계로 설정 추후에 단계별 애니메이션 나오도록 설정 필요
        setSnowmanLevel(SnowmanLevel.LV4)
        setClickListener()
        setMenuListener()
    }

    override fun onDestroyView() {
        Timber.d("destroy")
        homeViewModel.cancelAnimator()
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
                    LogUtil.log("lottie", "sun")
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
            updateWinterAnimation(it)
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
                LogUtil.log("start", snowJob.toString())
                snowJob = launch {
                    while (isActive) {
                        snowAnimator.dropSnow(100L)
                    }
                }
            } else {
                LogUtil.log("cancel", snowJob.toString())
                snowJob!!.cancelAndJoin()
            }
        }
    }

    private fun updateWinterAnimation(snowmanLevel: SnowmanLevel) {
        viewLifecycleOwner.lifecycleScope.launch {
            when (snowmanLevel) {
                SnowmanLevel.LV1 -> {
                    // TODO: 눈사람이 녹아버리는 애니메이션 추가
                }
                SnowmanLevel.LV2 -> {
                    // 2단계 눈사람 - 얼굴 통통 애니메이션
                    binding.ivHomeSnowmanFaceLv2.isVisible = true
                    snowmanLv2Animator.start()
                }
                SnowmanLevel.LV3 -> {
                    // 3단계 눈사람 - 얼굴 몸통 합체 애니메이션
                    binding.ivHomeSnowmanFaceLv3.setImageResource(R.drawable.ic_snowman_face_3_rotate)
                    snowmanLv3Animator.start()
                }
                SnowmanLevel.LV4 -> {
                    snowmanLv4Animator.start()
                }
            }
        }
    }
}
