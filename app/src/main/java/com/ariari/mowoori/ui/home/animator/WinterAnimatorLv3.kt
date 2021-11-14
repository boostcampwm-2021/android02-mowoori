package com.ariari.mowoori.ui.home.animator

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.Path
import android.util.TypedValue
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import com.ariari.mowoori.R
import com.ariari.mowoori.ui.home.HomeViewModel
import com.ariari.mowoori.ui.home.entity.ViewInfo

class WinterAnimatorLv3(
    private var face: ImageView,
    private var body: ImageView,
    private val homeViewModel: HomeViewModel,
    private val context: Context
) {
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

    var isFirstCycle = true

    fun start() {
        initShapeAnimator()
        initSnowInfo()
        isFirstCycle = true
    }

    private fun dpToPx(dp: Int) =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        )

    private fun getAnimatorFromResource(animatorResId: Int, view: View): Animator =AnimatorInflater
            .loadAnimator(
                context,
                animatorResId
            ).apply {
                setTarget(view)
            homeViewModel.addSnowAnim(this)
            }

    private fun getAnimatorFromProperty(view: View, property: PropertyValuesHolder, duration: Long) =
        ObjectAnimator.ofPropertyValuesHolder(view, property).apply {
            this.duration = duration
            homeViewModel.addSnowAnim(this)
        }

    private fun getFaceHorizontalAnimator(goRightNext: Boolean, goUpNext: Boolean, path: Path) =
        ObjectAnimator.ofFloat(face, View.X, View.Y, path).apply {
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
                                    updateFaceDownToBodyAnim(goRightNext = true)
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
                                    updateFaceDownToBodyAnim(goRightNext = false)
                                }
                            }
                        }
                    }
                }
            })
        }

    private fun initSnowInfo() {
        face.post {
            faceInfo = ViewInfo(face.x, face.y, face.width.toFloat(), face.height.toFloat())
            body.post {
                bodyInfo = ViewInfo(body.x, body.y, body.width.toFloat(), body.height.toFloat())
                initHorizontalAnimator()
                startFirstAnimation()
            }
        }
    }

    private fun startFirstAnimation() {
        AnimatorSet().apply {
            playTogether(faceRightUpAnim, faceUpShapeAnim)
        }.start()
    }

    private fun initShapeAnimator() {
        faceDownShapeAnim =getAnimatorFromResource(R.animator.animator_face_shape_down, face)
        faceUpShapeAnim = getAnimatorFromResource(R.animator.animator_face_shape_up, face)
        faceResetShapeAnim =getAnimatorFromResource(R.animator.animator_face_shape_reset, face)

        val showProperty = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
        val disappearProperty = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f)
        faceDisappearAnim = getAnimatorFromProperty(face, disappearProperty, 600)
        faceShowAnim = getAnimatorFromProperty(face, showProperty, 600)
        bodyButtonTopAnim = getAnimatorFromProperty(face, showProperty, 600)
        bodyButtonMiddleAnim = getAnimatorFromProperty(face, showProperty, 600)
        bodyButtonBottomAnim = getAnimatorFromProperty(face, showProperty, 600)
    }

    private fun initHorizontalAnimator() {
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

        val rotationRightUpAnimator = getAnimatorFromProperty(
            face, PropertyValuesHolder.ofFloat(View.ROTATION, -180f, 0f), 600
        )
        val rotationRightDownAnimator = getAnimatorFromProperty(
            face, PropertyValuesHolder.ofFloat(View.ROTATION, 0f, 180f), 600
        )
        val rotationLeftUpAnimator = getAnimatorFromProperty(
            face, PropertyValuesHolder.ofFloat(View.ROTATION, 180f, 0f), 600
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

        faceDownToBodyAnim = getAnimatorFromResource(R.animator.animator_face_down_to_body, face)
        faceUpFromBodyAnim = getAnimatorFromResource(R.animator.animator_face_up_from_body, face)
    }

    private fun updateFaceDownToBodyAnim(goRightNext: Boolean) {
        faceDownToBodyAnim.removeAllListeners()
        faceDownToBodyAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                when (goRightNext) {
                    true -> {
                        if (isFirstCycle) {
                            updateFaceUpFromBodyAnim()
                            isFirstCycle = false
                        }
                    }
                    false -> {
                        faceResetShapeAnim.start()
                        updateDisappearFace()
                    }
                }
            }
        })
        faceDownToBodyAnim.start()
    }

    private fun updateFaceUpFromBodyAnim() {
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

    private fun updateDisappearFace() {
        faceDisappearAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                face.setImageResource(R.drawable.ic_snowman_face_3_done)
                updateShowFace()
            }
        })
        faceDisappearAnim.start()
    }

    private fun updateShowFace() {
        faceShowAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                updateShowButtons()
            }
        })
        faceShowAnim.start()
    }

    private fun updateShowButtons() {
        AnimatorSet().apply {
            play(bodyButtonTopAnim).before(AnimatorSet().apply {
                play(bodyButtonMiddleAnim).before(
                    bodyButtonBottomAnim
                )
            })
        }.start()
    }
}
