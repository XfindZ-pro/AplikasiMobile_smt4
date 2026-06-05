package com.aplikasiprojeksmt4.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.aplikasiprojeksmt4.databinding.FragmentTambahProgramBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TambahProgramFragment extends Fragment {

    private FragmentTambahProgramBinding binding;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private Uri imageUri;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    binding.ivProgramPreview.setImageURI(imageUri);
                    binding.ivProgramPreview.setVisibility(View.VISIBLE);
                    binding.llUploadPlaceholder.setVisibility(View.GONE);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTambahProgramBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.cvUploadImage.setOnClickListener(v -> pickImage());

        binding.btnBuatProgram.setOnClickListener(v -> validateAndSave());
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void validateAndSave() {
        String nama = binding.etNamaProgram.getText().toString().trim();
        String organisasi = binding.etOrganisasi.getText().toString().trim();
        String wilayah = binding.etWilayah.getText().toString().trim();
        String status = binding.etStatus.getText().toString().trim();
        String target = binding.etTarget.getText().toString().trim();
        String deskripsi = binding.etDeskripsi.getText().toString().trim();
        
        String tipe = binding.rbDana.isChecked() ? "Dana" : "Barang";

        if (TextUtils.isEmpty(nama) || TextUtils.isEmpty(organisasi) || TextUtils.isEmpty(wilayah) || 
            TextUtils.isEmpty(status) || TextUtils.isEmpty(target) || TextUtils.isEmpty(deskripsi)) {
            Toast.makeText(getContext(), "Harap isi semua field", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(getContext(), "Harap pilih foto program", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadImageAndSave(nama, organisasi, wilayah, tipe, status, target, deskripsi);
    }

    private void uploadImageAndSave(String nama, String organisasi, String wilayah, String tipe, String status, String target, String deskripsi) {
        binding.btnBuatProgram.setEnabled(false);
        binding.btnBuatProgram.setText("Mengunggah Gambar...");

        String fileName = UUID.randomUUID().toString();
        StorageReference ref = storage.getReference().child("program_images/" + fileName);

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveToFirestore(nama, organisasi, wilayah, tipe, status, target, deskripsi, uri.toString());
                }))
                .addOnFailureListener(e -> {
                    binding.btnBuatProgram.setEnabled(true);
                    binding.btnBuatProgram.setText("Buat Program");
                    Toast.makeText(getContext(), "Gagal unggah gambar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveToFirestore(String nama, String organisasi, String wilayah, String tipe, String status, String target, String deskripsi, String imageUrl) {
        binding.btnBuatProgram.setText("Menyimpan Program...");

        FirebaseUser currentUser = auth.getCurrentUser();
        String userId = (currentUser != null) ? currentUser.getUid() : null;

        Map<String, Object> program = new HashMap<>();
        program.put("nama", nama);
        program.put("organisasi", organisasi);
        program.put("wilayah", wilayah);
        program.put("tipe", tipe);
        program.put("status", status);
        program.put("target", target);
        program.put("deskripsi", deskripsi);
        program.put("imageUrl", imageUrl);
        program.put("terkumpul", 0);
        program.put("dibuat_oleh", userId); // Menyimpan UID pembuat
        program.put("created_at", FieldValue.serverTimestamp());

        db.collection("programs")
                .add(program)
                .addOnSuccessListener(documentReference -> {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Program berhasil dibuat!", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).navigateUp();
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        binding.btnBuatProgram.setEnabled(true);
                        binding.btnBuatProgram.setText("Buat Program");
                        Toast.makeText(getContext(), "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
