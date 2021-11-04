package com.ariari.mowoori.ui.home

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.FragmentHomeBinding
import com.ariari.mowoori.ui.home.adapter.DrawerAdapter
import com.ariari.mowoori.ui.home.adapter.DrawerAdapterDecoration
import com.ariari.mowoori.util.TimberUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.random.Random


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding ?: error(getString(R.string.binding_error))

    private val viewModel: HomeViewModel by viewModels()

    private var snowJob: Job? = null

    private lateinit var snowFace: ImageView
    private lateinit var snowFaceDownAnim: AnimatorSet
    private lateinit var snowFaceUpAnim: ObjectAnimator

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
        setDrawerOpenListener()
        setDrawerAdapter()
        setRecyclerViewDecoration()
        setObserver()
        setAnimation()
        setClickListener()
    }


    private fun setDrawerOpenListener() {
        binding.tbHome.setNavigationOnClickListener {
            binding.drawerHome.open()
        }
    }

    private fun setDrawerAdapter() {
        val adapter: DrawerAdapter by lazy {
            DrawerAdapter(object : DrawerAdapter.OnItemClickListener {
                override fun itemClick(position: Int) {
                    // TODO: 그룹 이동
                    // TODO: 그룹 아이템 배경 색상 변경
                    binding.drawerHome.close()
                }
            })
        }
        binding.rvDrawer.adapter = adapter
    }

    private fun setRecyclerViewDecoration() {
        val itemDecoration = DrawerAdapterDecoration()
        binding.rvDrawer.addItemDecoration(itemDecoration)
    }

    private fun setObserver() {
        viewModel.isSnowing.observe(viewLifecycleOwner, {
            updateSnowAnimation(it)
        })
        viewModel.snowmanLevel.observe(viewLifecycleOwner, {
            updateSnowmanAnimation(it)
        })
    }

    private fun setAnimation() {
        viewModel.updateIsSnowing()
        // 현재 임시로 1단계 눈사람 지정
        viewModel.updateSnowmanLevel(SnowmanLevel.SNOW_FACE)

        snowFace = binding.ivHomeSnowFace
        snowFaceDownAnim = AnimatorInflater
            .loadAnimator(
                requireContext(),
                R.animator.animator_snow_down
            ).apply {
                setTarget(snowFace)
                Timber.d("2")
            } as AnimatorSet
        snowFaceUpAnim = AnimatorInflater
            .loadAnimator(requireContext(), R.animator.animator_snow_up)
            .apply {
                setTarget(snowFace)
                Timber.d("1")
            } as ObjectAnimator
    }

    private fun setClickListener() {
        binding.containerHome.setOnClickListener {
            viewModel.updateIsSnowing()
        }
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

    private fun updateSnowmanAnimation(snowmanLevel: SnowmanLevel) {
        viewLifecycleOwner.lifecycleScope.launch {
            when (snowmanLevel) {
                SnowmanLevel.SNOW_NO -> {
                    // TODO: 눈사람이 녹아버리는 애니메이션 추가
                }
                SnowmanLevel.SNOW_FACE -> {
                    snowFace.isVisible = true
                    snowFaceUpAnim.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            snowFaceDownAnim.start()
                        }
                    })
                    snowFaceDownAnim.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            snowFaceUpAnim.start()
                        }
                    })
                    snowFaceUpAnim.start()
                }
                SnowmanLevel.SNOW_BODY -> {
                    // TODO: 눈사람 2단계 - 몸통까지 생겼을 때
                }
                SnowmanLevel.SNOW_CLOTHES -> {
                    // TODO: 눈사람 3단계(최종) - 팔 등 추가 장식
                }
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

        val set = AnimatorSet().apply {
            playTogether(moverX, moverY)
            duration = (Math.random() * 3000 + 3000).toLong()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: Animator?) {
                    super.onAnimationCancel(animation)
                }

                override fun onAnimationEnd(animation: Animator?) {
                    Timber.d("End")
                    super.onAnimationEnd(animation)
                    binding.containerHome.removeView(snow)
                }
            })
        }
        viewModel.addSnowAnimSet(set)

        set.start()
        delay(delayTime)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.cancelSnowAnimSets()
        snowFaceDownAnim.cancel()
        snowFaceUpAnim.cancel()
        _binding = null
    }
}
