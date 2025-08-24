package com.liskovsoft.smartyoutubetv2.tv.ui.playback;

import android.annotation.TargetApi;
import android.app.PictureInPictureParams;
import android.os.Build;
import android.os.Bundle;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.liskovsoft.sharedutils.helpers.Helpers;
import com.liskovsoft.sharedutils.mylogger.Log;
import com.liskovsoft.smartyoutubetv2.common.app.models.playback.manager.PlayerEngine;
import com.liskovsoft.smartyoutubetv2.common.app.presenters.AppDialogPresenter;
import com.liskovsoft.smartyoutubetv2.common.app.views.PlaybackView;
import com.liskovsoft.smartyoutubetv2.common.prefs.GeneralData;
import com.liskovsoft.smartyoutubetv2.common.prefs.MainUIData;
import com.liskovsoft.smartyoutubetv2.common.prefs.PlayerData;
import com.liskovsoft.smartyoutubetv2.common.utils.Utils;
import com.liskovsoft.smartyoutubetv2.tv.R;
import com.liskovsoft.smartyoutubetv2.tv.ui.common.MobileFriendlyActivity;
import com.liskovsoft.smartyoutubetv2.tv.ui.widgets.gestures.MobileGestureHandler;
import com.liskovsoft.smartyoutubetv2.tv.ui.widgets.animations.MobileAnimationHelper;

/**
 * Loads PlaybackFragment and delegates input from a game controller.
 * Enhanced with mobile-friendly touch controls and orientation support.
 * <br>
 * For more information on game controller capabilities with leanback, review the
 * <a href="https://developer.android.com/training/game-controllers/controller-input.html">docs</href>.
 */
public class PlaybackActivity extends MobileFriendlyActivity implements MobileGestureHandler.SwipeListener {
    private static final String TAG = PlaybackActivity.class.getSimpleName();
    private static final float GAMEPAD_TRIGGER_INTENSITY_ON = 0.5f;
    // Off-condition slightly smaller for button debouncing.
    private static final float GAMEPAD_TRIGGER_INTENSITY_OFF = 0.45f;
    private boolean gamepadTriggerPressed = false;
    private PlaybackFragment mPlaybackFragment;
    private boolean mIsBackPressed;
    
    // Mobile UI elements
    private View mMobileOverlayControls;
    private boolean mMobileControlsVisible = false;
    private MobileGestureHandler mGestureHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_playback);
        Fragment fragment =
                getSupportFragmentManager().findFragmentByTag(getString(R.string.playback_tag));
        if (fragment instanceof PlaybackFragment) {
            mPlaybackFragment = (PlaybackFragment) fragment;
        }
        
        // Initialize mobile controls if on mobile device
        if (isMobileDevice()) {
            initializeMobileControls();
            setupGestureNavigation();
        }
    }
    
    @Override
    protected void onOrientationChanged(int oldOrientation, int newOrientation) {
        super.onOrientationChanged(oldOrientation, newOrientation);
        
        if (isMobileDevice()) {
            updateMobileControlsForOrientation();
        }
    }
    
    @Override
    protected void onMobileLandscapeMode() {
        super.onMobileLandscapeMode();
        // Hide mobile overlay controls in landscape for full screen experience
        if (mMobileOverlayControls != null) {
            mMobileOverlayControls.setVisibility(View.GONE);
            mMobileControlsVisible = false;
        }
    }
    
    @Override
    protected void onMobilePortraitMode() {
        super.onMobilePortraitMode();
        // Show mobile controls in portrait mode
        showMobileControls();
    }
    
    @Override
    protected boolean shouldKeepScreenOn() {
        return true; // Keep screen on during video playback
    }
    
    /**
     * Initialize mobile-specific playback controls
     */
    private void initializeMobileControls() {
        // Find mobile control elements
        mMobileOverlayControls = findViewById(R.id.mobile_overlay_controls);
        
        // Initialize portrait controls
        initializePortraitControls();
        
        // Initialize landscape controls
        initializeLandscapeControls();
        
        // Set up touch to show/hide controls
        setupTouchControls();
    }
    
    /**
     * Initialize portrait mode controls
     */
    private void initializePortraitControls() {
        ImageButton playPauseButton = findViewById(R.id.mobile_play_pause_button);
        ImageButton previousButton = findViewById(R.id.mobile_previous_button);
        ImageButton nextButton = findViewById(R.id.mobile_next_button);
        ImageButton rewindButton = findViewById(R.id.mobile_rewind_button);
        ImageButton fastForwardButton = findViewById(R.id.mobile_fast_forward_button);
        ImageButton fullscreenButton = findViewById(R.id.mobile_fullscreen_button);
        
        if (playPauseButton != null) {
            playPauseButton.setOnClickListener(this::onMobilePlayPauseClicked);
        }
        if (previousButton != null) {
            previousButton.setOnClickListener(this::onMobilePreviousClicked);
        }
        if (nextButton != null) {
            nextButton.setOnClickListener(this::onMobileNextClicked);
        }
        if (rewindButton != null) {
            rewindButton.setOnClickListener(this::onMobileRewindClicked);
        }
        if (fastForwardButton != null) {
            fastForwardButton.setOnClickListener(this::onMobileFastForwardClicked);
        }
        if (fullscreenButton != null) {
            fullscreenButton.setOnClickListener(this::onMobileFullscreenClicked);
        }
    }
    
    /**
     * Initialize landscape mode controls
     */
    private void initializeLandscapeControls() {
        ImageButton playPauseButton = findViewById(R.id.mobile_landscape_play_pause_button);
        ImageButton previousButton = findViewById(R.id.mobile_landscape_previous_button);
        ImageButton nextButton = findViewById(R.id.mobile_landscape_next_button);
        ImageButton rewindButton = findViewById(R.id.mobile_landscape_rewind_button);
        ImageButton fastForwardButton = findViewById(R.id.mobile_landscape_fast_forward_button);
        
        if (playPauseButton != null) {
            playPauseButton.setOnClickListener(this::onMobilePlayPauseClicked);
        }
        if (previousButton != null) {
            previousButton.setOnClickListener(this::onMobilePreviousClicked);
        }
        if (nextButton != null) {
            nextButton.setOnClickListener(this::onMobileNextClicked);
        }
        if (rewindButton != null) {
            rewindButton.setOnClickListener(this::onMobileRewindClicked);
        }
        if (fastForwardButton != null) {
            fastForwardButton.setOnClickListener(this::onMobileFastForwardClicked);
        }
    }
    
    /**
     * Set up touch controls to show/hide mobile controls
     */
    private void setupTouchControls() {
        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            rootView.setOnClickListener(v -> toggleMobileControls());
        }
    }
    
    /**
     * Update mobile controls based on orientation
     */
    private void updateMobileControlsForOrientation() {
        if (isLandscape()) {
            // In landscape, use overlay controls that can be hidden
            hideMobileControls();
        } else {
            // In portrait, show controls at bottom
            showMobileControls();
        }
    }
    
    /**
     * Show mobile controls
     */
    private void showMobileControls() {
        View portraitControls = findViewById(R.id.mobile_playback_controls);
        if (portraitControls != null) {
            MobileAnimationHelper.slideInFromBottom(portraitControls);
        }
        
        if (mMobileOverlayControls != null && isLandscape()) {
            MobileAnimationHelper.fadeIn(mMobileOverlayControls);
            mMobileControlsVisible = true;
        }
    }
    
    /**
     * Hide mobile controls
     */
    private void hideMobileControls() {
        if (mMobileOverlayControls != null && isLandscape()) {
            MobileAnimationHelper.fadeOut(mMobileOverlayControls);
            mMobileControlsVisible = false;
        }
    }
    
    /**
     * Toggle mobile controls visibility
     */
    private void toggleMobileControls() {
        if (isLandscape()) {
            if (mMobileControlsVisible) {
                hideMobileControls();
            } else {
                showMobileControls();
            }
        }
    }
    
    /**
     * Setup gesture navigation for playback
     */
    private void setupGestureNavigation() {
        mGestureHandler = new MobileGestureHandler(this, this);
        
        // Attach gesture handler to the video surface
        View videoSurface = findViewById(android.R.id.content);
        if (videoSurface != null) {
            mGestureHandler.attachToView(videoSurface);
        }
    }
    
    // Mobile control button handlers
    private void onMobilePlayPauseClicked(View view) {
        // TODO: Delegate to playback fragment
        if (mPlaybackFragment != null) {
            // mPlaybackFragment.togglePlayPause();
        }
    }
    
    private void onMobilePreviousClicked(View view) {
        // TODO: Delegate to playback fragment  
        if (mPlaybackFragment != null) {
            // mPlaybackFragment.skipToPrevious();
        }
    }
    
    private void onMobileNextClicked(View view) {
        // TODO: Delegate to playback fragment
        if (mPlaybackFragment != null) {
            // mPlaybackFragment.skipToNext();
        }
    }
    
    private void onMobileRewindClicked(View view) {
        // TODO: Delegate to playback fragment
        if (mPlaybackFragment != null) {
            // mPlaybackFragment.rewind();
        }
    }
    
    private void onMobileFastForwardClicked(View view) {
        // TODO: Delegate to playback fragment
        if (mPlaybackFragment != null) {
            // mPlaybackFragment.fastForward();
        }
    }
    
    private void onMobileFullscreenClicked(View view) {
        // Toggle between portrait and landscape for mobile
        if (isPortrait()) {
            // Switch to landscape for fullscreen
            setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            // Switch back to portrait
            setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    
    // Implement SwipeListener interface for playback gestures
    @Override
    public void onSwipeLeft() {
        // Seek forward in video
        if (mPlaybackFragment != null) {
            // TODO: Implement seek forward
        }
    }
    
    @Override
    public void onSwipeRight() {
        // Seek backward in video
        if (mPlaybackFragment != null) {
            // TODO: Implement seek backward
        }
    }
    
    @Override
    public void onSwipeUp() {
        // Increase volume
        // TODO: Implement volume control
    }
    
    @Override
    public void onSwipeDown() {
        // Decrease volume
        // TODO: Implement volume control
    }
    
    @Override
    public void onTap() {
        // Toggle playback controls visibility
        toggleMobileControls();
    }
    
    @Override
    public void onDoubleTap() {
        // Toggle play/pause
        onMobilePlayPauseClicked(null);
    }

    @Override
    protected void initTheme() {
        int playerThemeResId = MainUIData.instance(this).getColorScheme().playerThemeResId;
        if (playerThemeResId > 0) {
            setTheme(playerThemeResId);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mPlaybackFragment != null) {
            mPlaybackFragment.onDispatchKeyEvent(event);
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mPlaybackFragment != null) {
            mPlaybackFragment.onDispatchTouchEvent(event);
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        if (mPlaybackFragment != null) {
            mPlaybackFragment.onDispatchGenericMotionEvent(event);
        }

        return super.dispatchGenericMotionEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BUTTON_R1) {
            mPlaybackFragment.skipToNext();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_L1) {
            mPlaybackFragment.skipToPrevious();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_L2) {
            mPlaybackFragment.rewind();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_R2) {
            mPlaybackFragment.fastForward();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        // This method will handle gamepad events.
        if (event.getAxisValue(MotionEvent.AXIS_LTRIGGER) > GAMEPAD_TRIGGER_INTENSITY_ON
                && !gamepadTriggerPressed) {
            mPlaybackFragment.rewind();
            gamepadTriggerPressed = true;
        } else if (event.getAxisValue(MotionEvent.AXIS_RTRIGGER) > GAMEPAD_TRIGGER_INTENSITY_ON
                && !gamepadTriggerPressed) {
            mPlaybackFragment.fastForward();
            gamepadTriggerPressed = true;
        } else if (event.getAxisValue(MotionEvent.AXIS_LTRIGGER) < GAMEPAD_TRIGGER_INTENSITY_OFF
                && event.getAxisValue(MotionEvent.AXIS_RTRIGGER) < GAMEPAD_TRIGGER_INTENSITY_OFF) {
            gamepadTriggerPressed = false;
        } else if ((event.getSource() & InputDevice.SOURCE_CLASS_POINTER) != 0 && event.getAction() == MotionEvent.ACTION_SCROLL) {
            // mouse wheel handling
            Utils.volumeUp(this, getPlaybackView(), event.getAxisValue(MotionEvent.AXIS_VSCROLL) < 0.0f);
            return true;
        }
        return super.onGenericMotionEvent(event);
    }

    // For N devices that support it, not "officially"
    // More: https://medium.com/s23nyc-tech/drop-in-android-video-exoplayer2-with-picture-in-picture-e2d4f8c1eb30
    @TargetApi(24)
    @SuppressWarnings("deprecation")
    private void enterPipMode() {
        // NOTE: When exiting PIP mode onPause is called immediately after onResume

        // Also, avoid enter pip on stop!
        // More info: https://developer.android.com/guide/topics/ui/picture-in-picture#continuing_playback

        if (Helpers.isPictureInPictureSupported(this)) {
            if (wannaEnterToPip()) {
                Log.d(TAG, "Entering PIP mode...");

                try {
                    if (Build.VERSION.SDK_INT >= 26) {
                        PictureInPictureParams.Builder params = new PictureInPictureParams.Builder();
                        enterPictureInPictureMode(params.build());
                    } else {
                        enterPictureInPictureMode();
                    }
                } catch (Exception e) {
                    // Device doesn't support picture-in-picture mode
                    Log.e(TAG, e.getMessage());
                }
            }
        }
    }

    /**
     * BACK pressed, PIP player's button pressed
     */
    @Override
    public void finish() {
        Log.d(TAG, "Finishing activity...");

        //if (isBackgroundBackEnabled()) {
        //    mPlaybackFragment.blockEngine(true);
        //}

        // NOTE: When exiting PIP mode onPause is called immediately after onResume

        // Also, avoid enter pip on stop!
        // More info: https://developer.android.com/guide/topics/ui/picture-in-picture#continuing_playback

        // NOTE: block back button for PIP.
        // User pressed PIP button in the player.
        if (!skipPip()) {
            enterPipMode(); // NOTE: without this call app will hangs when pressing on PIP button
        }

        if (doNotDestroy() && !skipPip()) {
            mPlaybackFragment.blockEngine(true);
            // Ensure to opening this activity when the user is returning to the app
            getViewManager().blockTop(this);
            getViewManager().startParentView(this);
        } else {
            if (getPlayerTweaksData().isKeepFinishedActivityEnabled()) {
                //moveTaskToBack(true); // Don't do this or you'll have problems when player overlaps other apps (e.g. casting)
                getViewManager().startParentView(this);

                // Player with TextureView keeps running in background because onStop() fired with huge delay (~5sec).
                mPlaybackFragment.maybeReleasePlayer();
            } else {
                super.finish();
            }
        }
    }

    @Override
    public void finishReally() {
        mPlaybackFragment.onFinish();
        super.finishReally();
    }

    @Override
    protected void onPause() {
        boolean hasDialogBug = AppDialogPresenter.instance(this).isDialogShown() && Build.VERSION.SDK_INT <= 23;
        boolean isScreenOff = getPlayerData().getBackgroundMode() != PlayerData.BACKGROUND_MODE_DEFAULT && Utils.isHardScreenOff(this);

        if (hasDialogBug || isScreenOff) {
            mPlaybackFragment.blockEngine(true);
        }

        // Run the code before the contained fragment
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        mIsBackPressed = true;
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        mIsBackPressed = false;
        super.onResume();
    }

    @SuppressWarnings("deprecation")
    private void enterBackgroundPlayMode() {
        if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 26) {
            if (Build.VERSION.SDK_INT == 21) {
                // Playback pause fix?
                mPlaybackFragment.showOverlay(true);
            }

            if (mPlaybackFragment.isPlaying()) {
                // Argument equals true to notify the system that the activity
                // wishes to be visible behind other translucent activities
                if (!requestVisibleBehind(true)) {
                    // App-specific method to stop playback and release resources
                    // because call to requestVisibleBehind(true) failed
                    mPlaybackFragment.onDestroy();
                }
            } else {
                // Argument equals false because the activity is not playing
                requestVisibleBehind(false);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onVisibleBehindCanceled() {
        // App-specific method to stop playback and release resources
        mPlaybackFragment.onDestroy();
        super.onVisibleBehindCanceled();
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);

        mPlaybackFragment.onPIPChanged(isInPictureInPictureMode);
    }

    /**
     * HOME or BACK pressed
     */
    @Override
    public void onUserLeaveHint() {
        // Check that user not open dialog/search activity instead of really leaving the activity
        // Activity may be overlapped by the dialog, back is pressed or new view started
        if (mIsBackPressed || isFinishing() || getViewManager().isNewViewPending() || getGeneralData().getBackgroundPlaybackShortcut() == GeneralData.BACKGROUND_PLAYBACK_SHORTCUT_BACK) {
            return;
        }

        switch (getPlayerData().getBackgroundMode()) {
            case PlayerData.BACKGROUND_MODE_PLAY_BEHIND:
                enterBackgroundPlayMode();
                // Do we need to do something additional when running Play Behind?
                break;
            case PlayerData.BACKGROUND_MODE_PIP:
                enterPipMode();
                if (doNotDestroy()) {
                    mPlaybackFragment.blockEngine(true);
                    // Ensure to opening this activity when the user will return to the app
                    getViewManager().blockTop(this);
                    // Enable collapse app to Home launcher
                    //getViewManager().enableMoveToBack(true);
                }
                break;
            case PlayerData.BACKGROUND_MODE_SOUND:
                if (doNotDestroy()) {
                    // Ensure to continue a playback
                    mPlaybackFragment.blockEngine(true);
                    getViewManager().blockTop(this);
                    //getViewManager().enableMoveToBack(true);
                }
                break;
        }
    }

    public boolean isInPipMode() {
        if (Build.VERSION.SDK_INT < 24) {
            return false;
        }

        return isInPictureInPictureMode();
    }

    public PlaybackView getPlaybackView() {
        return mPlaybackFragment;
    }

    private boolean skipPip() {
        return mIsBackPressed && getGeneralData().getBackgroundPlaybackShortcut() == GeneralData.BACKGROUND_PLAYBACK_SHORTCUT_HOME;
    }

    private boolean isEngineBlocked() {
        return mPlaybackFragment != null && mPlaybackFragment.isEngineBlocked();
    }

    @TargetApi(24)
    private boolean wannaEnterToPip() {
        //return mPlaybackFragment != null && mPlaybackFragment.getBackgroundMode() == PlayerEngine.BACKGROUND_MODE_PIP && !isInPictureInPictureMode();
        //return mPlaybackFragment != null && mPlaybackFragment.isEngineBlocked() && !isInPictureInPictureMode();
        boolean isPip = getPlayerData().getBackgroundMode() == PlayerData.BACKGROUND_MODE_PIP || isEngineBlocked();
        return isPip && !isInPictureInPictureMode();
    }

    private boolean doNotDestroy() {
        sIsInPipMode = isInPipMode();
        //return sIsInPipMode || mPlaybackFragment.getBackgroundMode() == PlayerEngine.BACKGROUND_MODE_SOUND;
        //return sIsInPipMode || mPlaybackFragment.isEngineBlocked();
        boolean isBackground = getPlayerData().getBackgroundMode() == PlayerEngine.BACKGROUND_MODE_SOUND || isEngineBlocked();
        return sIsInPipMode || isBackground;
    }

    //private boolean isBackgroundBackEnabled() {
    //    return getGeneralData().getBackgroundPlaybackShortcut() == GeneralData.BACKGROUND_PLAYBACK_SHORTCUT_BACK ||
    //            getGeneralData().getBackgroundPlaybackShortcut() == GeneralData.BACKGROUND_PLAYBACK_SHORTCUT_HOME_BACK;
    //}
}
