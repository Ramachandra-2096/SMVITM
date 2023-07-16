package com.example.smvitm.ui.exam;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ExamViewModel extends ViewModel {
    private MutableLiveData<String> text;

    public ExamViewModel() {
        text = new MutableLiveData<>();
        text.setValue("This is the exam fragment");
    }

    public LiveData<String> getText() {
        return text;
    }
}
