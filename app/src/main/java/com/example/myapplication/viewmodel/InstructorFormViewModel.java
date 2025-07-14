package com.example.myapplication.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.datamodel.Instructor;
import com.example.myapplication.repository.InstructorRepository;

public class InstructorFormViewModel extends AndroidViewModel {
    private MutableLiveData<Instructor> instructorDetails = new MutableLiveData<>();
    private InstructorRepository instructorRepository;

    public InstructorFormViewModel(Application application) {
        super(application);
        instructorRepository = new InstructorRepository(application);
    }

    public LiveData<Instructor> getInstructorDetails() {
        return instructorDetails;
    }
    public void loadInstructor(int instructorId) {
        if (instructorId != -1) {
            instructorRepository.getInstructorById(instructorId).observeForever(instructor -> {
                instructorDetails.setValue(instructor);
            });
        } else {
            instructorDetails.setValue(null);
        }
    }
}
