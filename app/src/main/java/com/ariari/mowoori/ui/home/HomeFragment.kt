package com.ariari.mowoori.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
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
import com.ariari.mowoori.ui.home.animator.SnowAnimator
import com.ariari.mowoori.ui.home.animator.SnowmanLv2Animator
import com.ariari.mowoori.ui.home.animator.SnowmanLv3Animator
import com.ariari.mowoori.ui.home.animator.SnowmanLv4Animator
import com.ariari.mowoori.ui.home.animator.SnowmanLv4Animator.Companion.ADD_SOURCES
import com.ariari.mowoori.ui.home.animator.SnowmanLv4Animator.Companion.BODY_MEASURE
import com.ariari.mowoori.ui.home.animator.SnowmanLv4Animator.Companion.LEFT_BLACK_DONE
import com.ariari.mowoori.ui.home.animator.SnowmanLv4Animator.Companion.LEFT_WHITE_DONE
import com.ariari.mowoori.ui.home.animator.SnowmanLv4Animator.Companion.RESET_ALPHA
import com.ariari.mowoori.ui.home.animator.SnowmanLv4Animator.Companion.RIGHT_BLACK_DONE
import com.ariari.mowoori.ui.home.animator.SnowmanLv4Animator.Companion.RIGHT_WHITE_DONE
import com.ariari.mowoori.ui.home.entity.Lv4Component
import com.ariari.mowoori.ui.stamp.entity.DetailInfo
import com.ariari.mowoori.util.EventObserver
import com.ariari.mowoori.util.LogUtil
import com.ariari.mowoori.util.MowooriMessagingService
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
    private lateinit var snowAnimator: SnowAnimator

    // 2단계 눈사람 얼굴 애니메이션
    private lateinit var snowmanLv2Animator: SnowmanLv2Animator

    // 3단계 눈사람 얼굴, 몸통 애니메이션
    private lateinit var snowmanLv3Animator: SnowmanLv3Animator

    // 4단계 눈사람 팔 애니메이션
    private lateinit var snowmanLv4Animator: SnowmanLv4Animator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUserInfo()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = homeViewModel
        binding.layoutHomeSnowmanFaceLv4.viewModel = homeViewModel
        setAnimators()
        setUserInfoObserver()
        setGroupInfoListObserver()
        setDrawerOpenListener()
        setDrawerAdapter()
        setObservers()
        setClickListener()
        setMenuListener()
        setPlusClickListener()
    }

    private fun setAnimators() {
        LogUtil.log("lv3_body",binding.ivHomeSnowmanBody.toString())
        LogUtil.log("lv3_face",binding.ivHomeSnowmanFaceLv3.toString())
        snowAnimator = SnowAnimator(
            container = binding.containerHome,
            addAnimator = { animator -> addSnowAnimator(animator) },
            context = requireContext()
        )
        snowmanLv2Animator =
            SnowmanLv2Animator(
                face = binding.ivHomeSnowmanFaceLv2,
                addAnimator = { animator -> addAnimator(animator) },
                context = requireContext()
            )
        snowmanLv3Animator =
            SnowmanLv3Animator(
                face = binding.ivHomeSnowmanFaceLv3,
                body = binding.ivHomeSnowmanBody,
                buttons = arrayOf(
                    binding.ivHomeSnowmanButtonTop,
                    binding.ivHomeSnowmanButtonMiddle,
                    binding.ivHomeSnowmanButtonBottom
                ),
                addAnimator = { animator -> addAnimator(animator) },
                context = requireContext()
            )
        snowmanLv4Animator = SnowmanLv4Animator(
            component = Lv4Component(
                binding.layoutHomeSnowmanFaceLv4,
                listOf(binding.ivHomeSnowmanLeftHand, binding.ivHomeSnowmanRightHand),
                binding.ivHomeSnowmanBody,
                listOf(binding.ivHomeFirstExclamation, binding.ivHomeSecondExclamation),
                listOf(binding.ivHomeLeftHeart, binding.ivHomeRightHeart)
            ),
            addAnimator = { animator -> addAnimator(animator) },
            updateAttributes = { attributes -> updateLv4Attributes(attributes) },
            context = requireContext()
        )
    }

    override fun onDestroyView() {
        Timber.d("destroy")
        homeViewModel.cancelSnowAnimator()
        homeViewModel.cancelAnimator()
        homeViewModel.removeSources()
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

    private fun setGroupInfoListObserver() {
        homeViewModel.groupList.observe(viewLifecycleOwner, { groupList ->
            if (groupList.isEmpty()) {
                binding.tvHomeEmpty.isVisible = true
                binding.tvHomeEmptyDrawer.isVisible = true
            } else {
                binding.tvHomeEmpty.isVisible = false
                binding.tvHomeEmptyDrawer.isVisible = false
                adapter.submitList(groupList)
            }
        })
    }

    private fun setMenuListener() {
        binding.tbHome.setOnMenuItemClickListener { item ->
            when (item.itemId) {
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
//                R.id.alarmFragment -> {
//                    findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
//                }
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
                homeViewModel.cancelAnimator()
                homeViewModel.setCurrentGroupInfo(groupId)
                binding.drawerHome.close()
            }
        })
        binding.rvDrawer.adapter = adapter
    }

    private fun setObservers() {
        homeViewModel.isSnowing.observe(viewLifecycleOwner) {
            updateSnowAnimation(it)
        }
        homeViewModel.snowmanLevel.observe(viewLifecycleOwner) {
            updateWinterAnimation(it)
        }
        homeViewModel.isBodyMeasured.observe(viewLifecycleOwner, {
            if (it) {
                snowmanLv4Animator.setBlackViewInfo()
            }
        })
        homeViewModel.blackEyeViewInfoMediator.observe(viewLifecycleOwner, {
            if (it) {
                snowmanLv4Animator.setWhiteViewInfo()
                homeViewModel.doneBlackViewInfo()
            }
        })
        homeViewModel.whiteEyeViewInfoMediator.observe(viewLifecycleOwner, {
            if (it) {
                snowmanLv4Animator.setObjectAnimators()
                homeViewModel.doneWhiteViewInfo()
            }
        })
    }

    private fun setClickListener() {
        binding.containerHome.setOnClickListener {
            homeViewModel.updateIsSnowing()
        }
    }

    private fun updateSnowAnimation(isSnowing: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {
            if (isSnowing) {
                LogUtil.log("start", snowJob.toString())
                snowJob = launch {
                    while (isActive) {
                        snowAnimator.dropSnow(200L)
                    }
                }
            } else {
                LogUtil.log("cancel", snowJob.toString())
                snowJob?.cancelAndJoin()
            }
        }
    }

    private fun updateWinterAnimation(snowmanLevel: SnowmanLevel) {
        when (snowmanLevel) {
            SnowmanLevel.LV1 -> {
                // 1단계 - 눈만 내리는 애니메이션
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

    private fun addSnowAnimator(animator: Animator) {
        homeViewModel.addSnowAnimator(animator)
    }

    private fun addAnimator(animator: Animator) {
        homeViewModel.addAnimator(animator)
    }

    private fun updateLv4Attributes(step: Int) {
        when (step) {
            ADD_SOURCES -> homeViewModel.addSources()
            RESET_ALPHA -> homeViewModel.resetAlphaForLv4()
            BODY_MEASURE -> homeViewModel.bodyMeasured()
            LEFT_BLACK_DONE -> homeViewModel.leftBlackViewInfoDone()
            RIGHT_BLACK_DONE -> homeViewModel.rightBlackViewInfoDone()
            LEFT_WHITE_DONE -> homeViewModel.leftWhiteViewInfoDone()
            RIGHT_WHITE_DONE -> homeViewModel.rightWhiteViewInfoDone()
        }
    }

    private fun setPlusClickListener() {
        binding.layoutDrawerHeader.tvDrawerHeaderAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_inviteCheckFragment)
        }
    }
}
