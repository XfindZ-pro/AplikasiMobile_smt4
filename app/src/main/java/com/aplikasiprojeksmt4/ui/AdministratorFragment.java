package com.aplikasiprojeksmt4.ui;

import android.app.AlertDialog;
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
import com.aplikasiprojeksmt4.R;
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
    private boolean isUploading = false;
    private String cloudVersion = "Unknown";

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

        // Tombol Kembali
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Setup Bottom Navigation untuk Administrator
        setupBottomNavigation();

        // Tombol Status Cloud di Pojok Kanan Atas (Beranda Tab)
        binding.btnCloudStatus.setOnClickListener(v -> showCloudStatusDialog());

        // Tombol Rilis Versi Ini (Beranda Tab)
        binding.llRilisUpdate.setOnClickListener(v -> {
            if (!isUploading) {
                showReleaseConfirmation();
            }
        });

        // Muat informasi stats dan versi awal
        loadStats();
        loadCloudVersionInfo();
    }

    private void setupBottomNavigation() {
        binding.adminBottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            
            // Sembunyikan semua layout tab terlebih dahulu
            binding.layoutAdminHome.setVisibility(View.GONE);
            binding.layoutAdminProgram.setVisibility(View.GONE);
            binding.layoutAdminVerifikasi.setVisibility(View.GONE);
            binding.layoutAdminDonatur.setVisibility(View.GONE);

            // Tampilkan layout sesuai tab yang dipilih
            if (id == R.id.adminHome) {
                binding.layoutAdminHome.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.adminProgram) {
                binding.layoutAdminProgram.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.adminVerifikasi) {
                binding.layoutAdminVerifikasi.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.adminDonatur) {
                binding.layoutAdminDonatur.setVisibility(View.VISIBLE);
                return true;
            }
            return false;
        });
    }

    private void loadStats() {
        // Dummy data dashboard admin
        binding.tvTotalDana.setText("Rp 300rb");
    }

    private void loadCloudVersionInfo() {
        db.collection("app_settings").document("update_info")
                .addSnapshotListener((value, error) -> {
                    if (binding == null || !isAdded()) return;
                    if (value != null && value.exists()) {
                        cloudVersion = value.getString("latest_version");
                        // Sinkronisasi status visual icon cloud
                        if (BuildConfig.VERSION_NAME.equals(cloudVersion)) {
                            binding.ivCloudStatus.setAlpha(1.0f);
                        } else {
                            binding.ivCloudStatus.setAlpha(0.5f);
                        }
                    }
                });
    }

    private void showCloudStatusDialog() {
        String msg = "Versi Aplikasi Saat Ini: " + BuildConfig.VERSION_NAME + 
                     "\nVersi di Cloud: " + cloudVersion;
        
        new AlertDialog.Builder(requireContext())
                .setTitle("Status Update Cloud")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showReleaseConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Rilis")
                .setMessage("Apakah Anda yakin ingin merilis versi " + BuildConfig.VERSION_NAME + " ke cloud?")
                .setPositiveButton("Ya, Rilis", (dialog, which) -> uploadApkAndRelease())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void setUploadState(boolean uploading) {
        isUploading = uploading;
        if (binding == null) return;

        if (uploading) {
            binding.llRilisUpdate.setEnabled(false);
            binding.pbUpload.setVisibility(View.VISIBLE);
            binding.ivArrowRilis.setVisibility(View.GONE);
            binding.tvRilisText.setText("Proses...");
        } else {
            binding.llRilisUpdate.setEnabled(true);
            binding.pbUpload.setVisibility(View.GONE);
            binding.ivArrowRilis.setVisibility(View.VISIBLE);
            binding.tvRilisText.setText("Rilis\nVersi Ini");
        }
    }

    private void uploadApkAndRelease() {
        if (getContext() == null) return;
        
        setUploadState(true);
        Toast.makeText(getContext(), "Sedang mengunggah file APK...", Toast.LENGTH_SHORT).show();
        
        String apkPath = getContext().getPackageCodePath();
        File apkFile = new File(apkPath);
        Uri fileUri = Uri.fromFile(apkFile);

        StorageReference storageRef = storage.getReference().child("releases/donasiku_latest.apk");

        storageRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        updateFirestoreInfo(uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    setUploadState(false);
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Gagal: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateFirestoreInfo(String downloadUrl) {
        String currentVersion = BuildConfig.VERSION_NAME;
        Map<String, Object> update = new HashMap<>();
        update.put("latest_version", currentVersion);
        update.put("download_url", downloadUrl);
        
        db.collection("app_settings").document("update_info")
                .set(update, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    setUploadState(false);
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Berhasil Rilis Versi " + currentVersion, Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    setUploadState(false);
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Gagal update Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
