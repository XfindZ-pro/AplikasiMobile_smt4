package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.ActivityMainBinding;

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

        setSupportActionBar(binding.toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            
            // Define top-level destinations to avoid showing back button
            Set<Integer> topLevelDestinations = new HashSet<>();
            topLevelDestinations.add(R.id.HomeFragment);
            topLevelDestinations.add(R.id.FirstFragment); // For the intro screens

            appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations).build();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

            // Control visibility of Bottom Navigation based on destination
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.HomeFragment || 
                    destination.getId() == R.id.MapsFragment || 
                    destination.getId() == R.id.HistoryFragment || 
                    destination.getId() == R.id.ProfileFragment) {
                    
                    binding.bottomAppBar.setVisibility(View.VISIBLE);
                    binding.fab.setVisibility(View.VISIBLE);
                    if (getSupportActionBar() != null) getSupportActionBar().hide();
                } else {
                    binding.bottomAppBar.setVisibility(View.GONE);
                    binding.fab.setVisibility(View.GONE);
                    
                    // Specific logic for fragments that should show/hide toolbar
                    if (destination.getId() == R.id.FirstFragment || 
                        destination.getId() == R.id.WelcomeFragment || 
                        destination.getId() == R.id.LoginFragment || 
                        destination.getId() == R.id.RegisterFragment) {
                        if (getSupportActionBar() != null) getSupportActionBar().hide();
                    } else {
                        if (getSupportActionBar() != null) getSupportActionBar().show();
                    }
                }
            });
        }

        binding.fab.setOnClickListener(view -> {
            Toast.makeText(this, "Aksi Tambah Donasi", Toast.LENGTH_SHORT).show();
        });
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
