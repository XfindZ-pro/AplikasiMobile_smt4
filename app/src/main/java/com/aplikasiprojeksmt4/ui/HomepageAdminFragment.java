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

        // Navigasi ke Profil Admin
        binding.btnProfile.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_HomepageAdminFragment_to_ProfileAdminFragment)
        );

        // Navigasi ke Verifikasi Ajuan Program
        binding.btnVerifikasi.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_HomepageAdminFragment_to_VerifikasiAjuanProgramAdminFragment)
        );

        // Navigasi ke Verifikasi Donasi Dana
        binding.btnDonasiDana.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_HomepageAdminFragment_to_VerifikasiDonasiDanaFragment)
        );

        // Navigasi ke Verifikasi Donasi Barang
        binding.btnDonasiBarang.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_HomepageAdminFragment_to_VerifikasiDonasiBarangFragment)
        );

        // Navigasi ke Statistik via tombol "Lihat Statistik"
        binding.tvLihatStatistik.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_HomepageAdminFragment_to_StatistikFragment)
        );

        // Setup Bottom Navigation
        binding.bottomNavigation.setSelectedItemId(R.id.nav_beranda);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_beranda) {
                return true;
            } else if (id == R.id.nav_program_admin) {
                Navigation.findNavController(view).navigate(R.id.action_HomepageAdminFragment_to_PageProgramFragment);
                return true;
            } else if (id == R.id.nav_statistik) {
                Navigation.findNavController(view).navigate(R.id.action_HomepageAdminFragment_to_StatistikFragment);
                return true;
            } else if (id == R.id.nav_donatur) {
                Navigation.findNavController(view).navigate(R.id.action_HomepageAdminFragment_to_DonaturAdminFragment);
                return true;
            }
            return false;
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
