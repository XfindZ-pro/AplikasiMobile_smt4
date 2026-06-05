package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.HomepageadminBinding;

public class HomepageAdminFragment extends Fragment {

    private HomepageadminBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = HomepageadminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Navigasi ke Verifikasi Ajuan Program
        binding.btnVerifikasi.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_HomepageAdminFragment_to_VerifikasiAjuanProgramAdminFragment)
        );

        // Navigasi ke Verifikasi Donasi Dana
        binding.btnDonasiDana.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_HomepageAdminFragment_to_VerifikasiDonasiDanaFragment)
        );

        // Setup Bottom Navigation
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_beranda) {
                return true;
            }
            // Tambahkan logika navigasi tab lain di sini
            return false;
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
