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
import com.aplikasiprojeksmt4.databinding.HomepageAdminBinding;

public class HomepageAdminFragment extends Fragment {

    private HomepageAdminBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = HomepageAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Navigasi ke Tambah Program
        binding.btnTambahProgram.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_HomepageAdminFragment_to_TambahProgramFragment)
        );

        // Setup Bottom Navigation jika diperlukan
        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.adminHome) {
                // Sudah di Home
                return true;
            }
            // Tambahkan logika navigasi tab lain di sini jika diperlukan
            return false;
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
