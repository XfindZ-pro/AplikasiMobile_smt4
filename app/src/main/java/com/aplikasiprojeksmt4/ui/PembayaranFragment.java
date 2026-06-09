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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PembayaranFragment extends Fragment {

    private FragmentPembayaranBinding binding;
    private String amount;
    private String method;
    private String bank;
    private String programName;
    private String donorName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPembayaranBinding.inflate(inflater, container, false);
        if (getArguments() != null) {
            amount = getArguments().getString("amount");
            method = getArguments().getString("method");
            bank = getArguments().getString("bank");
            programName = getArguments().getString("programName");
            boolean isAnonymous = getArguments().getBoolean("isAnonymous", false);
            donorName = isAnonymous ? "Anonim" : "Zulpa Apipah"; // Default name for demo
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        displayData();
        setupLayoutByMethod();
        startTimer();

        binding.btnCopyVa.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                ClipData clip = ClipData.newPlainText("VA Number", binding.tvVaNumber.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Nomor VA disalin", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSaveQR.setOnClickListener(v -> {
            Toast.makeText(getContext(), "QR Code disimpan ke galeri", Toast.LENGTH_SHORT).show();
        });

        binding.btnCheckStatus.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            if (getArguments() != null) {
                bundle.putAll(getArguments());
                if (amount != null) {
                    try {
                        long val = Long.parseLong(amount);
                        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                        formatter.setMaximumFractionDigits(0);
                        bundle.putString("amount_formatted", formatter.format(val));
                    } catch (Exception e) {
                        bundle.putString("amount_formatted", "Rp " + amount);
                    }
                }
            }
            try {
                Navigation.findNavController(v).navigate(R.id.action_PembayaranFragment_to_StatusPembayaranFragment, bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        binding.btnCancel.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void setupLayoutByMethod() {
        if ("QRIS".equalsIgnoreCase(method)) {
            binding.tvHeaderTitle.setText("Pembayaran QRIS");
            binding.cardQRIS.setVisibility(View.VISIBLE);
            binding.cardBank.setVisibility(View.GONE);
        } else {
            binding.tvHeaderTitle.setText("Pembayaran");
            binding.cardQRIS.setVisibility(View.GONE);
            binding.cardBank.setVisibility(View.VISIBLE);
            binding.tvBankName.setText("Bank " + (bank != null && !bank.isEmpty() ? bank : "BCA"));
        }
    }

    private void displayData() {
        if (amount != null) {
            try {
                long val = Long.parseLong(amount);
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                formatter.setMaximumFractionDigits(0);
                binding.tvTotalAmount.setText(formatter.format(val));
            } catch (Exception e) {
                binding.tvTotalAmount.setText("Rp " + amount);
            }
        }

        binding.tvProgramName.setText(programName != null ? programName : "Donasi Peduli Sesama");
        binding.tvDonorName.setText(donorName);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("id", "ID"));
        binding.tvDeadline.setText(sdf.format(cal.getTime()));
        
        String va = "123 0858 " + (int)(Math.random() * 9000 + 1000) + " " + (int)(Math.random() * 9000 + 1000);
        binding.tvVaNumber.setText(va);
    }

    private void startTimer() {
        new CountDownTimer(86400000, 1000) { // 24 hours
            public void onTick(long millisUntilFinished) {
                if (binding == null) return;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                binding.tvCountdown.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
            }

            public void onFinish() {
                if (binding != null) binding.tvCountdown.setText("00:00:00");
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
