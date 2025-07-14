package com.example.myapplication.viewmodel;

import android.app.Application;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myapplication.datamodel.Course;
import com.example.myapplication.datamodel.Term;
import com.example.myapplication.repository.CourseRepository;
import com.example.myapplication.repository.TermRepository;

public class TermDetailViewModel extends AndroidViewModel {
    private Application application;
    private MutableLiveData<Integer> termId = new MutableLiveData<>();
    private LiveData<Term> term;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private TermRepository termRepository;
    public TermDetailViewModel(Application application) {
        super(application); // Call super constructor
        this.application = application; // Initialize the application field
        termRepository = new TermRepository(application);

        term = Transformations.switchMap(termId, id -> {
            isLoading.postValue(true);
            return termRepository.getTermById(id);
        });
    }

    public LiveData<Term> getTerm() {
        return term;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadTerm(int id) {
        termId.setValue(id);
    }

}
