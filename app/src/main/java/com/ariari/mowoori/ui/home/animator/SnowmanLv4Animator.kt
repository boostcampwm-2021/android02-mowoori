package com.ariari.mowoori.ui.home.animator

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.util.Property
import android.view.View
import androidx.core.view.isInvisible
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
    private lateinit var showUpFirstExclamationAnimator: Animator
    private lateinit var showUpSecondExclamationAnimator: Animator
    private lateinit var showUpLeftHeartAnimator: Animator
    private lateinit var showUpRightHeartAnimator: Animator
    private lateinit var showBigHeartAnimator: Animator
    private lateinit var showLeftEyeHeartAnimator: Animator
    private lateinit var showRightEyeHeartAnimator: Animator
    private lateinit var disappearFirstExclamationAnimator: Animator
    private lateinit var disappearSecondExclamationAnimator: Animator

    fun start() {
        homeViewModel.addSources()
        setXMLAnimators()
        initViewInfo()
        setObservers()
    }

    private fun setXMLAnimators() {
        showLeftHandAnimator =
            getAnimatorFromResource(R.animator.animator_hand_show, component.hands[0]).apply {
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        moveEyeToRightAnimatorSet.start()
                        showLeftHandAnimator.removeAllListeners()
                    }
                })
            }
        showRightHandAnimator =
            getAnimatorFromResource(R.animator.animator_hand_show, component.hands[1]).apply {
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        setEyesInvisible()
                        showUpFirstExclamationAnimator.cancel()
                        showUpSecondExclamationAnimator.cancel()
                        disappearFirstExclamationAnimator.start()
                        disappearSecondExclamationAnimator.start()
                        showBigHeartAnimator.start()
                        showUpLeftHeartAnimator.start()
                        showUpRightHeartAnimator.start()
                        showLeftEyeHeartAnimator.start()
                        showRightEyeHeartAnimator.start()
                        showRightHandAnimator.removeAllListeners()
                    }
                })
            }
        showUpFirstExclamationAnimator =
            getAnimatorFromResource(R.animator.animator_show_up, component.exclamations[0]).apply {
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        showUpFirstExclamationAnimator.removeAllListeners()
                    }
                })
            }
        showUpSecondExclamationAnimator = getAnimatorFromResource(R.animator.animator_show_up,
            component.exclamations[1]).apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    showUpFirstExclamationAnimator.removeAllListeners()
                }
            })
        }

        showUpLeftHeartAnimator = getAnimatorFromResource(R.animator.animator_show_up,
            component.hearts[0]).apply {
            startDelay = 180L
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    showUpLeftHeartAnimator.removeAllListeners()
                }
            })
        }
        showUpRightHeartAnimator = getAnimatorFromResource(R.animator.animator_show_up,
            component.hearts[1]).apply {
            startDelay = 180L
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    showUpRightHeartAnimator.removeAllListeners()
                }
            })
        }
        showBigHeartAnimator = getAnimatorFromResource(R.animator.animator_show_big_heart,
            component.face.ivHomeBigHeart).apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    showBigHeartAnimator.removeAllListeners()
                }
            })
        }

        showLeftEyeHeartAnimator = getAnimatorFromResource(R.animator.animator_show_up,
            component.face.ivHomeSnowmanLeftEyeHeartLv4).apply {
            startDelay = 180L
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    showLeftEyeHeartAnimator.removeAllListeners()
                }
            })
        }

        showRightEyeHeartAnimator = getAnimatorFromResource(R.animator.animator_show_up,
            component.face.ivHomeSnowmanRightEyeHeartLv4).apply {
            startDelay = 180L
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    showRightEyeHeartAnimator.removeAllListeners()
                }
            })
        }

        disappearFirstExclamationAnimator = getAnimatorFromResource(R.animator.animator_disappear,
            component.exclamations[0]).apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    disappearFirstExclamationAnimator.removeAllListeners()
                }
            })
        }

        disappearSecondExclamationAnimator = getAnimatorFromResource(R.animator.animator_disappear,
            component.exclamations[1]).apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    disappearSecondExclamationAnimator.removeAllListeners()
                }
            })
        }
    }

    private fun setEyesInvisible() {
        component.face.ivHomeSnowmanLeftEyeBlackLv4.isInvisible = true
        component.face.ivHomeSnowmanRightEyeBlackLv4.isInvisible = true
        component.face.ivHomeSnowmanLeftEyeWhiteLv4.isInvisible = true
        component.face.ivHomeSnowmanRightEyeWhiteLv4.isInvisible = true
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
            with(component.face.ivHomeSnowmanLeftEyeBlackLv4) {
                post {
                    leftBlackEyeInfo =
                        ViewInfo(this.x, this.y, this.width.toFloat(), this.height.toFloat())
                    homeViewModel.leftBlackViewInfoDone()
                }
            }
            with(component.face.ivHomeSnowmanLeftEyeWhiteLv4) {
                post {
                    leftWhiteEyeInfo =
                        ViewInfo(this.x, this.y, this.width.toFloat(), this.height.toFloat())
                    homeViewModel.leftWhiteViewInfoDone()
                }
            }
            with(component.face.ivHomeSnowmanRightEyeBlackLv4) {
                post {
                    rightBlackEyeInfo =
                        ViewInfo(this.x, this.y, this.width.toFloat(), this.height.toFloat())
                    homeViewModel.rightBlackViewInfoDone()
                }
            }
            with(component.face.ivHomeSnowmanRightEyeWhiteLv4) {
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
        moveLeftEyeToRightAnimator = getAnimatorOfFloat(component.face.ivHomeSnowmanLeftEyeWhiteLv4,
            View.TRANSLATION_X,
            leftWhiteEyeInfo.x - leftBlackEyeInfo.x - leftWhiteEyeInfo.width,
            600)
        moveRightEyeToRightAnimator =
            getAnimatorOfFloat(component.face.ivHomeSnowmanRightEyeWhiteLv4,
                View.TRANSLATION_X,
                rightWhiteEyeInfo.x - rightBlackEyeInfo.x - rightWhiteEyeInfo.width,
                600)
        setAnimationSet()
        startAnimation()
    }

    private fun setMoveLeftEyeToLeftAnimator() {
        val moveLeftEyePropertyX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X,
            leftBlackEyeInfo.x - leftWhiteEyeInfo.x)
        val moveLeftEyePropertyY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y,
            leftBlackEyeInfo.height - leftWhiteEyeInfo.height - (leftWhiteEyeInfo.y - leftBlackEyeInfo.y))
        moveLeftEyeToLeftAnimator =
            getAnimatorFromProperties(component.face.ivHomeSnowmanLeftEyeWhiteLv4,
                moveLeftEyePropertyX,
                moveLeftEyePropertyY,
                600)
    }

    private fun setMoveRightEyeToLeftAnimator() {
        val moveRightEyePropertyX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X,
            rightBlackEyeInfo.x - rightWhiteEyeInfo.x)
        val moveRightEyePropertyY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y,
            rightBlackEyeInfo.height - rightWhiteEyeInfo.height - (rightWhiteEyeInfo.y - rightBlackEyeInfo.y))
        moveRightEyeToLeftAnimator =
            getAnimatorFromProperties(component.face.ivHomeSnowmanRightEyeWhiteLv4,
                moveRightEyePropertyX,
                moveRightEyePropertyY,
                600)
    }

    private fun getAnimatorFromProperties(
        view: View,
        property1: PropertyValuesHolder,
        property2: PropertyValuesHolder,
        duration: Long,
    ) =
        ObjectAnimator.ofPropertyValuesHolder(view, property1, property2).apply {
            this.duration = duration
            homeViewModel.addAnimator(this)
        }

    private fun getAnimatorOfFloat(
        view: View,
        property: Property<View, Float>,
        value: Float,
        duration: Long,
    ) =
        ObjectAnimator.ofFloat(view, property, value).apply {
            this.duration = duration
            homeViewModel.addAnimator(this)
        }

    private fun setAnimationSet() {
        moveEyeToLeftAnimatorSet = AnimatorSet().apply {
            startDelay = 800
            playTogether(moveLeftEyeToLeftAnimator, moveRightEyeToLeftAnimator)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    showLeftHandAnimator.start()
                    showUpFirstExclamationAnimator.start()
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
                    showUpSecondExclamationAnimator.start()
                    moveEyeToRightAnimatorSet.removeAllListeners()
                }
            })
        }
    }

    private fun startAnimation() {
        moveEyeToLeftAnimatorSet.start()
    }
}
