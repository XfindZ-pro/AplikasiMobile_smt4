package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentLoginBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private FirebaseFirestore db;

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
            Toast.makeText(getContext(), "Firebase Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        binding.tvToRegister.setOnClickListener(v -> {
            if (isAdded()) {
                NavHostFragment.findNavController(this).navigate(R.id.action_LoginFragment_to_RegisterFragment);
            }
        });

        binding.btnLoginSubmit.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        if (binding == null) return;

        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            binding.etEmail.setError("Email tidak boleh kosong");
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError("Format email tidak valid");
            return;
        }

        if (password.isEmpty()) {
            binding.etPassword.setError("Password tidak boleh kosong");
            return;
        }

        binding.btnLoginSubmit.setEnabled(false);
        binding.btnLoginSubmit.setText("Masuk...");

        if (db == null) {
            Toast.makeText(getContext(), "Database tidak siap", Toast.LENGTH_SHORT).show();
            binding.btnLoginSubmit.setEnabled(true);
            binding.btnLoginSubmit.setText("MASUK");
            return;
        }

        db.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(task -> {
                    if (!isAdded() || binding == null) return;

                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        Toast.makeText(getContext(), "Login Berhasil!", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(this).navigate(R.id.action_LoginFragment_to_FirstFragment);
                    } else {
                        binding.btnLoginSubmit.setEnabled(true);
                        binding.btnLoginSubmit.setText("MASUK");
                        Toast.makeText(getContext(), "Email atau password salah!", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if (!isAdded() || binding == null) return;
                    binding.btnLoginSubmit.setEnabled(true);
                    binding.btnLoginSubmit.setText("MASUK");
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
