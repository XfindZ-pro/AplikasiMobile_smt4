package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentManajemenProgramBinding;

public class ManajemenProgramFragment extends Fragment {

    private FragmentManajemenProgramBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentManajemenProgramBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        binding.manajemenBottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            
            // Sembunyikan semua layout tab terlebih dahulu
            binding.layoutManajemenHome.setVisibility(View.GONE);
            binding.layoutManajemenProgram.setVisibility(View.GONE);
            binding.layoutManajemenNotif.setVisibility(View.GONE);

            // Tampilkan layout sesuai tab yang dipilih
            if (id == R.id.nav_manajemen_home) {
                binding.layoutManajemenHome.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.nav_manajemen_program) {
                binding.layoutManajemenProgram.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.nav_manajemen_notif) {
                binding.layoutManajemenNotif.setVisibility(View.VISIBLE);
                return true;
            }
            return false;
        });

        // Set default selection (Beranda)
        binding.manajemenBottomNav.setSelectedItemId(R.id.nav_manajemen_home);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
