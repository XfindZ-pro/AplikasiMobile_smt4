package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentTambahProgramBinding;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TambahProgramFragment extends Fragment {

    private FragmentTambahProgramBinding binding;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTambahProgramBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.btnBuatProgram.setOnClickListener(v -> simpanProgram());
    }

    private void simpanProgram() {
        String nama = binding.etNamaProgram.getText().toString().trim();
        String organisasi = binding.etOrganisasi.getText().toString().trim();
        String wilayah = binding.etWilayah.getText().toString().trim();
        String target = binding.etTarget.getText().toString().trim();
        String deskripsi = binding.etDeskripsi.getText().toString().trim();
        
        String tipe = binding.rbDana.isChecked() ? "Dana" : "Barang";

        if (TextUtils.isEmpty(nama) || TextUtils.isEmpty(organisasi) || TextUtils.isEmpty(wilayah) || 
            TextUtils.isEmpty(target) || TextUtils.isEmpty(deskripsi)) {
            Toast.makeText(getContext(), "Harap isi semua field", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnBuatProgram.setEnabled(false);
        binding.btnBuatProgram.setText("Menyimpan...");

        Map<String, Object> program = new HashMap<>();
        program.put("nama", nama);
        program.put("organisasi", organisasi);
        program.put("wilayah", wilayah);
        program.put("tipe", tipe);
        program.put("target", target);
        program.put("deskripsi", deskripsi);
        program.put("status", "Aktif");
        program.put("terkumpul", 0);
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
