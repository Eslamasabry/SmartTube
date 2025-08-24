package com.liskovsoft.smartyoutubetv2.tv.ui.widgets.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import androidx.annotation.NonNull;
import androidx.leanback.widget.VerticalGridView;
import androidx.recyclerview.widget.RecyclerView;
import com.liskovsoft.sharedutils.mylogger.Log;

/**
 * Touch-enabled VerticalGridView for mobile-friendly interface<br/>
 * Supports touch scrolling, gestures, and orientation changes<br/>
 * Used in: lb_rows_fragment.xml, lb_vertical_grid.xml
 */
public class TouchVerticalGridView extends VerticalGridView {
    private static final String TAG = TouchVerticalGridView.class.getSimpleName();
    private GestureDetector mGestureDetector;

    public TouchVerticalGridView(Context context) {
        super(context);
        init();
    }

    public TouchVerticalGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TouchVerticalGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // Enable touch scrolling
        setNestedScrollingEnabled(true);
        
        // Initialize gesture detector for better touch handling
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // Handle fling gestures for smooth scrolling
                if (Math.abs(velocityY) > Math.abs(velocityX)) {
                    // Vertical fling
                    fling(0, (int) velocityY);
                    return true;
                }
                return false;
            }
            
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                // Handle touch scroll
                if (canScrollVertically((int) distanceY)) {
                    scrollBy(0, (int) distanceY);
                    return true;
                }
                return false;
            }
        });

        addOnScrollListener(new OnScrollListener() {
            private int mLastState;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mLastState = newState;
                
                // Log scroll state changes for debugging
                if (Log.isDebugMode()) {
                    Log.d(TAG, "Scroll state changed: " + newState);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                // Handle scroll events
                if (dy != 0 && Log.isDebugMode()) {
                    Log.d(TAG, "Scrolled dy: " + dy + ", state: " + mLastState);
                }
            }
        });
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Let gesture detector handle the touch event first
        if (mGestureDetector != null && mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        
        // Fall back to default touch handling
        return super.onTouchEvent(event);
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Allow touch interception for gesture handling
        if (mGestureDetector != null && mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        
        return super.onInterceptTouchEvent(event);
    }
}
