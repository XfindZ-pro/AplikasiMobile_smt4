package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.ActivityMainBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 1. Memanggil alat pengantar paket Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();

// 2. Membungkus isi suratnya
        Map<String, Object> suratTest = new HashMap<>();
        suratTest.put("pesan", "Halo Firebase, ini DonasiKu!");
        suratTest.put("pengirim", "Firzanta");

// 3. Mengirim suratnya ke ruangan bernama "testing" di gudang Firebase
        db.collection("testing")
                .add(suratTest)
                .addOnSuccessListener(documentReference -> {
                    System.out.println("HORE! Surat berhasil terkirim ke Firebase!");
                })
                .addOnFailureListener(e -> {
                    System.out.println("YAH! Suratnya gagal terkirim...");
                });

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // BAGIAN INI: Mengarahkan tombol FAB ke Welcome Page
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.WelcomeFragment);
            }
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
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
