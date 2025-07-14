package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.datamodel.Instructor;

import java.util.List;

public class InstructorAdapter extends RecyclerView.Adapter<InstructorAdapter.InstructorViewHolder> {
    private List<Instructor> instructors;
    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(Instructor instructor);
    }
    public InstructorAdapter(List<Instructor> instructors, OnItemClickListener listener) {
        this.instructors = instructors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InstructorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.instructor_item_layout, parent, false);
        return new InstructorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructorViewHolder holder, int position) {
        Instructor instructor = instructors.get(position);
        holder.instructorNameTextView.setText(instructor.getName());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null && instructor != null) {
                listener.onItemClick(instructor);
            }
        });
    }

    @Override
    public int getItemCount() {
        return instructors.size();
    }

    public static class InstructorViewHolder extends RecyclerView.ViewHolder {
        TextView instructorNameTextView;

        public InstructorViewHolder(@NonNull View itemView) {
            super(itemView);
            instructorNameTextView = itemView.findViewById(R.id.instructorNameTextView);
        }
    }
}
