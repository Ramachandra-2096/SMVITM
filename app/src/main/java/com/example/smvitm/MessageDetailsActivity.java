package com.example.smvitm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MessageDetailsActivity extends AppCompatActivity {

    private TextView messageContentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);

        messageContentTextView = findViewById(R.id.messageContentTextView);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("messageContent")) {
            String messageContent = intent.getStringExtra("messageContent");
            messageContentTextView.setText(messageContent);
        }
    }
}
