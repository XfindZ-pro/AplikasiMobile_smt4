package com.aplikasiprojeksmt4.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.BuildConfig;
import com.aplikasiprojeksmt4.databinding.FragmentAdministratorBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AdministratorFragment extends Fragment {

    private FragmentAdministratorBinding binding;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdministratorBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.llRilisUpdate.setOnClickListener(v -> uploadApkAndRelease());
    }

    private void uploadApkAndRelease() {
        if (getContext() == null) return;
        
        Toast.makeText(getContext(), "Sedang memproses rilis update...", Toast.LENGTH_SHORT).show();
        
        // 1. Dapatkan path APK dari aplikasi yang sedang berjalan
        String apkPath = getContext().getPackageCodePath();
        File apkFile = new File(apkPath);
        Uri fileUri = Uri.fromFile(apkFile);

        // 2. Tentukan lokasi penyimpanan di Firebase Storage
        // Kita gunakan nama tetap agar file lama tertimpa atau bisa gunakan versi dalam nama
        StorageReference storageRef = storage.getReference().child("releases/donasiku_latest.apk");

        // 3. Proses Upload
        storageRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // 4. Ambil URL unduhan setelah upload berhasil
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        updateFirestoreInfo(uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Gagal upload APK: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateFirestoreInfo(String downloadUrl) {
        String currentVersion = BuildConfig.VERSION_NAME;
        
        Map<String, Object> update = new HashMap<>();
        update.put("latest_version", currentVersion);
        update.put("download_url", downloadUrl);
        
        // 5. Update Firestore agar terdeteksi oleh UpdateManager di user lain
        db.collection("app_settings").document("update_info")
                .set(update, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Update berhasil dirilis! Versi: " + currentVersion, Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Gagal update info Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
