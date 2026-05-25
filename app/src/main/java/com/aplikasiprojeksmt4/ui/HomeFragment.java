package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentHomeBinding;
import com.aplikasiprojeksmt4.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SessionManager sessionManager;
    private FirebaseFirestore db;
    private ListenerRegistration userListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        sessionManager = new SessionManager(requireContext());
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listenToUserData();

        // Navigasi ke halaman notifikasi
        binding.flNotification.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_HomeFragment_to_NotifikasiPageFragment)
        );

        // Navigasi ke halaman Donasi Uang
        binding.btnDonasiUang.setOnClickListener(v ->
            Navigation.findNavController(v).navigate(R.id.action_HomeFragment_to_DonasiUangFragment)
        );

        // Navigasi ke halaman Donasi Barang
        binding.btnDonasiBarang.setOnClickListener(v ->
            Navigation.findNavController(v).navigate(R.id.action_HomeFragment_to_DonasiBarangFragment)
        );
    }

    private void listenToUserData() {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            if (binding != null) {
                binding.tvUserName.setText(sessionManager.getUsername());
            }
            return;
        }

        userListener = db.collection("users").document(userId).addSnapshotListener((value, error) -> {
            if (binding == null || !isAdded()) return;

            if (value != null && value.exists()) {
                String nama = value.getString("nama");
                String photoUrl = value.getString("profile_photo");

                binding.tvUserName.setText(nama != null ? nama : "User");

                if (photoUrl != null && !photoUrl.isEmpty()) {
                    ImageView profileIv = binding.ivProfilePictureHome;
                    if (profileIv != null) {
                        Glide.with(this)
                                .load(photoUrl)
                                .circleCrop()
                                .placeholder(R.drawable.group_2)
                                .into(profileIv);
                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userListener != null) {
            userListener.remove();
        }
        binding = null;
    }
}
