package com.aplikasiprojeksmt4.ui;

import android.app.AlertDialog;
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
import com.aplikasiprojeksmt4.databinding.FragmentDetailDonasiMitraBinding;
import com.aplikasiprojeksmt4.models.Program;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetailDonasiMitraFragment extends Fragment {

    private FragmentDetailDonasiMitraBinding binding;
    private Program program;
    private FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            program = (Program) getArguments().getSerializable("program");
        }
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailDonasiMitraBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (program != null) {
            displayProgramDetails();
        }

        binding.btnBackDetail.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.btnTarikDana.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Fitur Tarik Dana", Toast.LENGTH_SHORT).show();
        });

        binding.btnEditProgram.setOnClickListener(v -> {
            if (program != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("program", program);
                Navigation.findNavController(v).navigate(R.id.action_DetailDonasiMitraFragment_to_EditProgramFragment, bundle);
            }
        });

        binding.btnHapusProgram.setOnClickListener(v -> {
            if (program != null) {
                showDeleteConfirmation();
            }
        });
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Hapus Program")
                .setMessage("Apakah Anda yakin ingin menghapus program ini?")
                .setPositiveButton("Hapus", (dialog, which) -> deleteProgram())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteProgram() {
        if (program.getId() == null) {
            Toast.makeText(getContext(), "ID Program tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("programs").document(program.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Program berhasil dihapus", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Gagal menghapus program: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void displayProgramDetails() {
        binding.tvDetailNama.setText(program.getNama());
        binding.tvDetailOrganisasi.setText(program.getDibuat_oleh_nama() != null ? program.getDibuat_oleh_nama() : "Mitra");
        
        if (program.getNama() != null && !program.getNama().isEmpty()) {
            binding.tvInisial.setText(program.getNama().substring(0, Math.min(3, program.getNama().length())).toUpperCase());
        }

        long terkumpul = program.getTerkumpul();
        long target = program.getTargetValue();
        
        binding.tvDetailTerkumpul.setText("Rp " + String.format("%,d", terkumpul).replace(',', '.'));
        
        int progress = 0;
        if (target > 0) {
            progress = (int) ((terkumpul * 100) / target);
        }
        
        binding.progressDetail.setProgress(progress);
        binding.tvTargetDetail.setText(progress + "% tercapai - Target Rp " + String.format("%,d", target).replace(',', '.'));
        
        binding.tvTentangProgram.setText(program.getDeskripsi());

        if (program.getImageUrl() != null && !program.getImageUrl().isEmpty()) {
            Glide.with(this).load(program.getImageUrl()).into(binding.ivProgramDetail);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
