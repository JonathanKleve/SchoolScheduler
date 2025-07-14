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

import com.example.myapplication.datamodel.Course;
import com.example.myapplication.datamodel.Instructor;
import com.example.myapplication.datamodel.Term;
import com.example.myapplication.repository.CourseRepository;
import com.example.myapplication.repository.InstructorRepository;
import com.example.myapplication.repository.TermRepository;
import com.example.myapplication.utilities.AlertReceiver;

public class CourseDetailViewModel extends AndroidViewModel{
    private Application application;
    private MutableLiveData<Integer> courseId = new MutableLiveData<>();
    private LiveData<Course> course;
    private LiveData<Term> term;
    private LiveData<Instructor> instructor;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private CourseRepository courseRepository;
    private TermRepository termRepository;
    private InstructorRepository instructorRepository;

    public CourseDetailViewModel(Application application) {
        super(application); // Call super constructor
        this.application = application; // Initialize the application field
        courseRepository = new CourseRepository(application);
        termRepository = new TermRepository(application);
        instructorRepository = new InstructorRepository(application);

        course = Transformations.switchMap(courseId, id -> {
            isLoading.postValue(true);
            return courseRepository.getCourseById(id);
        });

        term = Transformations.switchMap(course, courseData -> {
            if (courseData != null) {
                return termRepository.getTermById(courseData.getTermId());
            } else {
                return new MutableLiveData<>(null);
            }
        });

        instructor = Transformations.switchMap(course, courseData -> {
            if (courseData != null) {
                return instructorRepository.getInstructorById(courseData.getInstructorId());
            } else {
                return new MutableLiveData<>(null);
            }
        });
    }

    public LiveData<Course> getCourse() {
        return course;
    }

    public LiveData<Term> getTerm() {
        return term;
    }
    public LiveData<Instructor> getInstructor(){ return instructor; }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadCourse(int id) {
        courseId.setValue(id);
    }

    public void setCourseEndAlert(String message, long triggerTime, int notificationId) {
        AlarmManager alarmManager = (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(application, AlertReceiver.class);
        intent.putExtra("message", message);
        intent.putExtra("notificationId", notificationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(application, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Set the alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
    }
}