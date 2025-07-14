package com.example.myapplication.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.myapplication.datamodel.Term;
import com.example.myapplication.repository.TermRepository;

public class TermFormViewModel extends AndroidViewModel {
    private MutableLiveData<Term> termDetails = new MutableLiveData<>();
    private TermRepository termRepository;

    public TermFormViewModel(Application application) {
        super(application);
        termRepository = new TermRepository(application);
    }

    public LiveData<Term> getTermDetails() {
        return termDetails;
    }

    public void loadTerm(int termId) {
        if (termId != -1) {
            termRepository.getTermById(termId).observeForever(term -> {
                termDetails.setValue(term);
            });
        } else {
            termDetails.setValue(null);
        }
    }
}
