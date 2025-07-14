package com.example.myapplication.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.myapplication.R;

public class MainActivityViewModel extends AndroidViewModel {

    private MutableLiveData<String[]> spinnerItems = new MutableLiveData<>();

    public MainActivityViewModel(Application application) {
        super(application);
        loadSpinnerItems();
    }

    public MutableLiveData<String[]> getSpinnerItems() {
        return spinnerItems;
    }

    private void loadSpinnerItems() {
        String[] items = getApplication().getResources().getStringArray(R.array.spinner_items);
        spinnerItems.setValue(items);
    }
}