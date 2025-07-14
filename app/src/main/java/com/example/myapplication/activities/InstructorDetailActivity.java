package com.example.myapplication.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.repository.InstructorRepository;
import com.example.myapplication.viewmodel.InstructorDetailViewModel;
import com.example.myapplication.viewmodel.TermDetailViewModel;

import java.time.format.DateTimeFormatter;

public class InstructorDetailActivity extends BaseActivity {
    private TextView titleTextView;
    private TextView instructorNameTextView;
    private TextView instructorPhoneTextView;
    private TextView instructorEmailTextView;
    private Button instructorEditButton;
    private Button instructorDeleteButton;
    private InstructorRepository instructorRepository;
    private InstructorDetailViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_detail);
        titleTextView = findViewById(R.id.instructorDetailPageTitleTextView);
        instructorNameTextView = findViewById(R.id.instructorDetailNameTextView);
        instructorPhoneTextView = findViewById(R.id.instructorDetailPhoneTextView);
        instructorEmailTextView = findViewById(R.id.instructorDetailEmailTextView);
        instructorEditButton = findViewById(R.id.instructorDetailEditButton);
        instructorDeleteButton = findViewById(R.id.instructorDetailDeleteButton);

        instructorRepository = new InstructorRepository(this);

        int instructorId = getIntent().getIntExtra("instructorId", -1);

        viewModel = new ViewModelProvider(this).get(InstructorDetailViewModel.class);

        viewModel.getInstructor().observe(this, instructor -> {
            if (instructor != null) {
                instructorNameTextView.setText(instructor.getName());
                instructorPhoneTextView.setText(instructor.getPhone());
                instructorEmailTextView.setText(instructor.getEmail());
            }
        });

        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(InstructorDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        if (instructorId != -1) {
            viewModel.loadInstructor(instructorId);
        }
        else {
            //error handling
            titleTextView.setText("Error: No Term ID provided");
        }

        instructorEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewModel.getInstructor().getValue() != null) {
                    Intent intent = new Intent(InstructorDetailActivity.this, InstructorFormActivity.class);
                    intent.putExtra("instructorId", instructorId);
                    startActivity(intent);
                }
            }
        });

        instructorDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewModel.getInstructor().getValue() != null) {
                    new AlertDialog.Builder(InstructorDetailActivity.this)
                            .setTitle("Confirm Delete")
                            .setMessage("Are you sure you want to delete this term?")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                // User confirmed, proceed with deletion
                                instructorRepository.deleteInstructor(viewModel.getInstructor().getValue());
                                // Optionally, provide feedback or navigate back
                                Toast.makeText(InstructorDetailActivity.this, "Instructor deleted.", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(InstructorDetailActivity.this, MainActivity.class);
                                startActivity(intent);
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                // User cancelled, do nothing
                                dialog.dismiss();
                            })
                            .show();
                } else {
                    Toast.makeText(InstructorDetailActivity.this, "Instructor data not loaded yet.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int instructorId = getIntent().getIntExtra("instructorId", -1);
        if (instructorId != -1) {
            viewModel.loadInstructor(instructorId);
        } else {
            // Handle error if courseId is not available
            titleTextView.setText("Error: No Course ID provided");
        }
    }
}
