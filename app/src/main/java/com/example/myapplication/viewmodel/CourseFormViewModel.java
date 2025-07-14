package com.example.myapplication.viewmodel;

import android.app.Application;

import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myapplication.R;
import com.example.myapplication.datamodel.Course;
import com.example.myapplication.datamodel.Instructor;
import com.example.myapplication.datamodel.Term;
import com.example.myapplication.repository.CourseRepository;
import com.example.myapplication.repository.InstructorRepository;
import com.example.myapplication.repository.TermRepository;

import java.util.List;
import java.util.stream.Collectors;

public class CourseFormViewModel extends AndroidViewModel {
    private MutableLiveData<String[]> spinnerItems = new MutableLiveData<>();
    private MutableLiveData<Course> courseDetails = new MutableLiveData<>();
    private LiveData<List<Term>> allTerms;
    private LiveData<List<String>> termTitles;
    private MutableLiveData<String> selectedTermTitle = new MutableLiveData<>();
    private LiveData<List<Instructor>> allInstructors;
    private LiveData<List<String>> instructorNames;
    private MutableLiveData<String> selectedInstructorName = new MutableLiveData<>();
    private CourseRepository courseRepository;
    private TermRepository termRepository;
    private InstructorRepository instructorRepository;

    public CourseFormViewModel(Application application) {
        super(application);
        courseRepository = new CourseRepository(application);
        instructorRepository = new InstructorRepository(application);
        termRepository = new TermRepository(application);
        loadSpinnerItems();
        loadAllTerms(); // Load LiveData of Course objects
        transformTermsToTitles(); // Transform to LiveData of titles
        loadAllInstructors();
        transformInstructorsToNames();
    }
    public MutableLiveData<String[]> getSpinnerItems() {
        return spinnerItems;
    }

    public LiveData<Course> getCourseDetails() {
        return courseDetails;
    }

    public LiveData<List<String>> getTermTitles() {
        return termTitles;
    }

    public LiveData<String> getSelectedTermTitle() {
        return selectedTermTitle;
    }

    public LiveData<List<String>> getInstructorNames() {
        return instructorNames;
    }
    public LiveData<String> getSelectedInstructorName() {
        return selectedInstructorName;
    }

    public void loadCourse(int courseId) {
        if (courseId != -1) {
            courseRepository.getCourseById(courseId).observeForever(course -> {
                courseDetails.setValue(course);
                // Fetch the associated term title
                termRepository.getTermById(course.getTermId()).observeForever(term -> {
                    if (term != null) {
                        selectedTermTitle.setValue(term.getTitle());
                    } else {
                        selectedTermTitle.setValue(null);
                    }
                });
                instructorRepository.getInstructorById(course.getInstructorId()).observeForever(instructor -> {
                    if (instructor != null) {
                        selectedInstructorName.setValue(instructor.getName());
                    } else {
                        selectedInstructorName.setValue(null);
                    }
                });
            });
        } else {
            courseDetails.setValue(null);
            selectedTermTitle.setValue(null);
            selectedInstructorName.setValue(null);
        }
    }

    private void loadSpinnerItems() {
        String[] items = getApplication().getResources().getStringArray(R.array.status_spinner_items);
        spinnerItems.setValue(items);
    }

    private void loadAllTerms() {
        allTerms = termRepository.getAllTerms();
    }

    public void loadAllInstructors() {
        allInstructors = instructorRepository.getAllInstructors();
    }

    public void loadTermTitleForCourse(int termId) {
        termRepository.getTermById(termId).observeForever(term -> {
            if (term != null) {
                selectedTermTitle.setValue(term.getTitle());
            }
        });
    }

    public void loadInstructorNameForCourse(int instructorId) {
        instructorRepository.getInstructorById(instructorId).observeForever(instructor ->{
            if (instructor != null) {
                selectedInstructorName.setValue(instructor.getName());
            }
        });
    }

    private void transformTermsToTitles() {
        termTitles = Transformations.map(allTerms, courses -> {
            if (courses != null) {
                return courses.stream().map(Term::getTitle).collect(Collectors.toList());
            } else {
                return null;
            }
        });
    }
    public void transformInstructorsToNames() {
        instructorNames = Transformations.map(allInstructors, courses -> {
            if(courses != null) {
                return courses.stream().map(Instructor::getName).collect(Collectors.toList());
            } else {
                return null;
            }
        });
    }
}
