package com.example.smvitm;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

public class ImageViewModel extends ViewModel {
    private Uri selectedImageUri;

    public Uri getSelectedImageUri() {
        return selectedImageUri;
    }

    public void setSelectedImageUri(Uri selectedImageUri) {
        this.selectedImageUri = selectedImageUri;
    }
}
