package com.ariari.mowoori.util

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.view.View

fun Context.getAnimatorFromResource(animatorResId: Int, view: View): Animator =
    AnimatorInflater.loadAnimator(
        this,
        animatorResId
    ).apply {
        setTarget(view)
    }

fun getAnimatorFromProperty(view: View, property: PropertyValuesHolder, duration: Long) =
    ObjectAnimator.ofPropertyValuesHolder(view, property).apply {
        this.duration = duration
    }
