package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentBuktiDonasiBinding;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BuktiDonasiFragment extends Fragment {

    private FragmentBuktiDonasiBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBuktiDonasiBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        binding.btnClose.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_BuktiDonasiFragment_to_HomeFragment));

        displayData();

        binding.btnDownload.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Bukti donasi disimpan ke galeri", Toast.LENGTH_SHORT).show();
        });
    }

    private void displayData() {
        if (getArguments() != null) {
            String amount = getArguments().getString("amount");
            String method = getArguments().getString("method");
            String bank = getArguments().getString("bank");
            boolean isAnonymous = getArguments().getBoolean("isAnonymous");

            if (amount != null) {
                long val = Long.parseLong(amount);
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                formatter.setMaximumFractionDigits(0);
                binding.tvAmount.setText(formatter.format(val));
            }

            binding.tvMethod.setText(method + (bank != null && !bank.isEmpty() ? " " + bank : ""));
            binding.tvDonorName.setText(isAnonymous ? "Anonim" : "Donatur");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("id", "ID"));
        binding.tvDate.setText(sdf.format(new Date()));
        
        binding.tvTransactionId.setText("DKU-" + System.currentTimeMillis() / 1000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
