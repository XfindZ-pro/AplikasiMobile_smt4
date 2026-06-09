package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.FragmentFormDonasiBarangBinding;

public class FormDonasiBarangFragment extends Fragment {

    private FragmentFormDonasiBarangBinding binding;
    private String selectedKondisi = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFormDonasiBarangBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.btnKondisiBaru.setOnClickListener(v -> selectKondisi("Baru"));
        binding.btnKondisiSangatBaik.setOnClickListener(v -> selectKondisi("Sangat Baik"));
        binding.btnKondisiBaik.setOnClickListener(v -> selectKondisi("Baik"));

        binding.btnJadwalkan.setOnClickListener(v -> {
            if (binding.rbDropPoint.isChecked()) {
                Navigation.findNavController(v).navigate(R.id.action_FormDonasiBarangFragment_to_DropPointFragment);
            } else {
                Navigation.findNavController(v).navigate(R.id.action_FormDonasiBarangFragment_to_AlamatPenjemputanFragment);
            }
        });
    }

    private void selectKondisi(String kondisi) {
        selectedKondisi = kondisi;
        // Reset styles
        binding.btnKondisiBaru.setStrokeColorResource(R.color.text_secondary);
        binding.btnKondisiSangatBaik.setStrokeColorResource(R.color.text_secondary);
        binding.btnKondisiBaik.setStrokeColorResource(R.color.text_secondary);

        // Highlight selected
        if (kondisi.equals("Baru")) {
            binding.btnKondisiBaru.setStrokeColorResource(R.color.primary_purple);
        } else if (kondisi.equals("Sangat Baik")) {
            binding.btnKondisiSangatBaik.setStrokeColorResource(R.color.primary_purple);
        } else if (kondisi.equals("Baik")) {
            binding.btnKondisiBaik.setStrokeColorResource(R.color.primary_purple);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
