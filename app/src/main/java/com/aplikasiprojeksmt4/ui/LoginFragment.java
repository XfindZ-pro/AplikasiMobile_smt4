package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentLoginBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private FirebaseFirestore db;
    private boolean isPasswordVisible = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            db = FirebaseFirestore.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Toggle Password Visibility
        binding.ivPasswordToggle.setOnClickListener(v -> {
            if (isPasswordVisible) {
                binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.ivPasswordToggle.setImageResource(android.R.drawable.ic_menu_view);
            } else {
                binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                binding.ivPasswordToggle.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            }
            isPasswordVisible = !isPasswordVisible;
            binding.etPassword.setSelection(binding.etPassword.getText().length());
        });

        // Navigasi ke Register
        binding.tvToRegister.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_LoginFragment_to_RegisterFragment);
        });

        binding.btnLoginSubmit.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        if (binding == null) return;

        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError("Email tidak valid");
            return;
        }

        if (password.isEmpty()) {
            binding.etPassword.setError("Password tidak boleh kosong");
            return;
        }

        if (db == null) {
            Toast.makeText(getContext(), "Database Error", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnLoginSubmit.setEnabled(false);
        binding.btnLoginSubmit.setText("Loading...");

        db.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(task -> {
                    if (binding == null) return;
                    binding.btnLoginSubmit.setEnabled(true);
                    binding.btnLoginSubmit.setText("Masuk");

                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        Toast.makeText(getContext(), "Login Berhasil", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(this).navigate(R.id.action_LoginFragment_to_FirstFragment);
                    } else {
                        Toast.makeText(getContext(), "Email atau Password Salah", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
