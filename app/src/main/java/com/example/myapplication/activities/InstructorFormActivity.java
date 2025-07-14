package com.example.myapplication.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.repository.InstructorRepository;
import com.example.myapplication.viewmodel.InstructorFormViewModel;
import com.example.myapplication.datamodel.Instructor;

public class InstructorFormActivity extends BaseActivity {
    private EditText instructorNameTextView;
    private EditText instructorEmailTextView;
    private EditText instructorPhoneTextView;
    private Button instructorSubmitButton;
    private InstructorRepository instructorRepository;
    private InstructorFormViewModel viewModel;
    private int instructorId = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_form);
        instructorNameTextView = findViewById(R.id.instructorFormNameField);
        instructorEmailTextView = findViewById(R.id.instructorFormEmailField);
        instructorPhoneTextView = findViewById(R.id.instructorFormPhoneField);
        instructorSubmitButton = findViewById(R.id.instructorFormSubmitButton);
        instructorRepository = new InstructorRepository(this);

        instructorId = getIntent().getIntExtra("instructorId", -1);
        viewModel = new ViewModelProvider(this).get(InstructorFormViewModel.class);

        viewModel.loadInstructor(instructorId);

        viewModel.getInstructorDetails().observe(this, instructor -> {
            if(instructor != null) {
                instructorNameTextView.setText(instructor.getName());
                instructorEmailTextView.setText(instructor.getEmail());
                instructorPhoneTextView.setText(instructor.getPhone());
            }
        });

        instructorSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInstructor();
            }
        });
    }

    private void saveInstructor() {
        String name = instructorNameTextView.getText().toString().trim();
        String phone = instructorPhoneTextView.getText().toString().trim();
        String email = instructorEmailTextView.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Instructor instructorToSave;
        if(instructorId != -1) {
            instructorToSave = new Instructor(instructorId, name, phone, email);
            instructorRepository.updateInstructor(instructorToSave);
            Toast.makeText(this, "Instructor updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            instructorToSave = new Instructor(0, name, phone, email);
            long insertedId = instructorRepository.insertInstructor(instructorToSave);
            if (insertedId > 0) {
                Toast.makeText(this, "Instructor saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save course", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
