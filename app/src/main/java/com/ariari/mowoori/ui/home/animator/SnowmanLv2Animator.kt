package com.ariari.mowoori.ui.home.animator

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.content.Context
import android.view.View
import android.widget.ImageView
import com.ariari.mowoori.R
import com.ariari.mowoori.ui.home.HomeViewModel

class SnowmanLv2Animator(
    private var face: ImageView,
    private val homeViewModel: HomeViewModel,
    private val context: Context
) {
    // 2단계 눈사람 얼굴 애니메이션
    private lateinit var faceDownAnim: Animator
    private lateinit var faceUpAnim: Animator
    private lateinit var faceHorizontalAnim: Animator

    fun start() {
        initAnimator()
        startAnimation()
    }

    private fun getAnimatorFromResource(animatorResId: Int, view: View?): Animator =
        AnimatorInflater
            .loadAnimator(
                context,
                animatorResId
            ).apply {
                setTarget(view)
                homeViewModel.addAnimator(this)
            }

    private fun initAnimator() {
        faceDownAnim = getAnimatorFromResource(R.animator.animator_snow_down, face).apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    faceUpAnim.start()
                }
            })
        }

        faceUpAnim = getAnimatorFromResource(R.animator.animator_snow_up, face).apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    faceDownAnim.start()
                }
            })
        }

        faceHorizontalAnim = getAnimatorFromResource(R.animator.animator_snow_move_horizontal, face)
    }

    private fun startAnimation() {
        AnimatorSet().apply {
            playTogether(faceHorizontalAnim, faceUpAnim)
        }.start()
    }
}
