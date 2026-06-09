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

        // 1. Tampilkan data awal dari Auth (sebagai placeholder)
        binding.tvProfileEmail.setText(currentUser.getEmail());
        if (currentUser.getDisplayName() != null) {
            binding.tvProfileName.setText(currentUser.getDisplayName());
        }

        // 2. Ambil data utama dari tabel 'users' di Firestore (Prioritas Foto Profil & Nama)
        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (isAdded() && documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("nama");
                        String fotoUrl = documentSnapshot.getString("fotoUrl");

                        // Update Nama dari tabel users
                        if (name != null) {
                            binding.tvProfileName.setText(name);
                        }

                        // Update Foto Profil dari tabel users (Prioritas Utama)
                        if (fotoUrl != null && !fotoUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(fotoUrl)
                                    .circleCrop() // Memastikan foto melingkar
                                    .placeholder(android.R.drawable.ic_menu_report_image)
                                    .into(binding.ivProfileAvatar);
                        } else if (currentUser.getPhotoUrl() != null) {
                            // Fallback ke foto dari Auth jika di tabel users kosong
                            Glide.with(this)
                                    .load(currentUser.getPhotoUrl())
                                    .circleCrop()
                                    .into(binding.ivProfileAvatar);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("ProfileMitra", "Gagal memuat data users", e));

        // 3. Ambil data pendukung dari tabel 'daftar_mitra' (Rekening, Alamat, dll)
        db.collection("daftar_mitra").document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (isAdded() && documentSnapshot.exists()) {
                        Mitra mitra = documentSnapshot.toObject(Mitra.class);
                        if (mitra != null) {
                            // Data tambahan mitra bisa diproses di sini
                            Log.d("ProfileMitra", "Data Rekening: " + mitra.getNamaBank());
                        }
                    }
                });
    }

    private void setupClickListeners() {
        binding.btnEditProfile.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Fitur Edit Profil segera hadir", Toast.LENGTH_SHORT).show();
        });

        binding.btnRekening.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Kelola Rekening Bank", Toast.LENGTH_SHORT).show();
        });

        binding.btnNotif.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Pengaturan Notifikasi", Toast.LENGTH_SHORT).show();
        });

        binding.btnLegal.setOnClickListener(v -> {
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
