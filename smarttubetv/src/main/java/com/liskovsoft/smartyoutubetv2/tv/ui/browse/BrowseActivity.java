package com.liskovsoft.smartyoutubetv2.tv.ui.browse;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.liskovsoft.smartyoutubetv2.common.prefs.MainUIData;
import com.liskovsoft.smartyoutubetv2.tv.R;
import com.liskovsoft.smartyoutubetv2.tv.ui.common.MobileFriendlyActivity;

public class BrowseActivity extends MobileFriendlyActivity {
    private static final String TAG = BrowseActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        
        // Initialize mobile-specific UI elements if on mobile device
        if (isMobileDevice()) {
            initializeMobileUI();
        }
    }

    @Override
    protected void onOrientationChanged(int oldOrientation, int newOrientation) {
        super.onOrientationChanged(oldOrientation, newOrientation);
        
        // Handle orientation changes for mobile devices
        if (isMobileDevice()) {
            updateMobileUIForOrientation();
        }
    }
    
    /**
     * Initialize mobile-specific UI components
     */
    private void initializeMobileUI() {
        // Find mobile toolbar elements (they may not exist in TV layout)
        ImageButton menuButton = findViewById(R.id.mobile_menu_button);
        ImageButton searchButton = findViewById(R.id.mobile_search_button);
        TextView titleView = findViewById(R.id.mobile_title);
        
        if (menuButton != null) {
            menuButton.setOnClickListener(this::onMobileMenuClicked);
        }
        
        if (searchButton != null) {
            searchButton.setOnClickListener(this::onMobileSearchClicked);
        }
        
        if (titleView != null) {
            titleView.setText(getString(R.string.app_name));
        }
    }
    
    /**
     * Update mobile UI elements when orientation changes
     */
    private void updateMobileUIForOrientation() {
        // Update layout elements based on orientation
        View sidePanel = findViewById(R.id.mobile_side_panel);
        View bottomNavigation = findViewById(R.id.mobile_bottom_navigation);
        
        if (isLandscape()) {
            // Show side panel in landscape if available
            if (sidePanel != null) {
                sidePanel.setVisibility(View.GONE); // Initially hidden, can be toggled
            }
            // Hide bottom navigation in landscape
            if (bottomNavigation != null) {
                bottomNavigation.setVisibility(View.GONE);
            }
        } else {
            // Hide side panel in portrait
            if (sidePanel != null) {
                sidePanel.setVisibility(View.GONE);
            }
            // Bottom navigation can be shown in portrait if needed
            if (bottomNavigation != null) {
                bottomNavigation.setVisibility(View.GONE); // Keep hidden for now
            }
        }
    }
    
    /**
     * Handle mobile menu button click
     */
    private void onMobileMenuClicked(View view) {
        // TODO: Implement navigation drawer or menu
        // For now, this could open a settings dialog or navigation menu
    }
    
    /**
     * Handle mobile search button click  
     */
    private void onMobileSearchClicked(View view) {
        // TODO: Open search interface
        // This could start the search activity or show search UI
    }

    @Override
    protected void initTheme() {
        int browseThemeResId = MainUIData.instance(this).getColorScheme().browseThemeResId;
        if (browseThemeResId > 0) {
            setTheme(browseThemeResId);
        }
    }
}
