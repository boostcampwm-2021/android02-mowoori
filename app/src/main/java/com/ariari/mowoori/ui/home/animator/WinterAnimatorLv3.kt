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
import com.ariari.mowoori.util.TimberUtil
import com.ariari.mowoori.util.getAnimatorFromProperty
import com.ariari.mowoori.util.getAnimatorFromResource

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

    private var _isFirstCycle = true
    val isFirstCycle = _isFirstCycle

    fun setIsFirstCycle(isFirst: Boolean) {
        _isFirstCycle = isFirst
    }

    private fun dpToPx(dp: Int) =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        )

    private fun getAnimator(animatorResId: Int, target: View): Animator {
        val anim = AnimatorInflater
            .loadAnimator(
                context,
                animatorResId
            ).apply {
                setTarget(target)
            }
        homeViewModel.addSnowAnim(anim)
        return anim
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

    fun setSnowmanInfo() {
        face.post {
            faceInfo = ViewInfo(face.x, face.y, face.width.toFloat(), face.height.toFloat())
            body.post {
                bodyInfo = ViewInfo(body.x, body.y, body.width.toFloat(), body.height.toFloat())
                setHorizontalAnimator()
                AnimatorSet().apply {
                    playTogether(faceRightUpAnim, faceUpShapeAnim)
                }.start()
            }
        }
    }

    fun setShapeAnimator() {
        faceDownShapeAnim =
            context.getAnimatorFromResource(R.animator.animator_face_shape_down, face)
        faceUpShapeAnim = context.getAnimatorFromResource(R.animator.animator_face_shape_up, face)
        faceResetShapeAnim =
            context.getAnimatorFromResource(R.animator.animator_face_shape_reset, face)

        val showProperty = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
        val disappearProperty = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f)
        faceDisappearAnim = getAnimatorFromProperty(face, disappearProperty, 600)
        faceShowAnim = getAnimatorFromProperty(face, showProperty, 600)
        bodyButtonTopAnim = getAnimatorFromProperty(face, showProperty, 600)
        bodyButtonMiddleAnim = getAnimatorFromProperty(face, showProperty, 600)
        bodyButtonBottomAnim = getAnimatorFromProperty(face, showProperty, 600)

        homeViewModel.addSnowAnim(
            listOf(
                faceDownShapeAnim,
                faceUpShapeAnim,
                faceResetShapeAnim,
                faceDisappearAnim,
                faceShowAnim,
                bodyButtonTopAnim,
                bodyButtonMiddleAnim,
                bodyButtonBottomAnim
            )
        )
    }

    private fun setFaceDownToBodyAnim(goRightNext: Boolean) {
        faceDownToBodyAnim.removeAllListeners()
        faceDownToBodyAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                when (goRightNext) {
                    true -> {
                        if (isFirstCycle) {
                            setFaceUpFromBodyAnim()
                            _isFirstCycle = false
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

        faceDownToBodyAnim =
            getAnimator(R.animator.animator_face_down_to_body, face)
        faceUpFromBodyAnim =
            getAnimator(R.animator.animator_face_up_from_body, face)
    }

    private fun disappearFace() {
        faceDisappearAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                TimberUtil.timber("anim", "destroy")
                face?.setImageResource(R.drawable.ic_snowman_face_3_done)
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
