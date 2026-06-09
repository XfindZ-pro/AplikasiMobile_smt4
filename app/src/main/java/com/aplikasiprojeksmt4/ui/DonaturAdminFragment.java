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
import com.aplikasiprojeksmt4.databinding.DonaturAdminBinding;

public class DonaturAdminFragment extends Fragment {

    private DonaturAdminBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DonaturAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup Bottom Navigation
        binding.bottomNavigation.setSelectedItemId(R.id.nav_donatur);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_donatur) {
                return true;
            } else if (id == R.id.nav_beranda) {
                Navigation.findNavController(view).navigate(R.id.action_DonaturAdminFragment_to_HomepageAdminFragment);
                return true;
            } else if (id == R.id.nav_program_admin) {
                Navigation.findNavController(view).navigate(R.id.action_DonaturAdminFragment_to_PageProgramFragment);
                return true;
            } else if (id == R.id.nav_statistik) {
                Navigation.findNavController(view).navigate(R.id.action_DonaturAdminFragment_to_StatistikFragment);
                return true;
            }
            return false;
        });

        // Di sini bisa ditambahkan logika untuk mengisi RecyclerView donatur
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
