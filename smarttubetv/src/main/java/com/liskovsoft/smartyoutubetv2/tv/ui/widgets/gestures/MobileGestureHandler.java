package com.liskovsoft.smartyoutubetv2.tv.ui.widgets.gestures;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Gesture handler for mobile navigation including swipe gestures
 */
public class MobileGestureHandler extends GestureDetector.SimpleOnGestureListener {
    private static final String TAG = MobileGestureHandler.class.getSimpleName();
    
    // Minimum distance for swipe detection
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    
    public interface SwipeListener {
        void onSwipeLeft();
        void onSwipeRight();
        void onSwipeUp();
        void onSwipeDown();
        void onTap();
        void onDoubleTap();
    }
    
    private SwipeListener mSwipeListener;
    private GestureDetector mGestureDetector;
    
    public MobileGestureHandler(Context context, SwipeListener swipeListener) {
        mSwipeListener = swipeListener;
        mGestureDetector = new GestureDetector(context, this);
    }
    
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }
    
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (mSwipeListener != null) {
            mSwipeListener.onTap();
        }
        return true;
    }
    
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (mSwipeListener != null) {
            mSwipeListener.onDoubleTap();
        }
        return true;
    }
    
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1 == null || e2 == null) return false;
        
        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getY() - e1.getY();
        
        if (Math.abs(diffX) > Math.abs(diffY)) {
            // Horizontal swipe
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    onSwipeRight();
                } else {
                    onSwipeLeft();
                }
                return true;
            }
        } else {
            // Vertical swipe
            if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    onSwipeDown();
                } else {
                    onSwipeUp();
                }
                return true;
            }
        }
        
        return false;
    }
    
    private void onSwipeLeft() {
        if (mSwipeListener != null) {
            mSwipeListener.onSwipeLeft();
        }
    }
    
    private void onSwipeRight() {
        if (mSwipeListener != null) {
            mSwipeListener.onSwipeRight();
        }
    }
    
    private void onSwipeUp() {
        if (mSwipeListener != null) {
            mSwipeListener.onSwipeUp();
        }
    }
    
    private void onSwipeDown() {
        if (mSwipeListener != null) {
            mSwipeListener.onSwipeDown();
        }
    }
    
    /**
     * Attach this gesture handler to a view
     */
    public void attachToView(View view) {
        view.setOnTouchListener((v, event) -> onTouchEvent(event));
    }
}