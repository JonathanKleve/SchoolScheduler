package com.example.myapplication.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.myapplication.R;
import com.example.myapplication.datamodel.Assessment;
import com.example.myapplication.datamodel.Course;
import com.example.myapplication.datamodel.Instructor;
import com.example.myapplication.datamodel.Term;
import com.example.myapplication.repository.AssessmentRepository;
import com.example.myapplication.repository.CourseRepository;
import com.example.myapplication.repository.InstructorRepository;
import com.example.myapplication.repository.TermRepository;
import java.util.List;

public class ListViewModel extends AndroidViewModel {

    private MutableLiveData<String> title = new MutableLiveData<>();
    private LiveData<List<Term>> terms = new MutableLiveData<>();
    private LiveData<List<Course>> courses;
    private LiveData<List<Assessment>> assessments = new MutableLiveData<>();
    private LiveData<List<Instructor>> instructors = new MutableLiveData<>();
    private LiveData<List<Course>> coursesByTermId = new MutableLiveData<>();
    private TermRepository termRepository;
    private CourseRepository courseRepository;
    private AssessmentRepository assessmentRepository;
    private InstructorRepository instructorRepository;
    private int selectedItemPosition;
    private int termId = -1;

    public ListViewModel(Application application) {
        super(application);
        termRepository = new TermRepository(application);
        courseRepository = new CourseRepository(application);
        assessmentRepository = new AssessmentRepository(application);
        instructorRepository = new InstructorRepository(application);

        terms = termRepository.getAllTerms();
        courses = courseRepository.getAllCourses();
        assessments = assessmentRepository.getAllAssessments();
        instructors = instructorRepository.getAllInstructors();
        coursesByTermId = courseRepository.getCoursesByTermId();

        loadData();
    }

    public void setTermId (int termId) {
        this.termId = termId;
        loadMatchingCourses(); //reload data when changed
    }

    public void setSelectedItemId(int selectedItemId) {
        this.selectedItemPosition = selectedItemId;
        loadData(); //reload data when changed
    }

    public LiveData<String> getTitle() {
        return title;
    }

    public LiveData<List<Term>> getTerms() {
        return terms;
    }

    public LiveData<List<Course>> getCourses() {
        return courses;
    }

    public LiveData<List<Assessment>> getAssessments() {
        return assessments;
    }

    public LiveData<List<Instructor>> getInstructors() {
        return instructors;
    }

    public LiveData<List<Course>> getCoursesByTermId() {
        return coursesByTermId;
    }

    public void loadData() {
        if(termId != -1) {
            loadMatchingCourses();
        }
        else {
            switch (selectedItemPosition) {
                case 0:
                    loadTerms();
                    break;
                case 1:
                    loadInstructors();
                    break;
                case 2:
                    loadCourses();
                    break;
                case 3:
                    loadAssessments();
                    break;
            }
        }
    }

    private void loadTerms() {
        title.setValue(getApplication().getString(R.string.terms_title));
    }

    private void loadCourses() {
        title.setValue(getApplication().getString(R.string.courses_title));
    }

    private void loadAssessments() {
        title.setValue(getApplication().getString(R.string.assessments_title));
    }

    private void loadInstructors() {
        title.setValue(getApplication().getString(R.string.instructors_title));
    }

    private void loadMatchingCourses() {
        courseRepository.loadCoursesByTermId(termId);
        title.setValue("Courses");
    }
}