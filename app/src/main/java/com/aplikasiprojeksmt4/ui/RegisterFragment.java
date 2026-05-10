package com.aplikasiprojeksmt4.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentRegisterBinding;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private FirebaseFirestore db;
    private String encodedImage = "";

    private final ActivityResultLauncher<String> getContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null && binding != null) {
                    try {
                        binding.ivRegProfile.setImageURI(uri);
                        encodedImage = encodeImage(uri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
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

        binding.ivRegProfile.setOnClickListener(v -> getContent.launch("image/*"));

        binding.btnRegisterSubmit.setOnClickListener(v -> registerUser());
    }

    private String encodeImage(Uri uri) throws Exception {
        InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        byte[] bytes = outputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void registerUser() {
        if (binding == null) return;

        String nama = binding.etRegNama.getText().toString().trim();
        String email = binding.etRegEmail.getText().toString().trim();
        String password = binding.etRegPassword.getText().toString().trim();

        if (nama.isEmpty()) {
            binding.etRegNama.setError("Nama tidak boleh kosong");
            return;
        }

        if (email.isEmpty()) {
            binding.etRegEmail.setError("Email tidak boleh kosong");
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etRegEmail.setError("Format email tidak valid");
            return;
        }

        if (password.isEmpty()) {
            binding.etRegPassword.setError("Password tidak boleh kosong");
            return;
        } else if (password.length() < 8) {
            binding.etRegPassword.setError("Password minimal 8 karakter");
            return;
        }

        if (db == null) {
            Toast.makeText(getContext(), "Database tidak siap", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = UUID.randomUUID().toString();
        Map<String, Object> user = new HashMap<>();
        user.put("id", userId);
        user.put("nama", nama);
        user.put("email", email);
        user.put("password", password);
        user.put("profile_photo", encodedImage);
        user.put("createdAt", FieldValue.serverTimestamp());

        binding.btnRegisterSubmit.setEnabled(false);
        binding.btnRegisterSubmit.setText("Mendaftar...");

        db.collection("users")
                .document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    if (isAdded() && binding != null) {
                        Toast.makeText(getContext(), "Registrasi Berhasil!", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(this).navigate(R.id.action_RegisterFragment_to_WelcomeFragment);
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded() && binding != null) {
                        binding.btnRegisterSubmit.setEnabled(true);
                        binding.btnRegisterSubmit.setText("DAFTAR SEKARANG");
                        Toast.makeText(getContext(), "Gagal: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
