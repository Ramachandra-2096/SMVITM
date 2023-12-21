package com.example.smvitm;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

public class PdfViewActivity extends AppCompatActivity {

    private PDFView pdfView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);

        pdfView = findViewById(R.id.pdfView);
        // Get the PDF URL from the intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("pdfUrl")) {
            String pdfUrl = intent.getStringExtra("pdfUrl");
            loadPdf(pdfUrl);
        } else {
            Toast.makeText(this, "PDF URL not provided", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadPdf(String pdfUrl) {
        Uri uri = Uri.parse(pdfUrl);
        Log.d("TAG", "loadPdf: "+pdfUrl);
        pdfView.fromUri(uri).load();
    }

}
