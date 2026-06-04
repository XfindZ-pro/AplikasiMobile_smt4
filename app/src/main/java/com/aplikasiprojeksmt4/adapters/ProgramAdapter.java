package com.aplikasiprojeksmt4.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aplikasiprojeksmt4.R;
import com.aplikasiprojeksmt4.databinding.ItemProgramAdminBinding;
import com.aplikasiprojeksmt4.models.Program;
import com.github.bumptech.glide.Glide;
import java.util.List;

public class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.ProgramViewHolder> {

    private List<Program> programs;

    public ProgramAdapter(List<Program> programs) {
        this.programs = programs;
    }

    @NonNull
    @Override
    public ProgramViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProgramAdminBinding binding = ItemProgramAdminBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ProgramViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgramViewHolder holder, int position) {
        Program program = programs.get(position);
        holder.binding.tvNamaProgram.setText(program.getNama());
        holder.binding.tvWilayah.setText(program.getWilayah());
        
        long target = program.getTargetValue();
        long terkumpul = program.getTerkumpul();
        int progress = 0;
        if (target > 0) {
            progress = (int) ((terkumpul * 100) / target);
        }
        
        holder.binding.progressIndicator.setProgress(progress);
        holder.binding.tvTerkumpul.setText("Rp " + String.format("%,d", terkumpul).replace(',', '.') + " terkumpul");
        holder.binding.tvPersentaseTarget.setText(progress + "% dari target");

        if (program.getImageUrl() != null && !program.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(program.getImageUrl())
                    .placeholder(R.drawable.group_2)
                    .into(holder.binding.ivProgramImage);
        } else {
            holder.binding.ivProgramImage.setImageResource(R.drawable.group_2);
        }
    }

    @Override
    public int getItemCount() {
        return programs.size();
    }

    public void setPrograms(List<Program> newPrograms) {
        this.programs = newPrograms;
        notifyDataSetChanged();
    }

    public static class ProgramViewHolder extends RecyclerView.ViewHolder {
        ItemProgramAdminBinding binding;

        public ProgramViewHolder(ItemProgramAdminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
