package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentHistoryBinding;
import com.google.android.material.button.MaterialButton;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        setupTabs();
        setupClickListeners();
    }

    private void setupClickListeners() {
        View.OnClickListener toDetail = v -> 
            Navigation.findNavController(v).navigate(R.id.action_HistoryFragment_to_DetailDonasiFragment);

        binding.cvHistoryItem1.setOnClickListener(toDetail);
        binding.cvHistoryItem2.setOnClickListener(toDetail);
        binding.cvHistoryItem3.setOnClickListener(toDetail);
    }

    private void setupTabs() {
        View.OnClickListener tabClickListener = v -> {
            resetTabs();
            MaterialButton selectedTab = (MaterialButton) v;
            selectedTab.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.white));
            selectedTab.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        };

        binding.tabSemua.setOnClickListener(tabClickListener);
        binding.tabUang.setOnClickListener(tabClickListener);
        binding.tabBarang.setOnClickListener(tabClickListener);
        binding.tabProses.setOnClickListener(tabClickListener);
    }

    private void resetTabs() {
        applyUnselectedStyle(binding.tabSemua);
        applyUnselectedStyle(binding.tabUang);
        applyUnselectedStyle(binding.tabBarang);
        applyUnselectedStyle(binding.tabProses);
    }
    
    private void applyUnselectedStyle(MaterialButton button) {
        button.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.white));
        button.setAlpha(0.3f);
        button.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
