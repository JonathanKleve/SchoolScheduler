package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.datamodel.Assessment;
import java.util.List;

public class AssessmentAdapter extends RecyclerView.Adapter<AssessmentAdapter.AssessmentViewHolder> {

    private List<Assessment> assessments;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Assessment assessment);
    }

    public AssessmentAdapter(List<Assessment> assessments, OnItemClickListener listener) {
        this.assessments = assessments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AssessmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.assessment_item_layout, parent, false);
        return new AssessmentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AssessmentViewHolder holder, int position) {
        Assessment assessment = assessments.get(position);
        holder.assessmentTitleTextView.setText(assessment.getTitle());

        holder.itemView.setOnClickListener(v ->{
            if (listener != null && assessment != null){
                listener.onItemClick(assessment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return assessments.size();
    }

    public static class AssessmentViewHolder extends RecyclerView.ViewHolder {
        TextView assessmentTitleTextView;

        public AssessmentViewHolder(@NonNull View itemView) {
            super(itemView);
            assessmentTitleTextView = itemView.findViewById(R.id.assessmentTitleTextView);
        }
    }
}