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
import com.example.myapplication.datamodel.Course;
import com.example.myapplication.datamodel.Instructor;
import com.example.myapplication.datamodel.Term;
import com.example.myapplication.repository.CourseRepository;
import com.example.myapplication.repository.TermRepository;
import com.example.myapplication.repository.InstructorRepository;
import com.example.myapplication.viewmodel.CourseFormViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CourseFormActivity extends BaseActivity {
    private EditText courseTitleTextView;
    private EditText courseStartTextView;
    private EditText courseEndTextView;
    private EditText courseNotesTextView;
    private Spinner courseStatusSpinner;
    private Spinner courseTermSpinner;
    private Spinner courseInstructorSpinner;
    private Button courseSubmitButton;
    private CourseRepository courseRepository;
    private TermRepository termRepository;
    private InstructorRepository instructorRepository;
    private CourseFormViewModel viewModel;
    private int courseId = -1;
    private int selectedTermId = -1;
    private int selectedInstructorId = -1;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_form);

        courseTitleTextView = findViewById(R.id.courseFormTitleField);
        courseStartTextView = findViewById(R.id.courseFormStartField);
        courseEndTextView = findViewById(R.id.courseFormEndField);
        courseNotesTextView = findViewById(R.id.courseFormNotesField);
        courseStatusSpinner = findViewById(R.id.courseFormStatusSpinner);
        courseTermSpinner = findViewById(R.id.courseFormTermSpinner);
        courseInstructorSpinner = findViewById(R.id.courseFormInstructorSpinner);
        courseSubmitButton = findViewById(R.id.courseFormSubmitButton);

        courseRepository = new CourseRepository(this);
        termRepository = new TermRepository(this);
        instructorRepository = new InstructorRepository(this);

        courseId = getIntent().getIntExtra("courseId", -1);

        // Get the ViewModel
        viewModel = new ViewModelProvider(this).get(CourseFormViewModel.class);

        viewModel.getSpinnerItems().observe(this, items -> {
            ArrayAdapter<String> adapterViewAll = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
            courseStatusSpinner.setAdapter(adapterViewAll);
        });

        viewModel.getTermTitles().observe(this, termTitles -> {
            if(termTitles != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, termTitles);
                courseTermSpinner.setAdapter(adapter);
            }
        });

        viewModel.getInstructorNames().observe(this, instructorNames -> {
            if(instructorNames != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, instructorNames);
                courseInstructorSpinner.setAdapter(adapter);
            }
        });

        viewModel.loadCourse(courseId);

        //pre-populating fields if editing

        viewModel.getCourseDetails().observe(this, course -> {
            if(course != null) {
                courseTitleTextView.setText(course.getTitle());
                courseStartTextView.setText(course.getStart().format(dateFormatter));
                courseEndTextView.setText(course.getEnd().format(dateFormatter));
                courseNotesTextView.setText(course.getNotes());
                String courseStatus = course.getStatus();
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) courseStatusSpinner.getAdapter();
                if (adapter != null) {
                    int position = adapter.getPosition(courseStatus);
                    if (position != -1) {
                        courseStatusSpinner.setSelection(position);
                    }
                }
                viewModel.loadTermTitleForCourse(course.getTermId());
                viewModel.loadInstructorNameForCourse(course.getInstructorId());
            }
        });

        // Observe selected course title for pre-selection
        viewModel.getSelectedTermTitle().observe(this, selectedTitle -> {
            if (selectedTitle != null) {
                ArrayAdapter<String> termAdapter = (ArrayAdapter<String>) courseTermSpinner.getAdapter();
                if (termAdapter != null) {
                    int position = termAdapter.getPosition(selectedTitle);
                    if (position != -1) {
                        courseTermSpinner.setSelection(position);
                    }
                }
            }
        });
        //same as above but for instructor
        viewModel.getSelectedInstructorName().observe(this, selectedName -> {
            if(selectedName != null) {
                ArrayAdapter<String> instructorAdapter = (ArrayAdapter<String>) courseInstructorSpinner.getAdapter();
                if(instructorAdapter != null) {
                    int position = instructorAdapter.getPosition(selectedName);
                    if (position != -1) {
                        courseInstructorSpinner.setSelection(position);
                    }
                }
            }
        });

        courseStartTextView.setOnClickListener(v -> {
            showDatePickerDialogue(courseStartTextView);
        });

        courseEndTextView.setOnClickListener(v -> {
            showDatePickerDialogue(courseEndTextView);
        });

        courseTermSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTermTitle = (String) parent.getItemAtPosition(position);
                // Fetch the Term ID based on the selected title
                termRepository.getAllTerms().observe(CourseFormActivity.this, terms -> {
                    if (terms != null) {
                        for (Term term : terms) {
                            if (term.getTitle().equals(selectedTermTitle)) {
                                selectedTermId = term.getId();
                                return;
                            }
                        }
                        selectedTermId = -1; // Term not found
                    } else {
                        selectedTermId = -1; // Error fetching terms
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTermId = -1;
            }
        });

        courseInstructorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedInstructorName = (String) parent.getItemAtPosition(position);
                // Fetch the Instructor ID based on the selected name
                instructorRepository.getAllInstructors().observe(CourseFormActivity.this, instructors -> {
                    if (instructors != null) {
                        for (Instructor instructor : instructors) {
                            if (instructor.getName().equals(selectedInstructorName)) {
                                selectedInstructorId = instructor.getId();
                                return;
                            }
                        }
                        selectedInstructorId = -1; // Instructor not found
                    } else {
                        selectedInstructorId = -1; // Error fetching instructors
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedInstructorId = -1;
            }
        });

        courseSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCourse();
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
            LocalDate date = LocalDate.parse(existingDate, dateFormatter);
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

    private void saveCourse() {
        String title = courseTitleTextView.getText().toString().trim();
        String startDateStr = courseStartTextView.getText().toString().trim();
        String endDateStr = courseEndTextView.getText().toString().trim();
        String notes = courseNotesTextView.getText().toString().trim();
        String status = courseStatusSpinner.getSelectedItem().toString();

        if (title.isEmpty() || startDateStr.isEmpty() || endDateStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields besides notes", Toast.LENGTH_SHORT).show();
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

        if (selectedTermId != -1 && selectedInstructorId != -1) {
            Course courseToSave;
            if (courseId != -1) {
                // Updating existing course
                courseToSave = new Course(courseId, title, startDate, endDate, status, selectedInstructorId, notes, selectedTermId);
                courseRepository.updateCourse(courseToSave);
                Toast.makeText(this, "Course updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // Inserting new course
                courseToSave = new Course(0, title, startDate, endDate, status, selectedInstructorId, notes, selectedTermId);
                long insertedId = courseRepository.insertCourse(courseToSave);
                if (insertedId > 0) {
                    Toast.makeText(this, "Course saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to save course", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "Please select both a Term and an Instructor", Toast.LENGTH_SHORT).show();
        }
    }
}
