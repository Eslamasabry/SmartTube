package com.liskovsoft.smartyoutubetv2.tv.ui.widgets.animations;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Animation utilities for mobile-friendly transitions and effects
 */
public class MobileAnimationHelper {
    private static final String TAG = MobileAnimationHelper.class.getSimpleName();
    
    // Animation durations
    public static final int ANIMATION_DURATION_SHORT = 200;
    public static final int ANIMATION_DURATION_MEDIUM = 300;
    public static final int ANIMATION_DURATION_LONG = 500;
    
    /**
     * Fade in a view with smooth animation
     */
    public static void fadeIn(View view) {
        fadeIn(view, ANIMATION_DURATION_MEDIUM, null);
    }
    
    public static void fadeIn(View view, int duration, Animator.AnimatorListener listener) {
        if (view == null) return;
        
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeIn.setDuration(duration);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        
        if (listener != null) {
            fadeIn.addListener(listener);
        }
        
        fadeIn.start();
    }
    
    /**
     * Fade out a view with smooth animation
     */
    public static void fadeOut(View view) {
        fadeOut(view, ANIMATION_DURATION_MEDIUM, null);
    }
    
    public static void fadeOut(View view, int duration, Animator.AnimatorListener listener) {
        if (view == null) return;
        
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        fadeOut.setDuration(duration);
        fadeOut.setInterpolator(new AccelerateDecelerateInterpolator());
        
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                if (listener != null) {
                    listener.onAnimationEnd(animation);
                }
            }
        });
        
        fadeOut.start();
    }
    
    /**
     * Slide in from bottom animation
     */
    public static void slideInFromBottom(View view) {
        slideInFromBottom(view, ANIMATION_DURATION_MEDIUM, null);
    }
    
    public static void slideInFromBottom(View view, int duration, Animator.AnimatorListener listener) {
        if (view == null) return;
        
        view.setTranslationY(view.getHeight());
        view.setVisibility(View.VISIBLE);
        
        ObjectAnimator slideIn = ObjectAnimator.ofFloat(view, "translationY", view.getHeight(), 0f);
        slideIn.setDuration(duration);
        slideIn.setInterpolator(new DecelerateInterpolator());
        
        if (listener != null) {
            slideIn.addListener(listener);
        }
        
        slideIn.start();
    }
    
    /**
     * Slide out to bottom animation
     */
    public static void slideOutToBottom(View view) {
        slideOutToBottom(view, ANIMATION_DURATION_MEDIUM, null);
    }
    
    public static void slideOutToBottom(View view, int duration, Animator.AnimatorListener listener) {
        if (view == null) return;
        
        ObjectAnimator slideOut = ObjectAnimator.ofFloat(view, "translationY", 0f, view.getHeight());
        slideOut.setDuration(duration);
        slideOut.setInterpolator(new AccelerateDecelerateInterpolator());
        
        slideOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                if (listener != null) {
                    listener.onAnimationEnd(animation);
                }
            }
        });
        
        slideOut.start();
    }
    
    /**
     * Slide in from right animation (for side panels)
     */
    public static void slideInFromRight(View view) {
        slideInFromRight(view, ANIMATION_DURATION_MEDIUM, null);
    }
    
    public static void slideInFromRight(View view, int duration, Animator.AnimatorListener listener) {
        if (view == null) return;
        
        view.setTranslationX(view.getWidth());
        view.setVisibility(View.VISIBLE);
        
        ObjectAnimator slideIn = ObjectAnimator.ofFloat(view, "translationX", view.getWidth(), 0f);
        slideIn.setDuration(duration);
        slideIn.setInterpolator(new DecelerateInterpolator());
        
        if (listener != null) {
            slideIn.addListener(listener);
        }
        
        slideIn.start();
    }
    
    /**
     * Slide out to right animation
     */
    public static void slideOutToRight(View view) {
        slideOutToRight(view, ANIMATION_DURATION_MEDIUM, null);
    }
    
    public static void slideOutToRight(View view, int duration, Animator.AnimatorListener listener) {
        if (view == null) return;
        
        ObjectAnimator slideOut = ObjectAnimator.ofFloat(view, "translationX", 0f, view.getWidth());
        slideOut.setDuration(duration);
        slideOut.setInterpolator(new AccelerateDecelerateInterpolator());
        
        slideOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                if (listener != null) {
                    listener.onAnimationEnd(animation);
                }
            }
        });
        
        slideOut.start();
    }
    
    /**
     * Scale up animation for button press feedback
     */
    public static void scalePress(View view) {
        if (view == null) return;
        
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f, 1f);
        
        scaleX.setDuration(ANIMATION_DURATION_SHORT);
        scaleY.setDuration(ANIMATION_DURATION_SHORT);
        
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());
        
        scaleX.start();
        scaleY.start();
    }
    
    /**
     * Rotation animation for loading indicators
     */
    public static ObjectAnimator createRotationAnimation(View view) {
        if (view == null) return null;
        
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
        rotation.setDuration(1000);
        rotation.setRepeatCount(ValueAnimator.INFINITE);
        rotation.setInterpolator(new AccelerateDecelerateInterpolator());
        
        return rotation;
    }
    
    /**
     * Cross-fade between two views
     */
    public static void crossFade(View fadeOutView, View fadeInView) {
        crossFade(fadeOutView, fadeInView, ANIMATION_DURATION_MEDIUM);
    }
    
    public static void crossFade(View fadeOutView, View fadeInView, int duration) {
        if (fadeInView != null) {
            fadeIn(fadeInView, duration, null);
        }
        
        if (fadeOutView != null) {
            fadeOut(fadeOutView, duration, null);
        }
    }
}