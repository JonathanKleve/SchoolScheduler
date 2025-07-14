package com.example.myapplication.viewmodel;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myapplication.datamodel.Assessment;
import com.example.myapplication.datamodel.Course;
import com.example.myapplication.repository.AssessmentRepository;
import com.example.myapplication.repository.CourseRepository;
import com.example.myapplication.utilities.AlertReceiver;

public class AssessmentDetailViewModel extends AndroidViewModel {

    private Application application;
    private MutableLiveData<Integer> assessmentId = new MutableLiveData<>();
    private LiveData<Assessment> assessment;
    private LiveData<Course> course;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>(null);

    private AssessmentRepository assessmentRepository;
    private CourseRepository courseRepository;

    public AssessmentDetailViewModel(Application application) {
        super(application); // Call super constructor
        this.application = application; // Initialize the application field
        assessmentRepository = new AssessmentRepository(application);
        courseRepository = new CourseRepository(application);

        assessment = Transformations.switchMap(assessmentId, id -> {
            isLoading.postValue(true);
            return assessmentRepository.getAssessmentById(id);
        });

        course = Transformations.switchMap(assessment, assessmentData -> {
            if (assessmentData != null) {
                return courseRepository.getCourseById(assessmentData.getCourseId());
            } else {
                return new MutableLiveData<>(null);
            }
        });
    }

    public LiveData<Assessment> getAssessment() {
        return assessment;
    }

    public LiveData<Course> getCourse() {
        return course;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadAssessment(int id) {
        assessmentId.setValue(id);
    }

    public void setAssessmentEndAlert(String message, long triggerTime, int notificationId) {
        AlarmManager alarmManager = (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(application, AlertReceiver.class);
        intent.putExtra("message", message);
        intent.putExtra("notificationId", notificationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(application, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Set the alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
    }
}