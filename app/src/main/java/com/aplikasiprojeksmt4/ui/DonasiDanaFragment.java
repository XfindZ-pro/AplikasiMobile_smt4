package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentDonasiDanaBinding;

import java.text.NumberFormat;
import java.util.Locale;

public class DonasiDanaFragment extends Fragment {

    private FragmentDonasiDanaBinding binding;
    private String selectedMethod = "Transfer Bank";
    private String selectedBank = "BCA";
    private long currentAmount = 0;
    private String programName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDonasiDanaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            programName = getArguments().getString("programName");
        }

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        setupNominalButtons();
        setupPaymentMethodSelection();
        setupAmountInput();

        binding.btnPayNow.setOnClickListener(v -> {
            if (currentAmount < 10000) {
                Toast.makeText(getContext(), "Minimal donasi adalah Rp 10.000", Toast.LENGTH_SHORT).show();
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putString("amount", String.valueOf(currentAmount));
            bundle.putString("method", selectedMethod);
            bundle.putString("bank", selectedBank);
            bundle.putString("programId", getArguments() != null ? getArguments().getString("programId") : "");
            bundle.putString("programName", programName);
            bundle.putString("message", binding.etMessage.getText().toString());
            bundle.putBoolean("isAnonymous", binding.switchAnonymous.isChecked());

            Navigation.findNavController(v).navigate(R.id.action_DonasiDanaFragment_to_PembayaranFragment, bundle);
        });
    }

    private void setupNominalButtons() {
        binding.btnNominal10.setOnClickListener(v -> updateAmount(10000));
        binding.btnNominal20.setOnClickListener(v -> updateAmount(20000));
        binding.btnNominal50.setOnClickListener(v -> updateAmount(50000));
        binding.btnNominal100.setOnClickListener(v -> updateAmount(100000));
        binding.btnNominal200.setOnClickListener(v -> updateAmount(200000));
        binding.btnNominalLainnya.setOnClickListener(v -> {
            binding.etCustomNominal.setText("");
            binding.etCustomNominal.requestFocus();
        });
    }

    private void updateAmount(long amount) {
        currentAmount = amount;
        String formatted = NumberFormat.getNumberInstance(new Locale("id", "ID")).format(amount);
        binding.etCustomNominal.setText(formatted);
    }

    private void setupAmountInput() {
        binding.etCustomNominal.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    binding.etCustomNominal.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^0-9]", "");
                    if (!cleanString.isEmpty()) {
                        long parsed = Long.parseLong(cleanString);
                        currentAmount = parsed;
                        String formatted = NumberFormat.getNumberInstance(new Locale("id", "ID")).format(parsed);
                        current = formatted;
                        binding.etCustomNominal.setText(formatted);
                        binding.etCustomNominal.setSelection(formatted.length());
                    } else {
                        currentAmount = 0;
                        current = "";
                        binding.etCustomNominal.setText("");
                    }

                    binding.etCustomNominal.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupPaymentMethodSelection() {
        binding.rgBanks.setOnCheckedChangeListener((group, checkedId) -> updateSelectedBank());

        binding.btnMethodTransfer.setOnClickListener(v -> {
            binding.rbTransfer.setChecked(true);
            binding.rbQRIS.setChecked(false);
            binding.llBankList.setVisibility(View.VISIBLE);
            selectedMethod = "Transfer Bank";
            updateSelectedBank();
        });

        binding.btnMethodQRIS.setOnClickListener(v -> {
            binding.rbQRIS.setChecked(true);
            binding.rbTransfer.setChecked(false);
            binding.llBankList.setVisibility(View.GONE);
            selectedMethod = "QRIS";
            selectedBank = "";
        });

        binding.rbTransfer.setOnClickListener(v -> binding.btnMethodTransfer.performClick());
        binding.rbQRIS.setOnClickListener(v -> binding.btnMethodQRIS.performClick());

        // Default selection
        binding.rbBCA.setChecked(true);
        updateSelectedBank();
    }

    private void updateSelectedBank() {
        int checkedId = binding.rgBanks.getCheckedRadioButtonId();
        if (checkedId == R.id.rbBCA) {
            selectedBank = "BCA";
        } else if (checkedId == R.id.rbBNI) {
            selectedBank = "BNI";
        } else if (checkedId == R.id.rbBRI) {
            selectedBank = "BRI";
        }
        binding.tvSelectedBank.setText(selectedBank);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
