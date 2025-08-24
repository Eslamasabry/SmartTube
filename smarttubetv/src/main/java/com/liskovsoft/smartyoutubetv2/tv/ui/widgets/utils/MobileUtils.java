package com.liskovsoft.smartyoutubetv2.tv.ui.widgets.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Utility class for handling mobile-friendly features like orientation changes,
 * screen size detection, and touch interface optimization.
 */
public class MobileUtils {
    private static final String TAG = MobileUtils.class.getSimpleName();
    
    // Screen size thresholds for mobile detection
    private static final int MOBILE_WIDTH_THRESHOLD_DP = 600;
    private static final int TABLET_WIDTH_THRESHOLD_DP = 900;
    
    /**
     * Checks if the device is likely a mobile phone (as opposed to tablet or TV)
     */
    public static boolean isMobileDevice(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float widthDp = metrics.widthPixels / metrics.density;
        float heightDp = metrics.heightPixels / metrics.density;
        
        // Consider it mobile if smallest dimension is less than threshold
        float smallestDp = Math.min(widthDp, heightDp);
        return smallestDp < MOBILE_WIDTH_THRESHOLD_DP;
    }
    
    /**
     * Checks if the device is a tablet
     */
    public static boolean isTabletDevice(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float widthDp = metrics.widthPixels / metrics.density;
        float heightDp = metrics.heightPixels / metrics.density;
        
        float smallestDp = Math.min(widthDp, heightDp);
        return smallestDp >= MOBILE_WIDTH_THRESHOLD_DP && smallestDp < TABLET_WIDTH_THRESHOLD_DP;
    }
    
    /**
     * Checks if the device supports touch interface
     */
    public static boolean isTouchDevice(Context context) {
        return context.getPackageManager().hasSystemFeature("android.hardware.touchscreen");
    }
    
    /**
     * Gets the current screen orientation
     */
    public static int getCurrentOrientation(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            return windowManager.getDefaultDisplay().getRotation();
        }
        return Surface.ROTATION_0;
    }
    
    /**
     * Checks if the device is currently in landscape orientation
     */
    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
    
    /**
     * Checks if the device is currently in portrait orientation
     */
    public static boolean isPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }
    
    /**
     * Forces the activity to use portrait orientation
     */
    public static void forcePortrait(Activity activity) {
        if (activity != null) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    
    /**
     * Forces the activity to use landscape orientation
     */
    public static void forceLandscape(Activity activity) {
        if (activity != null) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
    
    /**
     * Allows the activity to rotate freely
     */
    public static void allowRotation(Activity activity) {
        if (activity != null) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }
    
    /**
     * Gets the screen width in pixels
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }
    
    /**
     * Gets the screen height in pixels
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }
    
    /**
     * Converts dp to pixels
     */
    public static int dpToPx(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * metrics.density);
    }
    
    /**
     * Converts pixels to dp
     */
    public static float pxToDp(Context context, int px) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return px / metrics.density;
    }
    
    /**
     * Checks if the current layout should use mobile-optimized components
     */
    public static boolean shouldUseMobileLayout(Context context) {
        return isMobileDevice(context) && isTouchDevice(context);
    }
    
    /**
     * Gets the appropriate column count for grid views based on screen size and orientation
     */
    public static int getOptimalColumnCount(Context context) {
        if (isPortrait(context)) {
            return isMobileDevice(context) ? 1 : (isTabletDevice(context) ? 2 : 3);
        } else {
            return isMobileDevice(context) ? 2 : (isTabletDevice(context) ? 3 : 4);
        }
    }
}