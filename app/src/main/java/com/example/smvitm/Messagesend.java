package com.example.smvitm;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Messagesend extends AppCompatActivity {
    private TextView t;
    private EditText messageEditText;
    private Button sendMessageButton;
    private String targetUserUSN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagesend);

        t = findViewById(R.id.textView);
        messageEditText = findViewById(R.id.messageEditText);
        sendMessageButton = findViewById(R.id.sendMessageButton);

        Intent intent = getIntent();
        targetUserUSN = intent.getStringExtra("USN");

        t.setText(targetUserUSN);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString().trim();
                if (!message.isEmpty()) {
                    sendMessageToUser(targetUserUSN, message);
                }
            }
        });
    }

    private void sendMessageToUser(String targetUSN, String messageContent) {
        // Create a new message node in the Messages section of the database
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference().child("users").child(targetUSN).child("Messages");
        DatabaseReference newMessageRef = messagesRef.push();
        String messageKey = newMessageRef.getKey();

        // Create a Message object with the message content and other required fields
        Message message = new Message(messageContent, "Your_Sender_Name", false,messageKey);

        // Set the message object in the database under the new message node
        newMessageRef.setValue(message);

        // Clear the messageEditText after sending the message
        messageEditText.setText("Message Sent!");
    }
}
