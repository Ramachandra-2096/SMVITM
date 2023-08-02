package com.example.smvitm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class Messagesend extends AppCompatActivity {
    private EditText messageEditText;
    private String targetUserUSN;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagesend);

        TextView t = findViewById(R.id.textView);
        messageEditText = findViewById(R.id.messageEditText);
        Button sendMessageButton = findViewById(R.id.sendMessageButton);

        Intent intent = getIntent();
        targetUserUSN = intent.getStringExtra("USN");

        t.setText(targetUserUSN);

        sendMessageButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessageToUser(targetUserUSN, message);
            }
        });
    }

    private void sendMessageToUser(final String targetUSN, final String messageContent) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        usersRef.orderByChild("USN").equalTo(targetUSN).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();

                    FirebaseUser currentUser1 = mAuth.getCurrentUser();
                    String userId1 = Objects.requireNonNull(currentUser1).getUid();
                    DatabaseReference usersRef1 = FirebaseDatabase.getInstance().getReference().child("users").child(userId1);

                    usersRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                            if (dataSnapshot1.exists()) {
                                // The user data exists in the database
                                String userName = dataSnapshot1.child("Name").getValue(String.class);
                                // Call the method to send the message inside this onDataChange
                                sendMessageWithUserName(userId, userName, messageContent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void sendMessageWithUserName(String userId, String userName, String messageContent) {
        // Create a new message node in the Messages section of the database
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("Messages");
        DatabaseReference newMessageRef = messagesRef.push();
        String messageKey = newMessageRef.getKey();

        // Create a Message object with the message content and other required fields
        Message message = new Message(messageContent, userName, false, messageKey);
        newMessageRef.setValue(message);

        Toast.makeText(this, "Message sent ", Toast.LENGTH_SHORT).show();
    }


}
