package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aplikasiprojeksmt4.databinding.FragmentProfileMitraBinding;
import com.aplikasiprojeksmt4.models.Mitra;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileMitraFragment extends Fragment {

    private FragmentProfileMitraBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileMitraBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUserProfile();
        setupClickListeners();
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) return;

        // 1. Set data dasar dari Firebase Auth
        binding.tvProfileEmail.setText(currentUser.getEmail());
        if (currentUser.getDisplayName() != null) {
            binding.tvProfileName.setText(currentUser.getDisplayName());
        }
        if (currentUser.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(currentUser.getPhotoUrl())
                    .placeholder(android.R.drawable.ic_menu_report_image)
                    .into(binding.ivProfileAvatar);
        }

        // 2. Ambil data profil dari koleksi 'users' untuk Nama Lengkap/Display Name yang tersinkron
        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (isAdded() && documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("nama");
                        String foto = documentSnapshot.getString("fotoUrl");
                        if (name != null) binding.tvProfileName.setText(name);
                        if (foto != null && currentUser.getPhotoUrl() == null) {
                            Glide.with(this).load(foto).into(binding.ivProfileAvatar);
                        }
                    }
                });

        // 3. Ambil data spesifik mitra dari koleksi 'daftar_mitra'
        db.collection("daftar_mitra").document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (isAdded() && documentSnapshot.exists()) {
                        Mitra mitra = documentSnapshot.toObject(Mitra.class);
                        if (mitra != null) {
                            // Data mitra berhasil dimuat (rekening, alamat, dokumen ada di objek mitra)
                            Log.d("ProfileMitra", "Data Mitra: " + mitra.getNamaBank() + " - " + mitra.getNomorRekening());
                        }
                    } else {
                        // Jika data belum ada, kita bisa inisialisasi dokumen baru
                        Log.d("ProfileMitra", "Dokumen daftar_mitra belum tersedia untuk user ini");
                    }
                })
                .addOnFailureListener(e -> Log.e("ProfileMitra", "Error loading daftar_mitra", e));
    }

    private void setupClickListeners() {
        binding.btnEditProfile.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Fitur Edit Profil segera hadir", Toast.LENGTH_SHORT).show();
        });

        binding.btnRekening.setOnClickListener(v -> {
            // Tampilkan dialog atau navigasi ke detail rekening bank mitra
            Toast.makeText(getContext(), "Kelola Rekening Bank", Toast.LENGTH_SHORT).show();
        });

        binding.btnNotif.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Pengaturan Notifikasi", Toast.LENGTH_SHORT).show();
        });

        binding.btnLegal.setOnClickListener(v -> {
            // Tampilkan daftar dokumen legal dari koleksi daftar_mitra
            Toast.makeText(getContext(), "Lihat Dokumen Legal", Toast.LENGTH_SHORT).show();
        });

        binding.btnHelp.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Pusat Bantuan", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
