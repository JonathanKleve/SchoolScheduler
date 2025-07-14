package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.datamodel.Term;

import java.util.List;

public class TermAdapter extends RecyclerView.Adapter<TermAdapter.TermViewHolder> {
    private List<Term> terms;
    private OnItemClickListener listener;
    public interface OnItemClickListener { // Define interface
        void onItemClick(Term term);
    }

    public TermAdapter(List<Term> terms, OnItemClickListener listener) {
        this.terms = terms;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TermViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.term_item_layout, parent, false);
        return new TermViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TermViewHolder holder, int position) {
        Term term = terms.get(position);
        holder.termTitleTextView.setText(term.getTitle());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null && term != null) {
                listener.onItemClick(term);
            }
        });
    }

    @Override
    public int getItemCount() {
        return terms.size();
    }

    public static class TermViewHolder extends RecyclerView.ViewHolder {
        TextView termTitleTextView;

        public TermViewHolder(@NonNull View itemView) {
            super(itemView);
            termTitleTextView = itemView.findViewById(R.id.termTitleTextView);
        }
    }

}
