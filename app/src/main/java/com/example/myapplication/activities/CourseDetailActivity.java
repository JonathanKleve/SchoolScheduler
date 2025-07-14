package com.example.myapplication.activities;

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

import com.example.myapplication.R;
import com.example.myapplication.repository.CourseRepository;
import com.example.myapplication.repository.InstructorRepository;
import com.example.myapplication.repository.TermRepository;
import com.example.myapplication.viewmodel.CourseDetailViewModel;
import com.example.myapplication.utilities.AlertReceiver;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CourseDetailActivity extends BaseActivity {
    private CourseDetailViewModel viewModel;
    private TextView titleTextView;
    private TextView courseTitleTextView;
    private TextView courseTermTextView;
    private TextView courseStatusTextView;
    private TextView courseStartTextView;
    private TextView courseEndTextView;
    private TextView courseInstructorTextView;
    private TextView courseNotesTextView;
    private TextView courseInstructorLabelTextView;
    private TextView courseTermLabelTextView;
    private Button courseAlertButton;
    private Button courseEditButton;
    private Button courseDeleteButton;
    private Button courseNotesButton;
    private CourseRepository courseRepository;
    private TermRepository termRepository;
    private InstructorRepository instructorRepository;
    private ActivityResultLauncher<String[]> requestMultiplePermissionsLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        titleTextView = findViewById(R.id.courseDetailPageTitleTextView);
        courseTitleTextView = findViewById(R.id.courseDetailTitleTextView);
        courseTermTextView = findViewById(R.id.courseDetailTermTextView);
        courseStatusTextView = findViewById(R.id.courseDetailStatusTextView);
        courseStartTextView = findViewById(R.id.courseDetailStartTextView);
        courseEndTextView = findViewById(R.id.courseDetailEndTextView);
        courseInstructorTextView = findViewById(R.id.courseDetailInstructorTextView);
        courseNotesTextView = findViewById(R.id.courseDetailNotesTextView);
        courseInstructorLabelTextView = findViewById(R.id.courseDetailInstructorLabelTextView);
        courseTermLabelTextView = findViewById(R.id.courseDetailTermLabelTextView);

        courseAlertButton = findViewById(R.id.courseDetailAlertButton);
        courseEditButton = findViewById(R.id.courseDetailEditButton);
        courseDeleteButton = findViewById(R.id.courseDetailDeleteButton);
        courseNotesButton = findViewById(R.id.courseDetailNotesButton);

        courseRepository = new CourseRepository(this);
        termRepository = new TermRepository(this);
        instructorRepository = new InstructorRepository(this);

        int courseId = getIntent().getIntExtra("courseId", -1);

        // Get the ViewModel
        viewModel = new ViewModelProvider(this).get(CourseDetailViewModel.class);

        // Observe the course data
        viewModel.getCourse().observe(this, course -> {
            if (course != null) {
                courseTitleTextView.setText(course.getTitle());
                courseStatusTextView.setText(course.getStatus());
                // Format dates
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String startDate = course.getStart().format(formatter);
                String endDate = course.getEnd().format(formatter);
                courseStartTextView.setText(startDate);
                courseEndTextView.setText(endDate);
                courseNotesTextView.setText(course.getNotes());
            }
        });

        // observe the term data
        viewModel.getTerm().observe(this, term -> {
            if (term != null) {
                courseTermTextView.setText(term.getTitle());
            }
        });

        // observe the instructor data
        viewModel.getInstructor().observe(this, instructor -> {
            if (instructor != null) {
                courseInstructorTextView.setText(instructor.getName());
            } else {
                courseInstructorTextView.setText("No Instructor Assigned"); // Handle case where instructor is null
            }
        });

        // Observe errors
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(CourseDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        if (courseId != -1) {
            viewModel.loadCourse(courseId);
        }
        else {
            //error handling
            titleTextView.setText("Error: No Course ID provided");
        }
/*
        requestMultiplePermissionsLauncher = registerForActivityResult( // Changed
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    boolean alarmPermissionGranted = false;
                    boolean notificationPermissionGranted = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        alarmPermissionGranted = permissions.get(Manifest.permission.SCHEDULE_EXACT_ALARM) != null &&
                                (permissions.get(Manifest.permission.SCHEDULE_EXACT_ALARM) == true ||
                                        ContextCompat.checkSelfPermission(CourseDetailActivity.this, Manifest.permission.SCHEDULE_EXACT_ALARM) == -1);
                    }
                    notificationPermissionGranted = permissions.get(Manifest.permission.POST_NOTIFICATIONS) != null && permissions.get(Manifest.permission.POST_NOTIFICATIONS) == true;


                    if (alarmPermissionGranted && notificationPermissionGranted) {
                        setTheAlarm();
                    } else {
                        List<String> deniedPermissions = new ArrayList<>();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            if (permissions.get(Manifest.permission.SCHEDULE_EXACT_ALARM) == Boolean.FALSE && ContextCompat.checkSelfPermission(CourseDetailActivity.this, Manifest.permission.SCHEDULE_EXACT_ALARM) != -1) {
                                deniedPermissions.add(Manifest.permission.SCHEDULE_EXACT_ALARM);
                            }
                        }
                        if (permissions.get(Manifest.permission.POST_NOTIFICATIONS) == Boolean.FALSE) {
                            deniedPermissions.add(Manifest.permission.POST_NOTIFICATIONS);
                        }

                        if (!deniedPermissions.isEmpty()) {
                            showPermissionSettingsDialog("Permissions are required to set alerts. Please enable them in App settings.");
                        }
                    }
                }
        );

 */

        //alert button listener
        courseAlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewModel.getCourse().getValue() != null) {
                    /*
                    List<String> permissionsToRequest = new ArrayList<>();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (ContextCompat.checkSelfPermission(CourseDetailActivity.this, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED &&
                                ContextCompat.checkSelfPermission(CourseDetailActivity.this, Manifest.permission.SCHEDULE_EXACT_ALARM) != -1) {
                            permissionsToRequest.add(Manifest.permission.SCHEDULE_EXACT_ALARM);
                        }
                    }
                    if (ContextCompat.checkSelfPermission(CourseDetailActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS);
                    }

                    if (!permissionsToRequest.isEmpty()) {
                        requestMultiplePermissionsLauncher.launch(permissionsToRequest.toArray(new String[0])); // Launch request
                    } else {
                        setTheAlarm(); //if all permissions are already granted.
                    }S
                     */
                    setTheAlarms();
                } else {
                    Toast.makeText(CourseDetailActivity.this, "Course data not loaded yet.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        courseEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewModel.getCourse().getValue() != null) {
                    Intent intent = new Intent(CourseDetailActivity.this, CourseFormActivity.class);
                    intent.putExtra("courseId", courseId);
                    startActivity(intent);
                }
            }
        });

        courseDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewModel.getCourse().getValue() != null) {
                    new AlertDialog.Builder(CourseDetailActivity.this)
                            .setTitle("Confirm Delete")
                            .setMessage("Are you sure you want to delete this course?")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                // User confirmed, proceed with deletion
                                courseRepository.deleteCourse(viewModel.getCourse().getValue());
                                // Optionally, provide feedback or navigate back
                                Toast.makeText(CourseDetailActivity.this, "Course deleted.", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(CourseDetailActivity.this, MainActivity.class);
                                startActivity(intent);
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                // User cancelled, do nothing
                                dialog.dismiss();
                            })
                            .show();
                } else {
                    Toast.makeText(CourseDetailActivity.this, "Course data not loaded yet.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        courseNotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewModel.getCourse().getValue() != null) {
                    String body = "Course Notes:\n\n" + viewModel.getCourse().getValue().getNotes();
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Course Notes");
                    intent.putExtra(Intent.EXTRA_TEXT, body);
                    try {
                        startActivity(intent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(CourseDetailActivity.this, "No email client installed.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CourseDetailActivity.this, "Course data not loaded yet.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        courseInstructorLabelTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onInstructorClick();
            }
        });

        courseInstructorTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onInstructorClick();
            }
        });

        courseTermTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTermClick();
            }
        });

        courseTermLabelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTermClick();
            }
        });
    }

    private void onInstructorClick() {
        if (viewModel.getInstructor().getValue() != null) {
            Intent intent = new Intent(CourseDetailActivity.this, InstructorDetailActivity.class);
            intent.putExtra("instructorId", viewModel.getInstructor().getValue().getId());
            startActivity(intent);
        }
    }

    private void onTermClick() {
        if (viewModel.getTerm().getValue() != null) {
            Intent intent = new Intent(CourseDetailActivity.this, TermDetailActivity.class);
            intent.putExtra("termId", viewModel.getTerm().getValue().getId());
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("CourseDetailLifecycle", "onStart called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("CourseDetailLifecycle", "onResume called");
        int courseId = getIntent().getIntExtra("courseId", -1);
        if (courseId != -1) {
            viewModel.loadCourse(courseId);
        } else {
            // Handle error if courseId is not available
            titleTextView.setText("Error: No Course ID provided");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("CourseDetailLifecycle", "onPause called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("CourseDetailLifecycle", "onStop called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("CourseDetailLifecycle", "onDestroy called");
    }

    private void setTheAlarms() {
        if (viewModel.getCourse().getValue() != null) {
            LocalDate startDate = viewModel.getCourse().getValue().getStart();
            LocalDate endDate = viewModel.getCourse().getValue().getEnd();

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {

                //set start date alarm
                if (startDate != null) {
                    long startTimeTrigger = convertDateToTimestamp(startDate);
                    int startNotificationId = generateNotificationId();
                    Intent startIntent = new Intent(this, AlertReceiver.class);
                    startIntent.putExtra("message", "Your course '" + viewModel.getCourse().getValue().getTitle() + "' is starting!");
                    startIntent.putExtra("notificationId", startNotificationId);

                    int startFlags = PendingIntent.FLAG_UPDATE_CURRENT;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        startFlags |= PendingIntent.FLAG_MUTABLE;
                    }
                    PendingIntent startPendingIntent = PendingIntent.getBroadcast(this, startNotificationId, startIntent, startFlags);

                    setAlarm(alarmManager, startTimeTrigger, startPendingIntent, startDate, "Start"); // Refactor

                } else {
                    Log.e("CourseDetailActivity", "Course start date not available.");
                    Toast.makeText(this, "Course start date not available.", Toast.LENGTH_SHORT).show();
                }

                // set end date alarm
                if (endDate != null) {
                    long endTimeTrigger = convertDateToTimestamp(endDate);
                    int endNotificationId = generateNotificationId();

                    Intent endIntent = new Intent(this, AlertReceiver.class);
                    endIntent.putExtra("message", "Your course '" + viewModel.getCourse().getValue().getTitle() + "' is ending!");
                    endIntent.putExtra("notificationId", endNotificationId);
                    int endFlags = PendingIntent.FLAG_UPDATE_CURRENT;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        endFlags |= PendingIntent.FLAG_MUTABLE;
                    }
                    PendingIntent endPendingIntent = PendingIntent.getBroadcast(this, endNotificationId, endIntent, endFlags);

                    setAlarm(alarmManager, endTimeTrigger, endPendingIntent, endDate, "End"); // Refactor
                } else {
                    Log.e("CourseDetailActivity", "Course end date not available.");
                    Toast.makeText(this, "Course end date not available.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("CourseDetailActivity", "AlarmManager is null");
                Toast.makeText(this, "Failed to set alarm.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Course data not loaded yet.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setAlarm(AlarmManager alarmManager, long triggerTime, PendingIntent pendingIntent, LocalDate alertDate, String type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
        Log.d("CourseDetailActivity", "Alarm set for " + type + ": " + alertDate + " (timestamp: " + triggerTime + "), notificationId: " + pendingIntent.getCreatorUid());
        Toast.makeText(this, "Alert set for " + type + " " + alertDate, Toast.LENGTH_SHORT).show();
    }

    private void showPermissionSettingsDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage(message)
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    // Helper method to convert LocalDate to timestamp
    private long convertDateToTimestamp(LocalDate date) {
        return date.atStartOfDay(java.time.ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    private int generateNotificationId() {
        return new Random().nextInt(1000);
    }
}
