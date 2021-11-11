package com.ariari.mowoori.ui.home

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.Path
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.FragmentHomeBinding
import com.ariari.mowoori.ui.home.adapter.DrawerAdapter
import com.ariari.mowoori.ui.home.adapter.DrawerAdapterDecoration
import com.ariari.mowoori.ui.home.entity.ViewInfo
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
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding ?: error(getString(R.string.binding_error))

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var adapter: DrawerAdapter

    // 1단계 눈내리는 애니메이션
    private var snowJob: Job? = null

    // 2단계 눈사람 얼굴 애니메이션
    private lateinit var snowFace: ImageView
    private lateinit var faceDownAnim: Animator
    private lateinit var faceUpAnim: Animator
    private lateinit var faceHorizontalAnim: Animator

    // 3단계 눈사람 얼굴, 몸통 애니메이션
    private lateinit var faceInfo: ViewInfo
    private lateinit var bodyInfo: ViewInfo
    private lateinit var faceRightUpAnim: AnimatorSet
    private lateinit var faceRightDownAnim: AnimatorSet
    private lateinit var faceLeftUpAnim: AnimatorSet
    private lateinit var faceDownToBodyAnim: Animator
    private lateinit var faceUpFromBodyAnim: Animator
    private lateinit var faceDownShapeAnim: Animator
    private lateinit var faceUpShapeAnim: Animator
    private lateinit var faceResetShapeAnim: Animator
    private lateinit var faceDisappearAnim: Animator
    private lateinit var faceShowAnim: Animator
    private lateinit var bodyButtonTopAnim: Animator
    private lateinit var bodyButtonMiddleAnim: Animator
    private lateinit var bodyButtonBottomAnim: Animator

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // onAttach 콜백함수는 최초 한번만 실행된다.
        setUserInfo()
    }

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
        setUserInfoObserver()
        setCurrentGroupInfoObserver()
        setGroupInfoListObserver()
        setDrawerOpenListener()
        setDrawerAdapter()
        setRecyclerViewDecoration()
        setObserver()
        setAnimation()
        setClickListener()
        setMenuListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("destroy")
        homeViewModel.cancelSnowAnimList()
        _binding = null
    }

    private fun setUserInfo() {
        // TODO: 유저아이디는 로컬에서 가져오기 (현재는 더미 데이터 사용)
        homeViewModel.setUserInfo()
    }

    private fun setUserInfoObserver() {
        homeViewModel.userInfo.observe(viewLifecycleOwner, EventObserver { userInfo ->
            homeViewModel.setGroupInfoList(userInfo)
        })
    }

    private fun setCurrentGroupInfoObserver() {
        homeViewModel.currentGroupInfo.observe(viewLifecycleOwner, { groupInfo ->
            binding.tbHome.title = groupInfo.groupName
        })
    }

    private fun setGroupInfoListObserver() {
        homeViewModel.groupInfoList.observe(viewLifecycleOwner, { groupInfoList ->
            adapter.groups = groupInfoList
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
            override fun itemClick(position: Int) {
                homeViewModel.setCurrentGroupInfo(position)
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

    private fun createAnimation(animatorResId: Int, target: View): Animator {
        val anim = AnimatorInflater
            .loadAnimator(
                requireContext(),
                animatorResId
            ).apply {
                setTarget(target)
            }
        homeViewModel.addSnowAnim(anim)
        return anim
    }

    private fun setAnimation() {
        TimberUtil.timber("size", "${homeViewModel.isSnowing.value}")
        homeViewModel.updateIsSnowing()
        // 현재 임시로 2단계 눈사람 지정
        homeViewModel.updateSnowmanLevel(SnowmanLevel.SNOW_BODY)

        snowFace = binding.ivHomeSnowFace
        faceDownAnim = createAnimation(R.animator.animator_snow_down, snowFace).apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    faceUpAnim.start()
                }
            })
        }
        faceUpAnim = createAnimation(R.animator.animator_snow_up, snowFace).apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    faceDownAnim.start()
                }
            })
        }
        faceHorizontalAnim = createAnimation(R.animator.animator_snow_move_horizontal, snowFace)
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
                    snowFace.isVisible = true
                    AnimatorSet().apply {
                        playTogether(faceHorizontalAnim, faceUpAnim)
                    }.start()
                }
                SnowmanLevel.SNOW_BODY -> {
                    // 3단계 눈사람 - 얼굴 몸통 합체 애니메이션
                    startSnowmanWithBodyAnim()
                }
                SnowmanLevel.SNOW_CLOTHES -> {
                    // TODO: 4단계(최종) 눈사람 - 팔 등 추가 장식
                }
            }
        }
    }


    private fun setSnowmanInfo() {
        binding.ivHomeSnowmanFace.post {
            with(binding.ivHomeSnowmanFace) {
                faceInfo = ViewInfo(this.x, this.y, this.width.toFloat(), this.height.toFloat())
            }
            binding.ivHomeSnowmanBody.post {
                with(binding.ivHomeSnowmanBody) {
                    bodyInfo = ViewInfo(this.x, this.y, this.width.toFloat(), this.height.toFloat())
                }
                setHorizontalAnimator()
                AnimatorSet().apply {
                    playTogether(faceRightUpAnim, faceUpShapeAnim)
                }.start()
            }
        }

    }

    private fun startSnowmanWithBodyAnim() {
        setShapeAnimator()
        setSnowmanInfo()
        binding.ivHomeSnowmanFace.setImageResource(R.drawable.ic_snowman_face_3_rotate)
        homeViewModel.setIsFirstCycle(true)
    }

    private fun dpToPx(dp: Int) =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        )

    private fun setShapeAnimator() {
        faceDownShapeAnim = createAnimation(
            R.animator.animator_face_shape_down,
            binding.ivHomeSnowmanFace
        )
        faceUpShapeAnim = createAnimation(
            R.animator.animator_face_shape_up,
            binding.ivHomeSnowmanFace
        )
        faceResetShapeAnim = createAnimation(
            R.animator.animator_face_shape_reset,
            binding.ivHomeSnowmanFace
        )

        val showProperty = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
        val disappearProperty = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f)
        faceDisappearAnim = getAnimator(binding.ivHomeSnowmanFace, disappearProperty)
        faceShowAnim = getAnimator(binding.ivHomeSnowmanFace, showProperty)
        bodyButtonTopAnim = getAnimator(binding.ivHomeSnowmanButtonTop, showProperty)
        bodyButtonMiddleAnim = getAnimator(binding.ivHomeSnowmanButtonMiddle, showProperty)
        bodyButtonBottomAnim = getAnimator(binding.ivHomeSnowmanButtonBottom, showProperty)
    }

    private fun setFaceDownToBodyAnim(goRightNext: Boolean) {
        faceDownToBodyAnim.removeAllListeners()
        faceDownToBodyAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                when (goRightNext) {
                    true -> {
                        if (homeViewModel.isFirstCycle) {
                            setFaceUpFromBodyAnim()
                            homeViewModel.setIsFirstCycle(false)
                        }
                    }
                    false -> {
                        faceResetShapeAnim.start()
                        disappearFace()
                    }
                }
            }
        })
        faceDownToBodyAnim.start()
    }

    private fun setFaceUpFromBodyAnim() {
        faceUpFromBodyAnim.removeAllListeners()
        faceUpFromBodyAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                faceRightDownAnim.start()
                faceDownShapeAnim.start()
            }
        })
        faceUpFromBodyAnim.start()
    }

    private fun setHorizontalAnimator() {
        val margin = bodyInfo.x - faceInfo.x - faceInfo.width
        val left = faceInfo.x
        val top = bodyInfo.y - faceInfo.height - dpToPx(80)
        val right = faceInfo.x + bodyInfo.width + faceInfo.width + margin * 2
        val bottom =
            bodyInfo.y + (bodyInfo.height - faceInfo.height) * 2 + faceInfo.height + dpToPx(80)

        val rightUpPath = Path().apply {
            arcTo(left, top, right, bottom, 180f, 90f, true)
        }
        val rightDownPath = Path().apply {
            arcTo(left, top, right, bottom, 270f, 90f, true)
        }
        val leftUpPath = Path().apply {
            arcTo(left, top, right, bottom, 0f, -90f, true)
        }

        val rotationRightUpAnimator = getAnimator(
            binding.ivHomeSnowmanFace,
            PropertyValuesHolder.ofFloat(View.ROTATION, -180f, 0f)
        )
        val rotationRightDownAnimator = getAnimator(
            binding.ivHomeSnowmanFace,
            PropertyValuesHolder.ofFloat(View.ROTATION, 0f, 180f)
        )
        val rotationLeftUpAnimator = getAnimator(
            binding.ivHomeSnowmanFace,
            PropertyValuesHolder.ofFloat(View.ROTATION, 180f, 0f)
        )

        faceRightUpAnim = AnimatorSet().apply {
            playTogether(
                getFaceHorizontalAnimator(goRightNext = true, goUpNext = false, path = rightUpPath),
                rotationRightUpAnimator
            )
        }
        faceRightDownAnim = AnimatorSet().apply {
            playTogether(
                getFaceHorizontalAnimator(
                    goRightNext = false,
                    goUpNext = true,
                    path = rightDownPath
                ),
                rotationRightDownAnimator
            )
        }
        faceLeftUpAnim = AnimatorSet().apply {
            playTogether(
                getFaceHorizontalAnimator(goRightNext = false, goUpNext = false, path = leftUpPath),
                rotationLeftUpAnimator
            )
        }

        faceDownToBodyAnim =
            createAnimation(R.animator.animator_face_down_to_body, binding.ivHomeSnowmanFace)
        faceUpFromBodyAnim =
            createAnimation(R.animator.animator_face_up_from_body, binding.ivHomeSnowmanFace)
    }

    private fun getFaceHorizontalAnimator(goRightNext: Boolean, goUpNext: Boolean, path: Path) =
        ObjectAnimator.ofFloat(binding.ivHomeSnowmanFace, View.X, View.Y, path).apply {
            homeViewModel.addSnowAnim(this)
            if (goUpNext) {
                duration = 2000
                interpolator = BounceInterpolator()
            } else {
                duration = 1500
                interpolator = DecelerateInterpolator(1.5f)
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    when (goRightNext) {
                        true -> {
                            when (goUpNext) {
                                true -> { // Next : RightUp
                                    faceUpShapeAnim.start()
                                    faceRightUpAnim.start()
                                }
                                false -> { // Next : RightDown
                                    setFaceDownToBodyAnim(goRightNext = true)
                                }
                            }
                        }
                        false -> {
                            when (goUpNext) {
                                true -> { // Next : LeftUp
                                    faceUpShapeAnim.start()
                                    faceLeftUpAnim.start()
                                }
                                false -> { // Next : LeftDown
                                    setFaceDownToBodyAnim(goRightNext = false)
                                }
                            }
                        }
                    }
                }
            })
        }

    private fun getAnimator(imageView: ImageView, property: PropertyValuesHolder) =
        ObjectAnimator.ofPropertyValuesHolder(imageView, property).apply {
            homeViewModel.addSnowAnim(this)
            duration = 600
        }

    private fun disappearFace() {
        faceDisappearAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                binding?.ivHomeSnowmanFace.setImageResource(R.drawable.ic_snowman_face_3_done)
                showFace()
            }
        })
        faceDisappearAnim.start()
    }

    private fun showFace() {
        faceShowAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                showButtons()
            }
        })
        faceShowAnim.start()
    }

    private fun showButtons() {
        AnimatorSet().apply {
            play(bodyButtonTopAnim).before(AnimatorSet().apply {
                play(bodyButtonMiddleAnim).before(
                    bodyButtonBottomAnim
                )
            })
        }.start()
    }

}
