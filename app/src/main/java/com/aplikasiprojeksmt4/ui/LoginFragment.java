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

        db = FirebaseFirestore.getInstance();

        // Navigasi ke Register jika teks di klik
        binding.tvToRegister.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_LoginFragment_to_RegisterFragment);
        });

        binding.btnLoginSubmit.setOnClickListener(v -> {
            loginUser();
        });
    }

    private void loginUser() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        // Validasi input
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

        // Cek ke Firestore
        db.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        // Login sukses
                        Toast.makeText(getContext(), "Login Berhasil!", Toast.LENGTH_SHORT).show();
                        // Navigasi ke halaman utama (FirstFragment)
                        Navigation.findNavController(requireView()).navigate(R.id.FirstFragment);
                    } else {
                        // Login gagal
                        binding.btnLoginSubmit.setEnabled(true);
                        binding.btnLoginSubmit.setText("MASUK");
                        
                        // Menampilkan Toast dan memastikan link register terlihat/jelas
                        Toast.makeText(getContext(), "Akun tidak ditemukan atau password salah!", Toast.LENGTH_LONG).show();
                        binding.tvToRegister.setText("Akun tidak ditemukan? Daftar di sini");
                        binding.tvToRegister.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    }
                })
                .addOnFailureListener(e -> {
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
