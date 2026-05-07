package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.WelcomePageBinding;

public class WelcomeFragment extends Fragment {

    private WelcomePageBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = WelcomePageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Navigasi ke Halaman Login
        binding.btnLogin.setOnClickListener(v -> 
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_WelcomeFragment_to_LoginFragment)
        );

        // Navigasi ke Halaman Register
        binding.btnRegister.setOnClickListener(v -> 
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_WelcomeFragment_to_RegisterFragment)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
