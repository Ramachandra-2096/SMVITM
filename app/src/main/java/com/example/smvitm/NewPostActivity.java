package com.example.smvitm;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity {

    private EditText postContentEditText;
    private Button submitPostButton;
    private Button attachDocumentButton;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private Uri selectedDocumentUri;
    private static final int PICK_DOCUMENT_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        postContentEditText = findViewById(R.id.postContentEditText);
        submitPostButton = findViewById(R.id.submitPostButton);
        attachDocumentButton = findViewById(R.id.addAttachmentButton);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        attachDocumentButton.setOnClickListener(v -> openDocumentPicker());

        submitPostButton.setOnClickListener(v -> {
            String postContent = postContentEditText.getText().toString().trim();

            if (TextUtils.isEmpty(postContent)) {
                Toast.makeText(this, "Please enter post content", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();

                Map<String, Object> postData = new HashMap<>();
                postData.put("content", postContent);
                postData.put("userId", userId);
                postData.put("timestamp", System.currentTimeMillis() / 1000);

                // Upload the document and save the post with document URL
                uploadDocumentAndSavePost(postData);
            } else {
                Toast.makeText(this, "You are not authenticated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openDocumentPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_DOCUMENT_REQUEST_CODE);
    }

    private void uploadDocumentAndSavePost(Map<String, Object> postData) {
        if (selectedDocumentUri != null) {
            String postId = firestore.collection("posts").document().getId();

            StorageReference storageRef = storage.getReference().child("documents").child(postId);
            storageRef.putFile(selectedDocumentUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // File upload success, now get the download URL
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Add the file URL to postData
                            postData.put("fileUrl", uri.toString());

                            // Save the post data to Firestore
                            savePostToFirestore(postData, postId);
                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to retrieve download URL", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to upload document", Toast.LENGTH_SHORT).show();
                    });
        } else {
            String postId = firestore.collection("posts").document().getId();
            savePostToFirestore(postData, postId);
        }
    }




    private void savePostToFirestore(Map<String, Object> postData, String postId) {
        CollectionReference postsCollection = firestore.collection("posts");
        DocumentReference postDocumentRef = postsCollection.document(postId);

        postDocumentRef.set(postData)
                .addOnSuccessListener(Void -> {
                    Toast.makeText(this, "Post created successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(NewPostActivity.this, Home.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to create post", Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_DOCUMENT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedDocumentUri = data.getData();
        }
    }
}
