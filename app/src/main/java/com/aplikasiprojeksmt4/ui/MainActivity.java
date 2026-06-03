package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.ActivityMainBinding;
import com.aplikasiprojeksmt4.utils.UpdateManager;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // In-App Auto Update Check
        UpdateManager updateManager = new UpdateManager(this);
        updateManager.checkForUpdates();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            
            // Define top-level destinations to avoid showing back button on main tabs
            Set<Integer> topLevelDestinations = new HashSet<>();
            topLevelDestinations.add(R.id.HomeFragment);
            topLevelDestinations.add(R.id.MapsFragment);
            topLevelDestinations.add(R.id.HistoryFragment);
            topLevelDestinations.add(R.id.ProfileFragment);
            topLevelDestinations.add(R.id.FirstFragment);

            appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations).build();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

            // Handle Auto-Login from SplashActivity
            boolean isLoggedIn = getIntent().getBooleanExtra("IS_LOGGED_IN", false);
            if (isLoggedIn) {
                // Navigate to Home and clear the intro fragments from the backstack
                navController.navigate(R.id.HomeFragment, null, new NavOptions.Builder()
                        .setPopUpTo(R.id.nav_graph, true)
                        .build());
            }

            // Control visibility of ActionBar based on destination
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int id = destination.getId();
                if (id == R.id.HomeFragment || id == R.id.FirstFragment || id == R.id.WelcomeFragment || 
                    id == R.id.LoginFragment || id == R.id.RegisterFragment) {
                    if (getSupportActionBar() != null) getSupportActionBar().hide();
                } else {
                    if (getSupportActionBar() != null) getSupportActionBar().show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            return NavigationUI.navigateUp(navController, appBarConfiguration)
                    || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }
}
