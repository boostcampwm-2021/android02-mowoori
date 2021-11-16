package com.ariari.mowoori.ui.home.animator

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.ariari.mowoori.R
import com.ariari.mowoori.ui.home.HomeViewModel
import com.ariari.mowoori.ui.home.entity.Lv4Component
import com.ariari.mowoori.ui.home.entity.ViewInfo

class SnowmanLv4Animator(
    private val component: Lv4Component,
    private val homeViewModel: HomeViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val context: Context,
) {
    private lateinit var leftBlackEyeInfo: ViewInfo
    private lateinit var rightBlackEyeInfo: ViewInfo
    private lateinit var leftWhiteEyeInfo: ViewInfo
    private lateinit var rightWhiteEyeInfo: ViewInfo
    private lateinit var moveLeftEyeToLeftAnimator: ObjectAnimator
    private lateinit var moveRightEyeToLeftAnimator: ObjectAnimator
    private lateinit var moveLeftEyeToRightAnimator: ObjectAnimator
    private lateinit var moveRightEyeToRightAnimator: ObjectAnimator
    private lateinit var moveEyeToLeftAnimatorSet: AnimatorSet
    private lateinit var moveEyeToRightAnimatorSet: AnimatorSet
    private lateinit var showLeftHandAnimator: Animator
    private lateinit var showRightHandAnimator: Animator

    fun start() {
        homeViewModel.addSources()
        setXMLAnimators()
        initViewInfo()
        setObservers()
    }

    private fun setAnimationSet() {
        moveEyeToLeftAnimatorSet = AnimatorSet().apply {
            playTogether(moveLeftEyeToLeftAnimator, moveRightEyeToLeftAnimator)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    showLeftHandAnimator.start()
                    moveLeftEyeToLeftAnimator.removeAllListeners()
                }
            })
        }
        moveEyeToRightAnimatorSet = AnimatorSet().apply {
            playTogether(moveLeftEyeToRightAnimator, moveRightEyeToRightAnimator)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    showRightHandAnimator.start()
                    moveEyeToRightAnimatorSet.removeAllListeners()
                }
            })
        }
    }

    private fun setXMLAnimators() {
        showLeftHandAnimator =
            getAnimatorFromResource(R.animator.animator_hand_show, component.leftHand).apply {
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        moveEyeToRightAnimatorSet.start()
                        showLeftHandAnimator.removeAllListeners()
                    }
                })
            }
        showRightHandAnimator =
            getAnimatorFromResource(R.animator.animator_hand_show, component.rightHand).apply {
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        showRightHandAnimator.removeAllListeners()
                    }
                })
            }
    }

    private fun getAnimatorFromResource(animatorResId: Int, view: View): Animator = AnimatorInflater
        .loadAnimator(
            context,
            animatorResId
        ).apply {
            setTarget(view)
            homeViewModel.addAnimator(this)
        }

    private fun initViewInfo() {
        with(component.body) {
            post { homeViewModel.bodyMeasured() }
        }
    }

    private fun setObservers() {
        setIsBodyMeasuredObserver()
        setViewInfoMediatorObserver()
    }

    private fun setIsBodyMeasuredObserver() {
        homeViewModel.isBodyMeasured.observe(lifecycleOwner, {
            with(component.leftBlackEye) {
                post {
                    leftBlackEyeInfo =
                        ViewInfo(this.x, this.y, this.width.toFloat(), this.height.toFloat())
                    homeViewModel.leftBlackViewInfoDone()
                }
            }
            with(component.leftWhiteEye) {
                post {
                    leftWhiteEyeInfo =
                        ViewInfo(this.x, this.y, this.width.toFloat(), this.height.toFloat())
                    homeViewModel.leftWhiteViewInfoDone()
                }
            }
            with(component.rightBlackEye) {
                post {
                    rightBlackEyeInfo =
                        ViewInfo(this.x, this.y, this.width.toFloat(), this.height.toFloat())
                    homeViewModel.rightBlackViewInfoDone()
                }
            }
            with(component.rightWhiteEye) {
                post {
                    rightWhiteEyeInfo =
                        ViewInfo(this.x, this.y, this.width.toFloat(), this.height.toFloat())
                    homeViewModel.rightWhiteViewInfoDone()
                }
            }
        })
    }

    private fun setViewInfoMediatorObserver() {
        homeViewModel.viewInfoMediator.observe(lifecycleOwner, {
            if (it) {
                setObjectAnimators()
            }
        })
    }

    private fun setObjectAnimators() {
        setMoveLeftEyeToLeftAnimator()
        setMoveRightEyeToLeftAnimator()
        setMoveLeftEyeToRightAnimator()
        setMoveRightEyeToRightAnimator()
        setAnimationSet()
        startAnimation()
    }

    private fun startAnimation() {
        moveEyeToLeftAnimatorSet.start()
    }

    private fun setMoveLeftEyeToLeftAnimator() {
        val moveLeftEyePropertyX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X,
            leftBlackEyeInfo.x - leftWhiteEyeInfo.x)
        val moveLeftEyePropertyY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y,
            leftBlackEyeInfo.height - leftWhiteEyeInfo.height - (leftWhiteEyeInfo.y - leftBlackEyeInfo.y))
        moveLeftEyeToLeftAnimator = ObjectAnimator.ofPropertyValuesHolder(component.leftWhiteEye,
            moveLeftEyePropertyX,
            moveLeftEyePropertyY).apply {
            duration = 600
        }
    }

    private fun setMoveRightEyeToLeftAnimator() {
        val moveRightEyePropertyX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X,
            rightBlackEyeInfo.x - rightWhiteEyeInfo.x)
        val moveRightEyePropertyY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y,
            rightBlackEyeInfo.height - rightWhiteEyeInfo.height - (rightWhiteEyeInfo.y - rightBlackEyeInfo.y))
        moveRightEyeToLeftAnimator = ObjectAnimator.ofPropertyValuesHolder(component.rightWhiteEye,
            moveRightEyePropertyX,
            moveRightEyePropertyY).apply {
            duration = 600
        }
    }

    private fun setMoveLeftEyeToRightAnimator() {
        moveLeftEyeToRightAnimator = ObjectAnimator.ofFloat(component.leftWhiteEye,
            View.TRANSLATION_X,
            leftWhiteEyeInfo.x - leftBlackEyeInfo.x - leftWhiteEyeInfo.width)
            .apply {
                duration = 600
            }
    }

    private fun setMoveRightEyeToRightAnimator() {
        moveRightEyeToRightAnimator = ObjectAnimator.ofFloat(component.rightWhiteEye,
            View.TRANSLATION_X,
            rightWhiteEyeInfo.x - rightBlackEyeInfo.x - rightWhiteEyeInfo.width)
            .apply {
                duration = 600
            }
    }
}
