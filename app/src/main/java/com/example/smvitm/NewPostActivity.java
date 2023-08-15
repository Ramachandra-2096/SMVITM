package com.example.smvitm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity {

    private EditText postContentEditText;
    private Button submitPostButton;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        postContentEditText = findViewById(R.id.postContentEditText);
        submitPostButton = findViewById(R.id.submitPostButton);

        firestore = FirebaseFirestore.getInstance();

        submitPostButton.setOnClickListener(v -> {
            String postContent = postContentEditText.getText().toString();
            String userId = "user_id"; // Get the current user's ID

            Map<String, Object> postData = new HashMap<>();
            postData.put("content", postContent);
            postData.put("Admin", userId);
            postData.put("timestamp", System.currentTimeMillis());

            CollectionReference postsCollection = firestore.collection("posts");
            postsCollection.add(postData)
                    .addOnSuccessListener(documentReference -> {
                        startActivity(new Intent(NewPostActivity.this, Home.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Faild", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
