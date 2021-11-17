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
import android.view.ViewTreeObserver
import androidx.core.view.isInvisible
import com.ariari.mowoori.R
import com.ariari.mowoori.ui.home.HomeViewModel
import com.ariari.mowoori.ui.home.entity.Lv4Component
import com.ariari.mowoori.ui.home.entity.ViewInfo

class SnowmanLv4Animator(
    private val component: Lv4Component,
    private val homeViewModel: HomeViewModel,
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

    init {
        homeViewModel.addSources()
    }

    fun start() {
        setXMLAnimators()
        initViewInfo()
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
        component.body.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                component.body.viewTreeObserver.removeOnPreDrawListener(this)
                homeViewModel.bodyMeasured()
                return true
            }
        })
    }

    fun setViewInfo() {
        component.face.ivHomeSnowmanLeftEyeBlackLv4.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                component.face.ivHomeSnowmanLeftEyeBlackLv4.viewTreeObserver.removeOnPreDrawListener(
                    this)
                leftBlackEyeInfo =
                    ViewInfo(component.face.ivHomeSnowmanLeftEyeBlackLv4.x,
                        component.face.ivHomeSnowmanLeftEyeBlackLv4.y,
                        component.face.ivHomeSnowmanLeftEyeBlackLv4.width.toFloat(),
                        component.face.ivHomeSnowmanLeftEyeBlackLv4.height.toFloat())
                homeViewModel.leftBlackViewInfoDone()
                return true
            }
        })

        component.face.ivHomeSnowmanLeftEyeWhiteLv4.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                component.face.ivHomeSnowmanLeftEyeWhiteLv4.viewTreeObserver.removeOnPreDrawListener(
                    this)
                leftWhiteEyeInfo =
                    ViewInfo(component.face.ivHomeSnowmanLeftEyeWhiteLv4.x,
                        component.face.ivHomeSnowmanLeftEyeWhiteLv4.y,
                        component.face.ivHomeSnowmanLeftEyeWhiteLv4.width.toFloat(),
                        component.face.ivHomeSnowmanLeftEyeWhiteLv4.height.toFloat())
                homeViewModel.leftWhiteViewInfoDone()
                return true
            }
        })

        component.face.ivHomeSnowmanRightEyeBlackLv4.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                component.face.ivHomeSnowmanRightEyeBlackLv4.viewTreeObserver.removeOnPreDrawListener(
                    this)
                rightBlackEyeInfo =
                    ViewInfo(component.face.ivHomeSnowmanRightEyeBlackLv4.x,
                        component.face.ivHomeSnowmanRightEyeBlackLv4.y,
                        component.face.ivHomeSnowmanRightEyeBlackLv4.width.toFloat(),
                        component.face.ivHomeSnowmanRightEyeBlackLv4.height.toFloat())
                homeViewModel.rightBlackViewInfoDone()
                return true
            }
        })

        component.face.ivHomeSnowmanRightEyeWhiteLv4.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                component.face.ivHomeSnowmanRightEyeWhiteLv4.viewTreeObserver.removeOnPreDrawListener(
                    this)
                rightWhiteEyeInfo =
                    ViewInfo(component.face.ivHomeSnowmanRightEyeWhiteLv4.x,
                        component.face.ivHomeSnowmanRightEyeWhiteLv4.y,
                        component.face.ivHomeSnowmanRightEyeWhiteLv4.width.toFloat(),
                        component.face.ivHomeSnowmanRightEyeWhiteLv4.height.toFloat())
                homeViewModel.rightWhiteViewInfoDone()
                return true
            }
        })
    }

    fun setObjectAnimators() {
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
