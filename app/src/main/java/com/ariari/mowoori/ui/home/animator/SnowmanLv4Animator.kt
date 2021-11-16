package com.ariari.mowoori.ui.home.animator

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.ariari.mowoori.ui.home.HomeViewModel
import com.ariari.mowoori.ui.home.entity.Lv4Component
import com.ariari.mowoori.ui.home.entity.ViewInfo

class SnowmanLv4Animator(
    private val component: Lv4Component,
    private val homeViewModel: HomeViewModel,
    private val lifecycleOwner: LifecycleOwner,
) {
    private lateinit var leftBlackEyeInfo: ViewInfo
    private lateinit var rightBlackEyeInfo: ViewInfo
    private lateinit var leftWhiteEyeInfo: ViewInfo
    private lateinit var rightWhiteEyeInfo: ViewInfo
    private lateinit var moveLeftEyeToLeftAnimator: ObjectAnimator
    private lateinit var moveRightEyeToLeftAnimator: ObjectAnimator
    private lateinit var moveLeftEyeToRightAnimator: ObjectAnimator
    private lateinit var moveRightEyeToRightAnimator: ObjectAnimator

    fun start() {
        homeViewModel.addSources()
        initViewInfo()
        setObservers()
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
        AnimatorSet().apply {
            playTogether(moveLeftEyeToLeftAnimator, moveRightEyeToLeftAnimator)
            start()
        }
    }

    private fun setMoveLeftEyeToLeftAnimator() {
        val moveLeftEyePropertyX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X,
            leftBlackEyeInfo.x - leftWhiteEyeInfo.x)
        val moveLeftEyePropertyY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y,
            leftBlackEyeInfo.height - leftWhiteEyeInfo.height - (leftWhiteEyeInfo.y - leftBlackEyeInfo.y))
        moveLeftEyeToLeftAnimator = ObjectAnimator.ofPropertyValuesHolder(component.leftWhiteEye,
            moveLeftEyePropertyX,
            moveLeftEyePropertyY).apply {
            duration = 1000
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
            duration = 1000
        }
    }
}
