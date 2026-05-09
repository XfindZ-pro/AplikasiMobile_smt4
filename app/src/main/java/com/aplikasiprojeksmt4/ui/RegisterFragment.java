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
import androidx.navigation.Navigation;

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

    // Launcher untuk memilih gambar dari galeri
    private final ActivityResultLauncher<String> getContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
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
        
        db = FirebaseFirestore.getInstance();

        // Klik foto untuk pilih gambar
        binding.ivRegProfile.setOnClickListener(v -> getContent.launch("image/*"));

        binding.btnRegisterSubmit.setOnClickListener(v -> {
            registerUser();
        });
    }

    private String encodeImage(Uri uri) throws Exception {
        InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Kompres gambar agar tidak terlalu besar untuk Firestore (limit 1MB)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        byte[] bytes = outputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void registerUser() {
        String nama = binding.etRegNama.getText().toString().trim();
        String email = binding.etRegEmail.getText().toString().trim();
        String password = binding.etRegPassword.getText().toString().trim();

        // Validasi Nama
        if (nama.isEmpty()) {
            binding.etRegNama.setError("Nama tidak boleh kosong");
            return;
        }

        // Validasi Email
        if (email.isEmpty()) {
            binding.etRegEmail.setError("Email tidak boleh kosong");
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etRegEmail.setError("Format email tidak valid");
            return;
        }

        // Validasi Password
        if (password.isEmpty()) {
            binding.etRegPassword.setError("Password tidak boleh kosong");
            return;
        } else if (password.length() < 8) {
            binding.etRegPassword.setError("Password minimal 8 karakter");
            return;
        }

        // Simpan ke Firestore
        String userId = UUID.randomUUID().toString();
        Map<String, Object> user = new HashMap<>();
        user.put("id", userId);
        user.put("nama", nama);
        user.put("email", email);
        user.put("password", password); // Catatan: Sebaiknya gunakan Firebase Auth untuk keamanan
        user.put("profile_photo", encodedImage); // Foto dalam bentuk Base64 (bits)
        user.put("createdAt", FieldValue.serverTimestamp()); // Timestamp waktu pendaftaran

        binding.btnRegisterSubmit.setEnabled(false);
        binding.btnRegisterSubmit.setText("Mendaftar...");

        db.collection("users")
                .document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Registrasi Berhasil!", Toast.LENGTH_SHORT).show();
                    // Kembali ke halaman Welcome
                    Navigation.findNavController(requireView()).navigate(R.id.action_RegisterFragment_to_WelcomeFragment);
                })
                .addOnFailureListener(e -> {
                    binding.btnRegisterSubmit.setEnabled(true);
                    binding.btnRegisterSubmit.setText("DAFTAR SEKARANG");
                    Toast.makeText(getContext(), "Gagal: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
