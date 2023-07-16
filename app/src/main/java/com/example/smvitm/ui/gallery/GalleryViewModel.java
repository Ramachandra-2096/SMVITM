package com.example.smvitm.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GalleryViewModel extends ViewModel {

    private MutableLiveData<String> url;

    public GalleryViewModel() {
        url = new MutableLiveData<>();
        url.setValue("https://sode-edu.in/");
    }

    public void setUrl(String urlString) {
        url.setValue(urlString);
    }

    public LiveData<String> getUrl() {
        return url;
    }
}
