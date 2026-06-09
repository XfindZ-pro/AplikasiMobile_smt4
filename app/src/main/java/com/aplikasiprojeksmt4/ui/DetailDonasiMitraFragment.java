package com.aplikasiprojeksmt4.ui;

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

public class DetailDonasiMitraFragment extends Fragment {

    private FragmentDetailDonasiMitraBinding binding;
    private Program program;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            program = (Program) getArguments().getSerializable("program");
        }
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
            Toast.makeText(getContext(), "Fitur Edit Program", Toast.LENGTH_SHORT).show();
        });

        binding.btnHapusProgram.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Fitur Hapus Program", Toast.LENGTH_SHORT).show();
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
