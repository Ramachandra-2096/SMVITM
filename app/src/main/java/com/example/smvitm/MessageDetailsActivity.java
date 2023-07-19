package com.example.smvitm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MessageDetailsActivity extends AppCompatActivity {

    private TextView senderTextView;
    private TextView messageContentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);

        senderTextView = findViewById(R.id.senderTextView);
        messageContentTextView = findViewById(R.id.messageContentTextView);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("selectedMessage") && intent.hasExtra("userId")) {
            Message selectedMessage = (Message) intent.getSerializableExtra("selectedMessage");
            String userId = intent.getStringExtra("userId");

            // Update the TextViews with message details
            senderTextView.setText(selectedMessage.getSender());
            messageContentTextView.setText(selectedMessage.getContent());

            // Mark the message as read (if it's not already read)
            if (!selectedMessage.isRead()) {
                selectedMessage.setRead(true);

                // Update the message's "isRead" status in the Firebase database
                DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("users")
                        .child(userId).child("Messages").child(selectedMessage.getKey());
                messageRef.child("isRead").setValue(true);
            }
        }
    }
}
