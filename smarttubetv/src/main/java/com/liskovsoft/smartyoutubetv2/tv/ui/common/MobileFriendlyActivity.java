package com.liskovsoft.smartyoutubetv2.tv.ui.common;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import com.liskovsoft.smartyoutubetv2.tv.R;
import com.liskovsoft.smartyoutubetv2.tv.ui.widgets.utils.MobileUtils;
import com.liskovsoft.sharedutils.mylogger.Log;

/**
 * Base activity that provides mobile-friendly features including:
 * - Proper orientation change handling
 * - Mobile layout detection and optimization
 * - Touch-friendly UI adjustments
 * - Transition animations between orientations
 */
public abstract class MobileFriendlyActivity extends LeanbackActivity {
    private static final String TAG = MobileFriendlyActivity.class.getSimpleName();
    
    private boolean mIsMobileDevice;
    private boolean mIsTouchDevice;
    private int mLastOrientation = Configuration.ORIENTATION_UNDEFINED;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Detect device capabilities
        mIsMobileDevice = MobileUtils.isMobileDevice(this);
        mIsTouchDevice = MobileUtils.isTouchDevice(this);
        
        if (mIsMobileDevice) {
            setupMobileOptimizations();
        }
        
        mLastOrientation = getResources().getConfiguration().orientation;
        
        Log.d(TAG, "Mobile device: " + mIsMobileDevice + ", Touch device: " + mIsTouchDevice);
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        
        if (newConfig.orientation != mLastOrientation) {
            Log.d(TAG, "Orientation changed from " + mLastOrientation + " to " + newConfig.orientation);
            
            onOrientationChanged(mLastOrientation, newConfig.orientation);
            mLastOrientation = newConfig.orientation;
            
            // Apply mobile-specific changes for orientation
            if (mIsMobileDevice) {
                applyMobileOrientationChanges(newConfig.orientation);
            }
        }
    }
    
    /**
     * Called when orientation changes. Override to handle orientation-specific logic.
     */
    protected void onOrientationChanged(int oldOrientation, int newOrientation) {
        // Override in subclasses
    }
    
    /**
     * Sets up mobile-specific optimizations
     */
    private void setupMobileOptimizations() {
        // Hide navigation bar for better mobile experience in fullscreen content
        if (shouldHideNavigationBar()) {
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
        
        // Enable hardware acceleration for smooth animations
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        );
        
        // Keep screen on during video playback for mobile devices
        if (shouldKeepScreenOn()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
    
    /**
     * Applies mobile-specific changes when orientation changes
     */
    private void applyMobileOrientationChanges(int orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Landscape mode optimizations
            onMobileLandscapeMode();
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Portrait mode optimizations  
            onMobilePortraitMode();
        }
    }
    
    /**
     * Called when device enters landscape mode. Override for landscape-specific logic.
     */
    protected void onMobileLandscapeMode() {
        // Override in subclasses
    }
    
    /**
     * Called when device enters portrait mode. Override for portrait-specific logic.
     */
    protected void onMobilePortraitMode() {
        // Override in subclasses
    }
    
    /**
     * Whether this activity should hide navigation bar for mobile devices.
     * Override to customize behavior.
     */
    protected boolean shouldHideNavigationBar() {
        return false; // Default: don't hide navigation bar
    }
    
    /**
     * Whether this activity should keep screen on for mobile devices.
     * Override to customize behavior.
     */
    protected boolean shouldKeepScreenOn() {
        return false; // Default: don't keep screen on
    }
    
    /**
     * Returns true if this is a mobile device
     */
    protected boolean isMobileDevice() {
        return mIsMobileDevice;
    }
    
    /**
     * Returns true if this device supports touch
     */
    protected boolean isTouchDevice() {
        return mIsTouchDevice;
    }
    
    /**
     * Returns true if the current orientation is landscape
     */
    protected boolean isLandscape() {
        return MobileUtils.isLandscape(this);
    }
    
    /**
     * Returns true if the current orientation is portrait
     */
    protected boolean isPortrait() {
        return MobileUtils.isPortrait(this);
    }
    
    /**
     * Gets the optimal column count for grids based on screen size and orientation
     */
    protected int getOptimalColumnCount() {
        return MobileUtils.getOptimalColumnCount(this);
    }
}