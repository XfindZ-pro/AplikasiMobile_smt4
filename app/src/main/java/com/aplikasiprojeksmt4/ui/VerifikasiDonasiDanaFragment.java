package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.PageVerifdonasidanaBinding;

public class VerifikasiDonasiDanaFragment extends Fragment {

    private PageVerifdonasidanaBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PageVerifdonasidanaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Logika Filter Sederhana (hanya visual untuk desain)
        binding.filterSemua.setOnClickListener(this::setActiveFilter);
        binding.filterSukses.setOnClickListener(this::setActiveFilter);
        binding.filterPerluVerifikasi.setOnClickListener(this::setActiveFilter);
        binding.filterGagal.setOnClickListener(this::setActiveFilter);
    }

    private void setActiveFilter(View activeView) {
        // Reset semua filter ke style normal
        binding.filterSemua.setBackgroundResource(R.drawable.bg_card_white);
        binding.filterSukses.setBackgroundResource(R.drawable.bg_card_white);
        binding.filterPerluVerifikasi.setBackgroundResource(R.drawable.bg_card_white);
        binding.filterGagal.setBackgroundResource(R.drawable.bg_card_white);

        int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary_purple);
        binding.filterSemua.setTextColor(primaryColor);
        binding.filterSukses.setTextColor(primaryColor);
        binding.filterPerluVerifikasi.setTextColor(primaryColor);
        binding.filterGagal.setTextColor(primaryColor);

        // Set active style
        activeView.setBackgroundResource(R.drawable.bg_header_admin); // menggunakan gradasi sebagai background aktif
        if (activeView instanceof TextView) {
            ((TextView) activeView).setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
