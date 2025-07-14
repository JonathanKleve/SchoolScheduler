package com.example.myapplication.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.datamodel.Assessment;
import com.example.myapplication.datamodel.Course;
import com.example.myapplication.datamodel.Term;
import com.example.myapplication.repository.AssessmentRepository;
import com.example.myapplication.repository.CourseRepository;
import com.example.myapplication.viewmodel.AssessmentFormViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AssessmentFormActivity extends BaseActivity {
    private EditText assessTitleTextView;
    private EditText assessStartTextView;
    private EditText assessEndTextView;
    private Spinner assessTypeSpinner;
    private Spinner assessCourseSpinner;
    private Button assessSubmitButton;
    private AssessmentRepository assessmentRepository;
    private CourseRepository courseRepository;
    private AssessmentFormViewModel viewModel;
    private int assessmentId = -1;
    private int selectedCourseId = -1;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_form);

        assessTitleTextView = findViewById(R.id.assessFormTitleField);
        assessStartTextView = findViewById(R.id.assessFormStartField);
        assessEndTextView = findViewById(R.id.assessFormEndField);
        assessTypeSpinner = findViewById(R.id.assessFormTypeSpinner);
        assessCourseSpinner = findViewById(R.id.assessFormCourseSpinner);
        assessSubmitButton = findViewById(R.id.assessFormSubmitButton);

        assessmentRepository = new AssessmentRepository(this);
        courseRepository = new CourseRepository(this);

        assessmentId = getIntent().getIntExtra("assessmentId", -1);

        // Get the ViewModel
        viewModel = new ViewModelProvider(this).get(AssessmentFormViewModel.class);

        viewModel.getSpinnerItems().observe(this, items -> {
            ArrayAdapter<String> adapterViewAll = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
            assessTypeSpinner.setAdapter(adapterViewAll);
        });

        viewModel.getCourseTitles().observe(this, courseTitles -> {
            if(courseTitles != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, courseTitles);
                assessCourseSpinner.setAdapter(adapter);
            }
        });

        viewModel.loadAssessment(assessmentId);

        viewModel.getAssessmentDetails().observe(this, assessment -> {
            if(assessment != null) {
                assessTitleTextView.setText(assessment.getTitle());
                assessStartTextView.setText(assessment.getStart().format(dateFormatter));
                assessEndTextView.setText(assessment.getEnd().format(dateFormatter));
                String assessmentType = assessment.getType();
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) assessTypeSpinner.getAdapter();
                if (adapter != null) {
                    int position = adapter.getPosition(assessmentType);
                    if (position != -1) {
                        assessTypeSpinner.setSelection(position);
                    }
                }
            }
        });

        // Observe selected course title for pre-selection
        viewModel.getSelectedCourseTitle().observe(this, selectedTitle -> {
            if (selectedTitle != null) {
                ArrayAdapter<String> courseAdapter = (ArrayAdapter<String>) assessCourseSpinner.getAdapter();
                if (courseAdapter != null) {
                    int position = courseAdapter.getPosition(selectedTitle);
                    if (position != -1) {
                        assessCourseSpinner.setSelection(position);
                    }
                }
            }
        });

        assessStartTextView.setOnClickListener(v -> {
            showDatePickerDialogue(assessStartTextView);
        });

        assessEndTextView.setOnClickListener(v -> {
            showDatePickerDialogue(assessEndTextView);
        });

        assessCourseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCourseTitle = (String) parent.getItemAtPosition(position);
                // Fetch the Term ID based on the selected title
                courseRepository.getAllCourses().observe(AssessmentFormActivity.this, courses -> {
                    if (courses != null) {
                        for (Course course : courses) {
                            if (course.getTitle().equals(selectedCourseTitle)) {
                                selectedCourseId = course.getId();
                                return;
                            }
                        }
                        selectedCourseId = -1; // Term not found
                    } else {
                        selectedCourseId = -1; // Error fetching terms
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCourseId = -1;
            }
        });

        assessSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAssessment();
            }
        });

    }
    private void showDatePickerDialogue(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Attempt to parse existing date for initial selection
        String existingDate = editText.getText().toString();
        try {
            LocalDate date = LocalDate.parse(existingDate, dateFormatter); // Parse with DateTimeFormatter
            year = date.getYear();
            month = date.getMonthValue() - 1; // Calendar uses 0-based months
            day = date.getDayOfMonth();
            calendar.set(year, month, day);
        } catch (Exception e) {
            // If parsing fails, use current date as default
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.US, "%02d/%02d/%04d",monthOfYear + 1, dayOfMonth, year1);
                    editText.setText(selectedDate);
                },
                year,
                month,
                day
        );
        datePickerDialog.show();
    }

    private void saveAssessment() {
        String title = assessTitleTextView.getText().toString().trim();
        String startDateStr = assessStartTextView.getText().toString().trim();
        String endDateStr = assessEndTextView.getText().toString().trim();
        String type = assessTypeSpinner.getSelectedItem().toString();

        if (title.isEmpty() || startDateStr.isEmpty() || endDateStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        LocalDate startDate = null;
        LocalDate endDate = null;
        try {
            startDate = LocalDate.parse(startDateStr, dateFormatter);
            endDate = LocalDate.parse(endDateStr, dateFormatter);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCourseId != -1) {
            Assessment assessmentToSave;
            if (assessmentId != -1) {
                // Updating existing course
                assessmentToSave = new Assessment(assessmentId, title, type, startDate, endDate, selectedCourseId);
                assessmentRepository.updateAssessment(assessmentToSave);
                Toast.makeText(this, "Assessment updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // Inserting new course
                assessmentToSave = new Assessment(0, title, type, startDate, endDate, selectedCourseId);
                long insertedId = assessmentRepository.insertAssessment(assessmentToSave);
                if (insertedId > 0) {
                    Toast.makeText(this, "Assessment saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to save assessment", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "Please select a Course", Toast.LENGTH_SHORT).show();
        }

    }
}