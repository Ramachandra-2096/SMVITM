package com.example.smvitm.ui.gallery;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.smvitm.R;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private WebView webView;
    private List<String> urlHistory;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        webView = root.findViewById(R.id.webViewGallery);
        urlHistory = new ArrayList<>();

        galleryViewModel.getUrl().observe(getViewLifecycleOwner(), url -> {
            // Configure WebView settings
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            // Load the URL
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl(url);

            // Add the URL to the history
            urlHistory.add(url);
        });

        root.setFocusableInTouchMode(true);
        root.requestFocus();
        root.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                // Check if the current URL is the last URL in the history
                if (urlHistory.size() > 1 && webView.getUrl().equals(urlHistory.get(urlHistory.size() - 1))) {
                    // Remove the current URL from the history
                    urlHistory.remove(urlHistory.size() - 1);

                    // Load the previous URL
                    String previousUrl = urlHistory.get(urlHistory.size() - 1);
                    webView.loadUrl(previousUrl);
                    return true;
                }

                // Go back to the previous page
                webView.goBack();
                return true;
            }
            return false;
        });

        return root;
    }
}
