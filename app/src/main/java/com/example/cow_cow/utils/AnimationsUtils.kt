package com.example.cow_cow.utils

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.TranslateAnimation

object Animations {

    // Shake animation for a view
    fun shakeView(view: View) {
        val shake = ObjectAnimator.ofFloat(view, "translationX", 0f, 25f, -25f, 25f, -25f, 0f)
        shake.duration = 500 // Duration in milliseconds
        shake.interpolator = AccelerateDecelerateInterpolator()
        shake.start()
    }

    // Bounce animation for a view
    fun bounceView(view: View) {
        val bounce = TranslateAnimation(0f, 0f, 0f, -50f)
        bounce.duration = 500 // Duration in milliseconds
        bounce.repeatCount = 1
        bounce.repeatMode = Animation.REVERSE
        bounce.interpolator = BounceInterpolator()
        view.startAnimation(bounce)
    }
}
