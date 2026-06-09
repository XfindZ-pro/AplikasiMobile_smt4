package com.aplikasiprojeksmt4.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentPembayaranBinding;

import java.text.NumberFormat;
import java.util.Locale;

public class PembayaranFragment extends Fragment {

    private FragmentPembayaranBinding binding;
    private String amount;
    private String method;
    private String bank;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPembayaranBinding.inflate(inflater, container, false);
        if (getArguments() != null) {
            amount = getArguments().getString("amount");
            method = getArguments().getString("method");
            bank = getArguments().getString("bank");
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        displayData();
        startTimer();

        binding.btnCopyVa.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("VA Number", binding.tvVaNumber.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Nomor VA disalin", Toast.LENGTH_SHORT).show();
        });

        binding.btnCheckStatus.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("amount", amount);
            bundle.putString("programId", getArguments().getString("programId"));
            Navigation.findNavController(v).navigate(R.id.action_PembayaranFragment_to_StatusPembayaranFragment, bundle);
        });
    }

    private void displayData() {
        if (amount != null) {
            long val = Long.parseLong(amount);
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            formatter.setMaximumFractionDigits(0);
            binding.tvTotalAmount.setText(formatter.format(val));
        }

        binding.tvBankName.setText("Bank " + bank);
        
        // Pseudo-random VA generation for demo
        String va = "123 0858 " + (int)(Math.random() * 9000 + 1000) + " " + (int)(Math.random() * 9000 + 1000);
        binding.tvVaNumber.setText(va);
    }

    private void startTimer() {
        new CountDownTimer(86400000, 1000) { // 24 hours
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                binding.tvCountdown.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
            }

            public void onFinish() {
                binding.tvCountdown.setText("00:00:00");
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
