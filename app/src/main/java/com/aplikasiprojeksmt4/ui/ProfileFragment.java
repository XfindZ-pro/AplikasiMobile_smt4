package com.aplikasiprojeksmt4.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentProfileBinding;
import com.aplikasiprojeksmt4.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SessionManager sessionManager;
    private FirebaseFirestore db;
    private ListenerRegistration userListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        sessionManager = new SessionManager(requireContext());
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadUserData();

        binding.llDataDiri.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_DataDiriFragment)
        );

        binding.llRiwayat.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_HistoryFragment)
        );

        binding.llSertifikat.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_SertifDonasiFragment)
        );

        binding.llLaporanRealTime.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_LaporanRealTimeFragment)
        );

        binding.llAdministrator.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Halaman Administrator", Toast.LENGTH_SHORT).show();
        });

        binding.btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(requireActivity(), SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void loadUserData() {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            if (binding != null) {
                binding.tvProfileName.setText(sessionManager.getUsername());
                binding.tvProfileEmail.setText(sessionManager.getEmail());
            }
            return;
        }

        userListener = db.collection("users").document(userId).addSnapshotListener((value, error) -> {
            if (binding == null || !isAdded()) return;

            if (value != null && value.exists()) {
                String nama = value.getString("nama");
                String email = value.getString("email");
                String photoUrl = value.getString("profile_photo");
                String role = value.getString("role");

                if (role == null) {
                    Map<String, Object> updateRole = new HashMap<>();
                    updateRole.put("role", "user");
                    db.collection("users").document(userId).update(updateRole);
                    role = "user";
                }

                binding.tvProfileName.setText(nama != null ? nama : "User");
                binding.tvProfileEmail.setText(email != null ? email : "email@example.com");

                if ("admin".equals(role)) {
                    binding.llAdministrator.setVisibility(View.VISIBLE);
                    binding.viewSeparatorAdmin.setVisibility(View.VISIBLE);
                } else {
                    binding.llAdministrator.setVisibility(View.GONE);
                    binding.viewSeparatorAdmin.setVisibility(View.GONE);
                }

                if (photoUrl != null && !photoUrl.isEmpty()) {
                    Glide.with(this)
                            .load(photoUrl)
                            .circleCrop()
                            .placeholder(R.drawable.group_2)
                            .into(binding.ivProfilePicture);
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
