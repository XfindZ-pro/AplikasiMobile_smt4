package com.aplikasiprojeksmt4.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aplikasiprojeksmt4.adapters.ProgramAdapter;
import com.aplikasiprojeksmt4.databinding.PageProgramBinding;
import com.aplikasiprojeksmt4.models.Program;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class PageProgramFragment extends Fragment {

    private PageProgramBinding binding;
    private FirebaseFirestore db;
    private ProgramAdapter adapter;
    private List<Program> programList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PageProgramBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        setupRecyclerView();
        loadPrograms();
    }

    private void setupRecyclerView() {
        adapter = new ProgramAdapter(programList);
        binding.rvProgram.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvProgram.setAdapter(adapter);
    }

    private void loadPrograms() {
        db.collection("programs")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        programList.clear();
                        List<Program> docs = value.toObjects(Program.class);
                        for (int i = 0; i < docs.size(); i++) {
                            Program p = docs.get(i);
                            p.setId(value.getDocuments().get(i).getId());
                            programList.add(p);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
