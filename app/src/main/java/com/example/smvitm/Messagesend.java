package com.example.smvitm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Messagesend extends AppCompatActivity {
    private TextView t;
    private EditText messageEditText;
    private Button sendMessageButton;
    private String targetUserUSN,userName;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

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

    private void sendMessageToUser(final String targetUSN, final String messageContent) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        // Query the database to find the user with the matching USN
        usersRef.orderByChild("USN").equalTo(targetUSN).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();

                    FirebaseUser currentUser1 = mAuth.getCurrentUser();
                    String userId1 = currentUser1.getUid();
                    DatabaseReference usersRef1 = FirebaseDatabase.getInstance().getReference().child("users").child(userId1);

                    usersRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot1) {
                            if (dataSnapshot1.exists()) {
                                // The user data exists in the database
                                String userName = dataSnapshot1.child("Name").getValue(String.class);
                                // Call the method to send the message inside this onDataChange
                                sendMessageWithUserName(userId, userName, messageContent);
                            } else {
                                // Handle the case where the user data doesn't exist in the database
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle database error, if any
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error, if any
            }
        });
    }

    // Create a separate method to send the message with the userName
    private void sendMessageWithUserName(String userId, String userName, String messageContent) {
        // Create a new message node in the Messages section of the database
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("Messages");
        DatabaseReference newMessageRef = messagesRef.push();
        String messageKey = newMessageRef.getKey();

        // Create a Message object with the message content and other required fields
        Message message = new Message(messageContent, userName, false, messageKey);

        // Set the message object in the database under the new message node
        newMessageRef.setValue(message);

        // Clear the messageEditText after sending the message
        messageEditText.setText("Message Sent!");
    }


}
