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
import com.aplikasiprojeksmt4.databinding.FragmentStatusPembayaranBinding;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StatusPembayaranFragment extends Fragment {

    private FragmentStatusPembayaranBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStatusPembayaranBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Display current time
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("id", "ID"));
        binding.tvTime.setText(sdf.format(new Date()));

        // Random transaction ID
        String transId = "DKU-" + System.currentTimeMillis() / 1000;
        binding.tvTransactionId.setText(transId);

        binding.btnBackHome.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_StatusPembayaranFragment_to_HomeFragment)
        );

        binding.btnViewReceipt.setOnClickListener(v -> {
            // Logic to show receipt or navigate to history
            Navigation.findNavController(v).navigate(R.id.action_StatusPembayaranFragment_to_HomeFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
