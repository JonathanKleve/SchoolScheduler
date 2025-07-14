package com.example.myapplication.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.datamodel.Course;
import com.example.myapplication.datamodel.Term;
import com.example.myapplication.repository.CourseRepository;
import com.example.myapplication.repository.TermRepository;
import com.example.myapplication.viewmodel.CourseFormViewModel;
import com.example.myapplication.viewmodel.TermFormViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TermFormActivity extends BaseActivity {
    private EditText termTitleTextView;
    private EditText termStartTextView;
    private EditText termEndTextView;
    private Button termSubmitButton;
    private TermFormViewModel viewModel;
    private int termId = -1;
    private TermRepository termRepository;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_form);
        termTitleTextView = findViewById(R.id.termFormTitleField);
        termStartTextView = findViewById(R.id.termFormStartField);
        termEndTextView = findViewById(R.id.termFormEndField);
        termSubmitButton = findViewById(R.id.termFormSubmitButton);

        termRepository = new TermRepository(this);

        termId = getIntent().getIntExtra("termId", -1);

        viewModel = new ViewModelProvider(this).get(TermFormViewModel.class);

        viewModel.loadTerm(termId);

        //prepopulate if editing

        viewModel.getTermDetails().observe(this, term -> {
            if(term != null) {
                termTitleTextView.setText(term.getTitle());
                termStartTextView.setText(term.getStart().format(dateFormatter));
                termEndTextView.setText(term.getEnd().format(dateFormatter));
            }
        });

        termStartTextView.setOnClickListener(v -> {
            showDatePickerDialogue(termStartTextView);
        });

        termEndTextView.setOnClickListener(v -> {
            showDatePickerDialogue(termEndTextView);
        });

        termSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTerm();
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
            month = date.getMonthValue() - 1;
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

    private void saveTerm() {
        String title = termTitleTextView.getText().toString().trim();
        String startDateStr = termStartTextView.getText().toString().trim();
        String endDateStr = termEndTextView.getText().toString().trim();

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

        Term termToSave;
        if(termId != -1) {
            termToSave = new Term(termId, title, startDate, endDate);
            termRepository.updateTerm(termToSave);
            Toast.makeText(this, "Term updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            termToSave = new Term(0, title, startDate, endDate);
            long insertedId = termRepository.insertTerm(termToSave);
            if (insertedId > 0) {
                Toast.makeText(this, "Term saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save course", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
