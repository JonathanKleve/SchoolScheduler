package com.example.myapplication.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.datamodel.Course;
import com.example.myapplication.repository.CourseRepository;
import com.example.myapplication.repository.TermRepository;
import com.example.myapplication.viewmodel.TermDetailViewModel;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class TermDetailActivity extends BaseActivity {
    private TermDetailViewModel viewModel;
    private TextView titleTextView;
    private TextView termTitleTextView;
    private TextView termStartTextView;
    private TextView termEndTextView;
    private Button termEditButton;
    private Button termDeleteButton;
    private Button termCoursesButton;
    private TermRepository termRepository;
    private CourseRepository courseRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_detail);

        titleTextView = findViewById(R.id.termDetailPageTitleTextView);
        termTitleTextView = findViewById(R.id.termDetailTitleTextView);
        termStartTextView = findViewById(R.id.termDetailStartTextView);
        termEndTextView = findViewById(R.id.termDetailEndTextView);
        termEditButton = findViewById(R.id.termDetailEditButton);
        termDeleteButton = findViewById(R.id.termDetailDeleteButton);
        termCoursesButton = findViewById(R.id.termDetailCoursesButton);

        termRepository = new TermRepository(this);
        courseRepository = new CourseRepository(this);

        int termId = getIntent().getIntExtra("termId", -1);

        viewModel = new ViewModelProvider(this).get(TermDetailViewModel.class);
        
        viewModel.getTerm().observe(this, term -> {
            if (term != null) {
                termTitleTextView.setText(term.getTitle());
                // Format dates
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String startDate = term.getStart().format(formatter);
                String endDate = term.getEnd().format(formatter);
                termStartTextView.setText(startDate);
                termEndTextView.setText(endDate);
            }
        });

        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(TermDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        if (termId != -1) {
            viewModel.loadTerm(termId);
        }
        else {
            //error handling
            titleTextView.setText("Error: No Term ID provided");
        }

        termEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewModel.getTerm().getValue() != null) {
                    Intent intent = new Intent(TermDetailActivity.this, TermFormActivity.class);
                    intent.putExtra("termId", termId);
                    startActivity(intent);
                }
            }
        });

        termDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewModel.getTerm().getValue() != null) {
                    courseRepository.loadCoursesByTermId(viewModel.getTerm().getValue().getId());
                    LiveData<List<Course>> courseLiveData = courseRepository.getCoursesByTermId();
                    courseLiveData.observe(TermDetailActivity.this, associatedCourses -> {
                        if (associatedCourses != null && !associatedCourses.isEmpty()) {
                            new AlertDialog.Builder(TermDetailActivity.this)
                                    .setTitle("Cannot Delete")
                                    .setMessage("This term has associated courses and cannot be deleted.")
                                    .show();
                        } else {
                            new AlertDialog.Builder(TermDetailActivity.this)
                                    .setTitle("Confirm Delete")
                                    .setMessage("Are you sure you want to delete this term?")
                                    .setPositiveButton("Delete", (dialog, which) -> {
                                        // User confirmed, proceed with deletion
                                        termRepository.deleteTerm(viewModel.getTerm().getValue());
                                        // Optionally, provide feedback or navigate back
                                        Toast.makeText(TermDetailActivity.this, "Term deleted.", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(TermDetailActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    })
                                    .setNegativeButton("Cancel", (dialog, which) -> {
                                        // User cancelled, do nothing
                                        dialog.dismiss();
                                    })
                                    .show();
                        }
                        courseLiveData.removeObservers(TermDetailActivity.this);
                    });
                } else {
                    Toast.makeText(TermDetailActivity.this, "Term data not loaded yet.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        termCoursesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewModel.getTerm().getValue() != null) {
                    Intent intent = new Intent(TermDetailActivity.this, ListActivity.class);
                    intent.putExtra("termId", termId);
                    startActivity(intent);
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        int termId = getIntent().getIntExtra("termId", -1);
        if (termId != -1) {
            viewModel.loadTerm(termId);
        } else {
            // Handle error if courseId is not available
            titleTextView.setText("Error: No Term ID provided");
        }
    }
}
