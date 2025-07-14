package com.example.myapplication.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myapplication.R;
import com.example.myapplication.datamodel.Assessment;
import com.example.myapplication.datamodel.Course;
import com.example.myapplication.repository.AssessmentRepository;
import com.example.myapplication.repository.CourseRepository;

import java.util.List;
import java.util.stream.Collectors;

public class AssessmentFormViewModel extends AndroidViewModel {
    private MutableLiveData<String[]> spinnerItems = new MutableLiveData<>();
    private MutableLiveData<Assessment> assessmentDetails = new MutableLiveData<>();
    private LiveData<List<Course>> allCourses; // LiveData of Course objects
    private LiveData<List<String>> courseTitles; // Transformed LiveData of Course Titles
    private MutableLiveData<String> selectedCourseTitle = new MutableLiveData<>();
    private AssessmentRepository assessmentRepository;
    private CourseRepository courseRepository;

    public AssessmentFormViewModel(Application application) {
        super(application);
        assessmentRepository = new AssessmentRepository(application);
        courseRepository = new CourseRepository(application);
        loadSpinnerItems();
        loadAllCourses(); // Load LiveData of Course objects
        transformCoursesToTitles(); // Transform to LiveData of titles
    }

    public MutableLiveData<String[]> getSpinnerItems() {
        return spinnerItems;
    }

    public LiveData<Assessment> getAssessmentDetails() {
        return assessmentDetails;
    }

    public LiveData<List<String>> getCourseTitles() {
        return courseTitles;
    }

    public LiveData<String> getSelectedCourseTitle() {
        return selectedCourseTitle;
    }

    public void loadAssessment(int assessmentId) {
        if (assessmentId != -1) {
            assessmentRepository.getAssessmentById(assessmentId).observeForever(assessment -> {
                assessmentDetails.setValue(assessment);
                // Fetch the associated course title
                courseRepository.getCourseById(assessment.getCourseId()).observeForever(course -> {
                    if (course != null) {
                        selectedCourseTitle.setValue(course.getTitle());
                    } else {
                        selectedCourseTitle.setValue(null);
                    }
                });
            });
        } else {
            assessmentDetails.setValue(null);
            selectedCourseTitle.setValue(null);
        }
    }

    private void loadSpinnerItems() {
        String[] items = getApplication().getResources().getStringArray(R.array.assessment_type_spinner_items);
        spinnerItems.setValue(items);
    }

    private void loadAllCourses() {
        allCourses = courseRepository.getAllCourses();
    }

    private void transformCoursesToTitles() {
        courseTitles = Transformations.map(allCourses, courses -> {
            if (courses != null) {
                return courses.stream().map(Course::getTitle).collect(Collectors.toList());
            } else {
                return null;
            }
        });
    }
}