package com.example.myapplication.activities;

import com.example.myapplication.R;
import com.example.myapplication.repository.AssessmentRepository;
import com.example.myapplication.repository.CourseRepository;
import com.example.myapplication.utilities.AlertReceiver;
import com.example.myapplication.viewmodel.AssessmentDetailViewModel;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class AssessmentDetailActivity extends BaseActivity {

    private AssessmentDetailViewModel viewModel;
    private TextView titleTextView;
    private TextView assessTitleTextView;
    private TextView assessCourseTextView;
    private TextView assessTypeTextView;
    private TextView assessStartTextView;
    private TextView assessEndTextView;
    private TextView assessCourseLabelTextView;
    private Button assessAlertButton;
    private Button assessEditButton;
    private Button assessDeleteButton;
    private AssessmentRepository assessmentRepository;
    private CourseRepository courseRepository;
    private ActivityResultLauncher<Intent> requestAlarmPermissionLauncher;
    private ActivityResultLauncher<String> requestNotificationPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_detail);

        titleTextView = findViewById(R.id.assessDetailPageTitleTextView);
        assessTitleTextView = findViewById(R.id.assessDetailTitleTextView);
        assessCourseTextView = findViewById(R.id.assessDetailCourseTextView);
        assessTypeTextView = findViewById(R.id.assessDetailTypeTextView);
        assessStartTextView = findViewById(R.id.assessDetailStartTextView);
        assessEndTextView = findViewById(R.id.assessDetailEndTextView);
        assessCourseLabelTextView = findViewById(R.id.assessDetailCourseLabelTextView);

        assessAlertButton = findViewById(R.id.assessDetailAlertButton);
        assessEditButton = findViewById(R.id.assessDetailEditButton);
        assessDeleteButton = findViewById(R.id.assessDetailDeleteButton);

        assessmentRepository = new AssessmentRepository(this);
        courseRepository = new CourseRepository(this);

        int assessmentId = getIntent().getIntExtra("assessmentId", -1);

        // Get the ViewModel
        viewModel = new ViewModelProvider(this).get(AssessmentDetailViewModel.class);

        // Observe the data
        viewModel.getAssessment().observe(this, assessment -> {
            if (assessment != null) {
                assessTitleTextView.setText(assessment.getTitle());
                assessTypeTextView.setText(assessment.getType());
                // Format dates
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String startDate = assessment.getStart().format(formatter);
                String endDate = assessment.getEnd().format(formatter);
                assessStartTextView.setText(startDate);
                assessEndTextView.setText(endDate);
            }
        });

        viewModel.getCourse().observe(this, course -> {
            if (course != null) {
                assessCourseTextView.setText(course.getTitle());
            } else {
                assessCourseTextView.setText("Course Not Found"); // Handle potential null course
            }
        });

        // Observe errors
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(AssessmentDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        if (assessmentId != -1) {
            viewModel.loadAssessment(assessmentId);
        }
        else {
            //error handling
            titleTextView.setText("Error: No Assessment ID provided");
        }

        requestNotificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Log.d("AssessmentDetailActivity", "POST_NOTIFICATIONS granted");
                        setTheAlarms(); // Set the alarm after notification permission is granted
                    } else {
                        Log.e("AssessmentDetailActivity", "POST_NOTIFICATIONS denied");
                        Toast.makeText(this, "Notification permission required to show alerts.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        /*
        requestAlarmPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    int permissionCheckResult = ContextCompat.checkSelfPermission(
                            AssessmentDetailActivity.this,
                            Manifest.permission.SCHEDULE_EXACT_ALARM
                    );
                    boolean isGranted = (permissionCheckResult == PackageManager.PERMISSION_GRANTED || permissionCheckResult == -1); //corresponds to permission granted on my testing device
                    if (isGranted) {
                        Toast.makeText(this, "Exact alarm permission granted.", Toast.LENGTH_SHORT).show();
                        setTheAlarm();
                    } else {
                        Toast.makeText(this, "Exact alarm permission not granted.", Toast.LENGTH_SHORT).show();
                        // Handle the case where the user didn't grant the permission
                    }
                }
        );

         */

        //alert button listener
        assessAlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewModel.getAssessment().getValue() != null) {
                    /*
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // TIRAMISU is API 33
                        if (ContextCompat.checkSelfPermission(AssessmentDetailActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                            // Request POST_NOTIFICATIONS permission
                            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                            return; // Important: Return here!  Don't set the alarm until permission is granted.
                        } else {
                            setTheAlarm();
                        }
                    }
                    else {
                        setTheAlarm();
                    }
                     */
                    setTheAlarms();
                } else {
                    Toast.makeText(AssessmentDetailActivity.this, "Course data not loaded yet.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        assessEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewModel.getAssessment().getValue() != null) {
                    Intent intent = new Intent(AssessmentDetailActivity.this, AssessmentFormActivity.class);
                    intent.putExtra("assessmentId", assessmentId);
                    startActivity(intent);
                }
            }
        });

        assessDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewModel.getAssessment().getValue() != null) {
                    new AlertDialog.Builder(AssessmentDetailActivity.this)
                            .setTitle("Confirm Delete")
                            .setMessage("Are you sure you want to delete this assessment?")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                // User confirmed, proceed with deletion
                                assessmentRepository.deleteAssessment(viewModel.getAssessment().getValue());
                                // Optionally, provide feedback or navigate back
                                Toast.makeText(AssessmentDetailActivity.this, "Assessment deleted.", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(AssessmentDetailActivity.this, MainActivity.class);
                                startActivity(intent);
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                // User cancelled, do nothing
                                dialog.dismiss();
                            })
                            .show();
                } else {
                    Toast.makeText(AssessmentDetailActivity.this, "Assessment data not loaded yet.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        assessCourseLabelTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onCourseClick();
            }
        });

        assessCourseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCourseClick();
            }
        });
    }

    private void onCourseClick() {
        if (viewModel.getCourse().getValue() != null) {
            Intent intent = new Intent(AssessmentDetailActivity.this, CourseDetailActivity.class);
            intent.putExtra("courseId", viewModel.getCourse().getValue().getId());
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int assessmentId = getIntent().getIntExtra("assessmentId", -1);
        if (assessmentId != -1) {
            viewModel.loadAssessment(assessmentId);
        } else {
            // Handle error if courseId is not available
            titleTextView.setText("Error: No Assessment ID provided");
        }
    }

    private void setTheAlarms() {
        if (viewModel.getAssessment().getValue() != null) {
            //String message = "Your assessment '" + viewModel.getAssessment().getValue().getTitle() + "' is ending!";
            LocalDate startDate = viewModel.getAssessment().getValue().getStart();
            LocalDate endDate = viewModel.getAssessment().getValue().getEnd();

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                // set the start date alert
                if (startDate != null) {
                    long startTimeTrigger = convertDateToTimestamp(startDate);
                    int startNotificationId = generateNotificationId();
                    Intent startIntent = new Intent(this, AlertReceiver.class);
                    startIntent.putExtra("message", "Your assessment '" + viewModel.getAssessment().getValue().getTitle() + "' is starting!");
                    startIntent.putExtra("notificationId", startNotificationId);

                    int startFlags = PendingIntent.FLAG_UPDATE_CURRENT;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        startFlags |= PendingIntent.FLAG_MUTABLE;
                    }
                    PendingIntent startPendingIntent = PendingIntent.getBroadcast(this, startNotificationId, startIntent, startFlags);

                    setAlarm(alarmManager, startTimeTrigger, startPendingIntent, startDate, "Start");
                } else {
                    Log.e("AssessmentDetailActivity", "Assessment start date not available.");
                    Toast.makeText(this, "Assessment start date not available.", Toast.LENGTH_SHORT).show();
                }

                // set the end date alert
                if (endDate != null) {
                    long endTimeTrigger = convertDateToTimestamp(endDate);
                    int endNotificationId = generateNotificationId();

                    Intent endIntent = new Intent(this, AlertReceiver.class);
                    endIntent.putExtra("message", "Your assessment '" + viewModel.getAssessment().getValue().getTitle() + "' is ending!");
                    endIntent.putExtra("notificationId", endNotificationId);
                    int endFlags = PendingIntent.FLAG_UPDATE_CURRENT;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        endFlags |= PendingIntent.FLAG_MUTABLE;
                    }
                    PendingIntent endPendingIntent = PendingIntent.getBroadcast(this, endNotificationId, endIntent, endFlags);

                    setAlarm(alarmManager, endTimeTrigger, endPendingIntent, endDate, "End"); // Refactor
                } else {
                    Log.e("AssessmentDetailActivity", "Assessment end date not available.");
                    Toast.makeText(this, "Assessment end date not available.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("AssessmentDetailActivity", "AlarmManager is null");
                Toast.makeText(this, "Failed to set alarm.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Assessment data not loaded yet.", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean checkAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.SCHEDULE_EXACT_ALARM
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED;
        }
        return true; // Permission not needed for older versions
    }

    private void setAlarm(AlarmManager alarmManager, long triggerTime, PendingIntent pendingIntent, LocalDate alertDate, String type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
        Log.d("AssessmentDetailActivity", "Alarm set for " + type + ": " + alertDate + " (timestamp: " + triggerTime + "), notificationId: " + pendingIntent.getCreatorUid());
        Toast.makeText(this, "Alert set for " + type + " " + alertDate, Toast.LENGTH_SHORT).show();
    }

    // Helper method to convert LocalDate to timestamp
    private long convertDateToTimestamp(LocalDate date) {
        return date.atStartOfDay(java.time.ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    private int generateNotificationId() {
        return new Random().nextInt(1000);
    }
}