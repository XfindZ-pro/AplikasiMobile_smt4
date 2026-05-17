package com.aplikasiprojeksmt4.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.aplikasiprojeksmt4.databinding.FragmentDataDiriBinding;
import com.aplikasiprojeksmt4.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class DataDiriFragment extends Fragment {

    private FragmentDataDiriBinding binding;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private SessionManager sessionManager;
    private Uri imageUri;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    if (binding != null) {
                        binding.ivProfilePicture.setImageURI(imageUri);
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDataDiriBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance("gs://test-rizha.firebasestorage.app");
        sessionManager = new SessionManager(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadUserData();

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.tvGantiFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        binding.btnSimpan.setOnClickListener(v -> saveChanges());
    }

    private void loadUserData() {
        String userId = sessionManager.getUserId();
        if (userId == null) return;

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (binding == null || !isAdded()) return;
                    
                    if (documentSnapshot.exists()) {
                        String nama = documentSnapshot.getString("nama");
                        String email = documentSnapshot.getString("email");
                        String telepon = documentSnapshot.getString("no_telepon");
                        String alamat = documentSnapshot.getString("alamat");
                        String photoUrl = documentSnapshot.getString("profile_photo");

                        binding.etNama.setText(nama != null ? nama : "");
                        binding.etEmail.setText(email != null ? email : "");
                        binding.etTelepon.setText(telepon != null ? telepon : "");
                        binding.etAlamat.setText(alamat != null ? alamat : "");

                        if (photoUrl != null && !photoUrl.isEmpty()) {
                            Glide.with(this).load(photoUrl).into(binding.ivProfilePicture);
                        }
                    }
                });
    }

    private void saveChanges() {
        String userId = sessionManager.getUserId();
        if (userId == null) return;

        binding.btnSimpan.setEnabled(false);
        binding.btnSimpan.setText("Menyimpan...");

        if (imageUri != null) {
            uploadImageAndSaveData(userId);
        } else {
            saveDataToFirestore(userId, null);
        }
    }

    private void uploadImageAndSaveData(String userId) {
        StorageReference ref = storage.getReference().child("profile_photos/" + userId + ".jpg");
        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    if (binding != null) {
                        saveDataToFirestore(userId, uri.toString());
                    }
                }))
                .addOnFailureListener(e -> {
                    if (binding != null) {
                        binding.btnSimpan.setEnabled(true);
                        binding.btnSimpan.setText("Simpan Perubahan");
                        Toast.makeText(getContext(), "Gagal upload foto", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveDataToFirestore(String userId, @Nullable String photoUrl) {
        if (binding == null) return;

        String nama = binding.etNama.getText().toString().trim();
        String telepon = binding.etTelepon.getText().toString().trim();
        String alamat = binding.etAlamat.getText().toString().trim();

        Map<String, Object> updates = new HashMap<>();
        updates.put("nama", nama);
        updates.put("no_telepon", telepon);
        updates.put("alamat", alamat);
        if (photoUrl != null) {
            updates.put("profile_photo", photoUrl);
        }

        db.collection("users").document(userId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    if (binding != null) {
                        sessionManager.saveUsername(nama);
                        Toast.makeText(getContext(), "Data berhasil diperbarui", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(binding.getRoot()).navigateUp();
                    }
                })
                .addOnFailureListener(e -> {
                    if (binding != null) {
                        binding.btnSimpan.setEnabled(true);
                        binding.btnSimpan.setText("Simpan Perubahan");
                        Toast.makeText(getContext(), "Gagal memperbarui data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
