package com.ariari.mowoori.ui.home.animator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.ariari.mowoori.R
import com.ariari.mowoori.ui.home.HomeViewModel
import kotlinx.coroutines.delay
import kotlin.random.Random

class SnowAnimator(
    private val container: ConstraintLayout,
    private val addAnimator: (Animator) -> Unit,
    private val context: Context
) {

    private fun makeSnow() = ImageView(context).apply {
        setImageResource(R.drawable.ic_snow)
        // snow 크기 설정
        scaleX = Random.nextFloat() * .3f + .2f
        scaleY = scaleX
        alpha = 0.7f
    }

    suspend fun dropSnow(delayTime: Long) {
        val snow = makeSnow()
        container.addView(snow)

        // snow 좌표 설정
        val snowStartHeight = snow.scaleY + 70f
        val startX = Random.nextFloat() * container.width
        val endX = Random.nextFloat() * container.width

        val moverX = ObjectAnimator.ofFloat(snow, View.TRANSLATION_X, startX, endX)
        val moverY = ObjectAnimator.ofFloat(
            snow,
            View.TRANSLATION_Y,
            -snowStartHeight,
            container.height + snowStartHeight
        ).apply {
            interpolator = AccelerateInterpolator(1f)
        }

        val snowAnimSet = AnimatorSet().apply {
            playTogether(moverX, moverY)
            duration = (Math.random() * 3000 + 9000).toLong()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: Animator?) {
                    super.onAnimationCancel(animation)
                }

                override fun onAnimationEnd(animation: Animator?) {
                    // Timber.d("End")
                    super.onAnimationEnd(animation)
                    container.removeView(snow)
                }
            })
        }
        addAnimator(snowAnimSet)
        snowAnimSet.start()
        delay(delayTime)
    }
}
