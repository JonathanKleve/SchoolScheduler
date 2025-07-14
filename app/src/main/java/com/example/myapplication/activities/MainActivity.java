package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.R;
import com.example.myapplication.viewmodel.MainActivityViewModel;

public class MainActivity extends BaseActivity {
    private int selectedViewAllPosition = 0;
    private int selectedCreateNewPosition = 0;
    private MainActivityViewModel viewModel;
    private Spinner viewAllSpinner;
    private Spinner createNewSpinner;
    private Button viewAllButton;
    private Button createNewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        viewAllSpinner = findViewById(R.id.mainViewTypeSpinner);
        createNewSpinner = findViewById(R.id.mainNewTypeSpinner);
        viewAllButton = findViewById(R.id.mainViewAllButton);
        createNewButton = findViewById(R.id.mainCreateNewButton);

        viewModel.getSpinnerItems().observe(this, items -> {
            ArrayAdapter<String> adapterViewAll = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
            viewAllSpinner.setAdapter(adapterViewAll);

            ArrayAdapter<String> adapterCreateNew = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
            createNewSpinner.setAdapter(adapterCreateNew);
        });

        viewAllSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedViewAllPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });

        viewAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                intent.putExtra("selectedItemId", selectedViewAllPosition); // Pass resource ID
                startActivity(intent);
            }
        });

        createNewSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCreateNewPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });

        createNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                switch (selectedCreateNewPosition) {
                    case 0:
                        intent = new Intent(MainActivity.this, TermFormActivity.class);
                        break;
                    case 1:
                        intent = new Intent(MainActivity.this, InstructorFormActivity.class);
                        break;
                    case 2:
                        intent = new Intent(MainActivity.this, CourseFormActivity.class);
                        break;
                    case 3:
                        intent = new Intent(MainActivity.this, AssessmentFormActivity.class);
                        break;
                }
                startActivity(intent);
            }
        });
    }
}