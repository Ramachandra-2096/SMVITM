package com.example.smvitm;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CompleteMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_message);

        TextView messageTextView = findViewById(R.id.messageTextView);

        // Get the selected message from the intent
        Message selectedMessage = (Message) getIntent().getSerializableExtra("selectedMessage");

        // Display the complete message
        if (selectedMessage != null) {
            messageTextView.setText(selectedMessage.getContent());
        }
    }
}
