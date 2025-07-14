package com.example.myapplication.viewmodel;

import android.app.Application;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myapplication.datamodel.Instructor;
import com.example.myapplication.repository.InstructorRepository;

public class InstructorDetailViewModel extends AndroidViewModel {
    private MutableLiveData<Integer> instructorId = new MutableLiveData<>();
    private LiveData<Instructor> instructor;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private InstructorRepository instructorRepository;
    public InstructorDetailViewModel(Application application) {
        super(application);

        instructorRepository = new InstructorRepository(application);
        instructor = Transformations.switchMap(instructorId, id -> {
            isLoading.postValue(true);
            return instructorRepository.getInstructorById(id);
        });

    }
    public LiveData<Instructor> getInstructor() {
        return instructor;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadInstructor(int id) {
        instructorId.setValue(id);
    }
}
