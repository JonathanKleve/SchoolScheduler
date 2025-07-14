package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.adapters.AssessmentAdapter;
import com.example.myapplication.adapters.CourseAdapter;
import com.example.myapplication.adapters.InstructorAdapter;
import com.example.myapplication.adapters.TermAdapter;
import com.example.myapplication.datamodel.Assessment;
import com.example.myapplication.datamodel.Course;
import com.example.myapplication.datamodel.Instructor;
import com.example.myapplication.datamodel.Term;
import com.example.myapplication.datamodel.TermTestDataGenerator;
import com.example.myapplication.viewmodel.ListViewModel;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends BaseActivity implements AssessmentAdapter.OnItemClickListener, TermAdapter.OnItemClickListener, CourseAdapter.OnItemClickListener, InstructorAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private TextView titleTextView;
    private ListViewModel viewModel;
    private int termId = -1;
    private int selectedItemId = 0;

    private androidx.lifecycle.Observer<List<Term>> termObserver;
    private androidx.lifecycle.Observer<List<Course>> courseObserver;
    private androidx.lifecycle.Observer<List<Assessment>> assessmentObserver;
    private androidx.lifecycle.Observer<List<Instructor>> instructorObserver;
    private androidx.lifecycle.Observer<List<Course>> coursesByTermIdObserver;
    private Boolean useTestData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView = findViewById(R.id.listRecyclerView);
        titleTextView = findViewById(R.id.listTitleTextView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this).get(ListViewModel.class);

        termId = getIntent().getIntExtra("termId", -1);
        if (termId != -1) {
            viewModel.setTermId(termId);
        }
        else {
            selectedItemId = getIntent().getIntExtra("selectedItemId", 0);
            viewModel.setSelectedItemId(selectedItemId);
        }
        viewModel.loadData();

        viewModel.getTitle().observe(this, title -> {
            titleTextView.setText(title);
        });

        createObservers();
        observeData();
    }
    private void createObservers() {
        termObserver = terms -> {
            if (terms != null) {
                recyclerView.setAdapter(new TermAdapter(terms, this));
            }
        };
        courseObserver = courses -> {
            if (courses != null) {
                recyclerView.setAdapter(new CourseAdapter(courses, this));
            }
        };
        assessmentObserver = assessments -> {
            if (assessments != null) {
                recyclerView.setAdapter(new AssessmentAdapter(assessments, this));
            }
        };
        instructorObserver = instructors -> {
            if (instructors != null) {
                recyclerView.setAdapter(new InstructorAdapter(instructors, this));
            }
        };
        coursesByTermIdObserver = courses -> {
            if (courses != null) {
                recyclerView.setAdapter(new CourseAdapter(courses, this));
            }
        };
    }
    private void observeData() {
        if (termId != -1) {
            viewModel.getCoursesByTermId().observe(this, coursesByTermIdObserver);
        } else {
            switch (selectedItemId) {
                case 0:
                    if (useTestData) {
                        // Generate and set test data directly
                        List<Term> testTerms = TermTestDataGenerator.generateTermTestData(20);
                        recyclerView.setAdapter(new TermAdapter(testTerms, this));
                    } else {
                        viewModel.getTerms().observe(this, termObserver);
                    }
                    break;
                case 1:
                    viewModel.getInstructors().observe(this, instructorObserver);
                    break;
                case 2:
                    viewModel.getCourses().observe(this, courseObserver);
                    break;
                case 3:
                    viewModel.getAssessments().observe(this, assessmentObserver);
                    break;
            }
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        viewModel.loadData();
        observeData();
    }
    @Override
    public void onItemClick(Assessment assessment){
        Intent intent = new Intent(this, AssessmentDetailActivity.class);
        intent.putExtra("assessmentId", assessment.getId());
        startActivity(intent);
    }
    @Override
    public void onItemClick(Term term) {
        Intent intent = new Intent(this, TermDetailActivity.class);
        intent.putExtra("termId", term.getId());
        startActivity(intent);
    }
    @Override
    public void onItemClick(Course course) {
        Intent intent = new Intent(this, CourseDetailActivity.class);
        intent.putExtra("courseId", course.getId());
        startActivity(intent);
    }
    @Override
    public void onItemClick(Instructor instructor) {
        Intent intent = new Intent(this, InstructorDetailActivity.class);
        intent.putExtra("instructorId", instructor.getId());
        startActivity(intent);
    }
}